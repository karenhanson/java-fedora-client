/*
 * Copyright 2018 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dataconservancy.pass.client.fedora;

import java.io.IOException;
import java.io.InputStream;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import org.apache.http.HttpStatus;

import org.dataconservancy.pass.client.PassClientDefault;
import org.dataconservancy.pass.client.PassJsonAdapter;
import org.dataconservancy.pass.client.adapter.PassJsonAdapterBasic;
import org.dataconservancy.pass.model.PassEntity;
import org.fcrepo.client.PostBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fcrepo.client.DeleteBuilder;
import org.fcrepo.client.FcrepoClient;
import org.fcrepo.client.FcrepoOperationFailedException;
import org.fcrepo.client.FcrepoResponse;
import org.fcrepo.client.GetBuilder;

import static java.lang.String.format;
import static java.util.Base64.getEncoder;

import static org.dataconservancy.pass.client.fedora.RepositoryCrawler.Ignore.IGNORE_CONTAINERS;
import static org.dataconservancy.pass.client.fedora.RepositoryCrawler.Skip.depth;
import static org.dataconservancy.pass.client.fedora.RepositoryCrawler.Skip.SKIP_ACLS;

/**
 * Fedora CRUD client does basic work of creating, retrieving, updating, and deleting
 * records in Fedora based on model and/or URI provided.
 * @author Karen Hanson
 */
public class FedoraPassCrudClient {

    private static final Logger LOG = LoggerFactory.getLogger(FedoraPassCrudClient.class);

    private final static String JSONLD_CONTENTTYPE = "application/ld+json; charset=utf-8";
    private final static String JSONLD_PATCH_CONTENTTYPE = "application/merge-patch+json; charset=utf-8";
    private final static String SERVER_MANAGED_OMITTYPE = "http://fedora.info/definitions/v4/repository#ServerManaged";
    private final static String COMPACTED_ACCEPTTYPE = "application/ld+json";
    private final static String INCOMING_INCLUDETYPE = "http://fedora.info/definitions/v4/repository#InboundReferences";
    private final static String ETAG_HEADER = "ETag";
    private final static String ETAG_WEAK_PREFIX = "W/";
    
    /** 
     * The Fedora client tool 
     */
    private FcrepoClient client;

    /**
     * OkHttp client, for when using HTTP is desired
     */
    private OkHttpClient okHttpClient;

    /**
     * A JSON adapter for PASS 
     */
    private PassJsonAdapter adapter;
    
    /**
     * Crawls the repository
     */
    private RepositoryCrawler crawler = new RepositoryCrawler();

    /**
     * Instantiates default implementations of the underlying Fedora client, JSON adapter, and OkHttpClient.
     */
    public FedoraPassCrudClient() {
        this(FcrepoClient.client()
                .credentials(FedoraConfig.getUserName(), FedoraConfig.getPassword())
                .throwExceptionOnFailure()
                .build(),
             new PassJsonAdapterBasic());
    }

    /** 
     * Support passing in of Fedora client and adapter.  Instantiates a default OkHttpClient.
     * @param client
     * @param adapter
     */
    public FedoraPassCrudClient(FcrepoClient client, PassJsonAdapter adapter) {
        if (client == null) {
            throw new IllegalArgumentException("client parameter cannot be null");
        }
        if (adapter == null) {
            throw new IllegalArgumentException("adapter parameter cannot be null");
        }
        this.client = client;
        this.adapter = adapter;

        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();

        if (FedoraConfig.getUserName() != null) {
            okBuilder.addInterceptor((requestChain) -> {
                Request request = requestChain.request();
                LOG.trace("Adding 'Authorization' header for communication with {}", FedoraConfig.getBaseUrl());
                Request.Builder reqBuilder = request.newBuilder();
                byte[] bytes = format("%s:%s",
                        FedoraConfig.getUserName(), FedoraConfig.getPassword()).getBytes();
                return requestChain.proceed(reqBuilder.addHeader("Authorization",
                        "Basic " + getEncoder().encodeToString(bytes)).build());
            });
        }

        if (LOG.isDebugEnabled()) {
            Interceptor loggingInterceptor = new HttpLoggingInterceptor(LOG::debug);
            okBuilder.addInterceptor(loggingInterceptor);
        }

        String userAgent = System.getProperty("http.agent");
        if (userAgent != null) {
            LOG.trace("Adding 'User-Agent' header with value: {}", userAgent);
            okBuilder.addInterceptor((requestChain) -> {
                Request.Builder reqBuilder = requestChain.request().newBuilder();
                reqBuilder.removeHeader("User-Agent");
                reqBuilder.addHeader("User-Agent", userAgent);
                return requestChain.proceed(reqBuilder.build());
            });
        }

        this.okHttpClient = okBuilder.build();
    }

