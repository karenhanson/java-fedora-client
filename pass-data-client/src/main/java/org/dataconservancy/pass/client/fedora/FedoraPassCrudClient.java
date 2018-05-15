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

import java.io.ByteArrayInputStream;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpStatus;

import org.dataconservancy.pass.client.PassJsonAdapter;
import org.dataconservancy.pass.client.adapter.PassJsonAdapterBasic;
import org.dataconservancy.pass.model.PassEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fcrepo.client.DeleteBuilder;
import org.fcrepo.client.FcrepoClient;
import org.fcrepo.client.FcrepoOperationFailedException;
import org.fcrepo.client.FcrepoResponse;
import org.fcrepo.client.GetBuilder;

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
     * A JSON adapter for PASS 
     */
    private PassJsonAdapter adapter;

    public FedoraPassCrudClient() {
        client = FcrepoClient.client()
                .credentials(FedoraConfig.getUserName(), FedoraConfig.getPassword())
                .throwExceptionOnFailure()
                .build();
        adapter = new PassJsonAdapterBasic();
    }

    /** 
     * Support passing in of Fedora client and adapter
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
    }
    
    /**
     * @see org.dataconservancy.pass.client.PassClient#createResource(PassEntity)
     */
    public URI createResource(PassEntity modelObj) {
        URI container = null;
        URI newId = null;
        
        try {            
            container = new URI(FedoraConfig.getContainer(modelObj.getClass().getSimpleName()));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Container name could not be converted to a URI", e);
        }
        
        byte[] json = adapter.toJson(modelObj, true);
        InputStream jsonIS = new ByteArrayInputStream(json);
                
        try (FcrepoResponse response = client.post(container)
                .body(jsonIS, JSONLD_CONTENTTYPE)
                .perform()) {
            newId = response.getLocation();
            LOG.info("Container creation status and location: {}, {}", response.getStatusCode(), newId);
        } catch (FcrepoOperationFailedException | IOException e) {
            throw new RuntimeException("A problem occurred while attempting to create a new Resource", e);
        } 
        return newId;
    }

    /**
     * @see org.dataconservancy.pass.client.PassClient#updateResource(PassEntity)
     */
    public void updateResource(PassEntity modelObj) {
        byte[] json = adapter.toJson(modelObj, true);
        InputStream jsonIS = new ByteArrayInputStream(json);
        
        PatchBuilderExtension patchbuilder = new PatchBuilderExtension(modelObj.getId(), client);
        try (FcrepoResponse response = patchbuilder
                            .body(jsonIS, JSONLD_PATCH_CONTENTTYPE)
                            .ifMatch(modelObj.getVersionTag())
                            .perform()) {

            LOG.info("Container update status and location: {}, {}", response.getStatusCode(), modelObj.getId());
        } catch (FcrepoOperationFailedException e) {
            if (e.getStatusCode()==HttpStatus.SC_PRECONDITION_FAILED) {
                throw new UpdateConflictException("Failed to update. The data may have changed since the object was last retrieved.", e);
            } else {
                throw new RuntimeException("A problem occurred while attempting to update a Resource", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("A problem occurred while attempting to update a Resource", e);
        }
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
    
}