    /**
     * Support passing in of Fedora client, JSON adapter, and OkHttpClient
     * @param client
     * @param adapter
     * @param okHttpClient
     */
    public FedoraPassCrudClient(FcrepoClient client, PassJsonAdapter adapter, OkHttpClient okHttpClient) {
        if (client == null) {
            throw new IllegalArgumentException("client parameter cannot be null");
        }
        if (adapter == null) {
            throw new IllegalArgumentException("adapter parameter cannot be null");
        }
        if (client == null) {
            throw new IllegalArgumentException("okhttpclient parameter cannot be null");
        }
        this.client = client;
        this.adapter = adapter;
        this.okHttpClient = okHttpClient;
    }

    /**
     * @see org.dataconservancy.pass.client.PassClient#createResource(PassEntity)
     */
    public URI createResource(PassEntity modelObj) {
        return createInternal(modelObj, true).getId();
    }

    /**
     * @see org.dataconservancy.pass.client.PassClient#createResource(PassEntity)
     */
    public <T extends PassEntity> T createAndReadResource(T modelObj, Class<T> modelClass) {
        return createInternal(modelObj, true);
    }

    /**
     * @see org.dataconservancy.pass.client.PassClient#updateResource(PassEntity)
     */
    public void updateResource(PassEntity modelObj) {
        updateInternal(modelObj, true, false);
    }

    public <T extends PassEntity> T updateAndReadResource(T modelObj, Class<T> modelClass) {
        return updateInternal(modelObj, true, true);
    }

    /**
     * @see org.dataconservancy.pass.client.PassClient#deleteResource(URI)
     */
    public void deleteResource(URI uri) {
        try (FcrepoResponse response = new DeleteBuilder(uri, client).perform()) {
            LOG.info("Resource deletion status: {}", response.getStatusCode());
        } catch (IOException | FcrepoOperationFailedException e) {
            throw new RuntimeException("A problem occurred while attempting to delete a Resource", e);
        }
    }

    /**
     * @see org.dataconservancy.pass.client.PassClient#readResource(URI, Class)
     */
    public <T extends PassEntity> T readResource(URI uri, Class<T> modelClass) {      

        List<URI> omits = new ArrayList<URI>();
        try {
            omits.add(new URI(SERVER_MANAGED_OMITTYPE));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not create required \"omit\" type.", e);
        }
        
        try (FcrepoResponse response = new GetBuilder(uri, client)
                .accept(COMPACTED_ACCEPTTYPE)
                .preferRepresentation(null, omits)
                .perform()) {

          LOG.info("Resource read status: {}", response.getStatusCode());
          T model = adapter.toModel(response.getBody(), modelClass);
          
          //remove the etag prefix, not needed for version comparison
          String etag = response.getHeaderValue(ETAG_HEADER);
          if (etag!=null && etag.contains(ETAG_WEAK_PREFIX)) {
              etag = etag.replace(ETAG_WEAK_PREFIX, "");
          }
          model.setVersionTag(etag);
          
          return model;
          
        } catch (IOException | FcrepoOperationFailedException e) {
            throw new RuntimeException("A problem occurred while attempting to read a Resource", e);
        }        
    }

    /**
     * @see org.dataconservancy.pass.client.PassClient#getIncoming(URI)
     */
    public Map<String, Collection<URI>> getIncoming(URI passEntityUri) {
        List<URI> include = Collections.singletonList(URI.create(INCOMING_INCLUDETYPE));
        List<URI> omits = Collections.singletonList(URI.create(SERVER_MANAGED_OMITTYPE));

        try (FcrepoResponse response = new GetBuilder(passEntityUri, client)
                .accept(COMPACTED_ACCEPTTYPE)
                .preferRepresentation(include, omits)
                .perform()) {

            LOG.info("Resource read status: {}", response.getStatusCode());

            JsonNode raw = new ObjectMapper().readTree(response.getBody());
            JsonNode graph = raw.withArray("@graph");

            if (graph == null || graph.size() < 1) {
                return Collections.emptyMap();
            }

            Map<String, Collection<URI>> result = new ConcurrentHashMap<>();

            graph.elements().forEachRemaining((node) -> {
                if (!node.has("@id")) {
                    return;
                }

                URI incomingLink = URI.create(node.get("@id").asText());

                // Filter out any nodes in the graph that refer to the requested PASS entity
                // Remaining nodes in the graph are incoming links
                if (passEntityUri.toString().equals(incomingLink.toString())) {
                    return;
                }

                node.fieldNames().forEachRemaining(field -> {
                    if ("@id".equals(field)) {
                        return;
                    }

                    Collection<URI> uris = result.getOrDefault(field, new HashSet<>());
                    uris.add(incomingLink);
                    result.putIfAbsent(field, uris);
                });
            });

            return result;

        } catch (IOException | FcrepoOperationFailedException e) {
            throw new RuntimeException("A problem occurred while attempting to read a Resource", e);
        }
    }

    /**
     * @see PassClientDefault#upload(URI, InputStream, Map)
     *
     * @throws RuntimeException if building the request to the repository fails, or if performing the request fails
     */
    public URI upload(URI passEntityUri, InputStream content, Map<String, ?> params) {
        PostBuilder builder = new PostBuilder(passEntityUri, client);

        if (params.containsKey("content-type")) {
            builder.body(content, (String) params.get("content-type"));
        } else {
            builder.body(content);
        }

        if (params.containsKey("slug")) {
            builder.slug((String)params.get("slug"));
        }

        if (params.containsKey("sha256")) {
            builder.digestSha256((String) params.get("sha256"));
        }

        if (params.containsKey("md5")) {
            builder.digestMd5((String) params.get("md5"));
        }

        if (params.containsKey("sha1")) {
            builder.digestSha1((String) params.get("sha1"));
        }

        if (params.containsKey("filename")) {
            try {
                builder.filename((String) params.get("filename"));
            } catch (FcrepoOperationFailedException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        try (FcrepoResponse response = builder.perform()) {
            return response.getLocation();
        } catch (Exception e) {
            throw new RuntimeException("An problem occurred while POSTing binary content to Resource " +
                    passEntityUri + ": " + e.getMessage(), e);
        }
    }
    
    public <T extends PassEntity> int processAllEntities(Consumer<URI> processor, Class<T> modelClass) {
        if (modelClass == null) {
            return crawler.visit(
                    URI.create(FedoraConfig.getBaseUrl()), 
                    processor, 
                    IGNORE_CONTAINERS, 
                    depth(2).or(SKIP_ACLS));
        }

        URI container = null;
        try {
            container = new URI(FedoraConfig.getContainer(modelClass.getSimpleName()));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Container name could not be converted to a URI", e);
        }

        return crawler.visit(
                container,
                processor,
                IGNORE_CONTAINERS,
                depth(1).or(SKIP_ACLS));
    }

    private <T extends PassEntity> T createInternal(T modelObj, boolean includeContext) {
        byte[] json = adapter.toJson(modelObj, true);
        RequestBody body = RequestBody.create(MediaType.parse(JSONLD_CONTENTTYPE), json);

        URI container = null;
        try {
            container = new URI(FedoraConfig.getContainer(modelObj.getClass().getSimpleName()));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Container name could not be converted to a URI", e);
        }

        Request.Builder reqBuilder = new Request.Builder()
                .url(container.toString())
                .post(body)
                .addHeader("Accept", COMPACTED_ACCEPTTYPE)
                .addHeader("Prefer", "return=representation; omits=\"" + SERVER_MANAGED_OMITTYPE + "\"");

        try (Response res = okHttpClient.newCall(reqBuilder.build()).execute()) {
            handleNon2xx(modelObj, res);

            PassEntity entity = adapter.toModel(res.body().byteStream(), modelObj.getClass());
            LOG.info("Container creation status and location: {}, {}", res.code(), entity.getId());

            return (T) entity;
        } catch (Exception e) {
            throw new RuntimeException("A problem occurred while attempting to create a Resource: " +
                    e.getMessage(), e);
        }
    }

    private <T extends PassEntity> T updateInternal(T modelObj, boolean includeContext, boolean performRead) {
        byte[] json = adapter.toJson(modelObj, true);
        RequestBody body = RequestBody.create(MediaType.parse(JSONLD_PATCH_CONTENTTYPE), json);

        Request.Builder reqBuilder = new Request.Builder()
                .url(modelObj.getId().toString())
                .patch(body)
                .addHeader("Accept", COMPACTED_ACCEPTTYPE);

        if (modelObj.getVersionTag() != null) {
            reqBuilder.addHeader("If-Match", modelObj.getVersionTag());
        } else {
            LOG.warn("Executing PATCH without 'If-Match' header: a {}, id '{}' has a null 'version tag'",
                    modelObj.getClass().getName(), modelObj.getId());
        }

        try (Response res = okHttpClient.newCall(reqBuilder.build()).execute()) {
            if (res.code() == HttpStatus.SC_PRECONDITION_FAILED) {
                String msg = format("Failed to update %s - the data may have changed since %s was last retrieved.",
                        modelObj.getId(), modelObj.getId());
                throw new UpdateConflictException(msg);
            }
            handleNon2xx(modelObj, res);
        } catch (UpdateConflictException e) {
            throw e;
        } catch (Exception e) {
            String msg = format("A problem occurred while attempting to update Resource %s: %s ",
                    modelObj.getId(), e.getMessage());
            throw new RuntimeException(msg, e);
        }

        return performRead ? readResource(modelObj.getId(), (Class<T>) modelObj.getClass()) : null;
    }

    private static <T extends PassEntity> void handleNon2xx(T modelObj, Response res) throws IOException {
        if (res.code() < 200 || res.code() > 299) {
            String msg = format("Failed to update %s - unexpected status code %s: %s",
                    modelObj.getId(), res.code(), res.body().string());
            throw new RuntimeException(msg);
        }
    }

}
