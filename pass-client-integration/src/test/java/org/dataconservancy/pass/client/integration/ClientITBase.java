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

package org.dataconservancy.pass.client.integration;

import java.lang.reflect.Method;

import java.net.URI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import com.openpojo.reflection.PojoClass;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import org.dataconservancy.pass.client.fedora.FedoraConfig;
import org.junit.After;

import org.dataconservancy.pass.client.PassClient;
import org.dataconservancy.pass.client.PassClientFactory;
import org.dataconservancy.pass.model.Contributor;
import org.dataconservancy.pass.model.PassEntity;
import org.dataconservancy.pass.model.User;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.openpojo.reflection.impl.PojoClassFactory.enumerateClassesByExtendingType;

import static java.util.Base64.getEncoder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Base class for client ITs.
 *
 * @author apb@jhu.edu
 * @author Karen Hanson
 */
public abstract class ClientITBase {

    private static final Logger LOG = LoggerFactory.getLogger(ClientITBase.class);

    protected Map<URI, Class<? extends PassEntity>> createdUris = new HashMap<URI, Class<? extends PassEntity>>();
    
    protected static final int RETRIES = 12;
    
    static {
        if (System.getProperty("pass.fedora.baseurl") == null) {
            System.setProperty("pass.fedora.baseurl", "http://localhost:8080/fcrepo/rest/");
        }
        if (System.getProperty("pass.elasticsearch.url") == null) {
            System.setProperty("pass.elasticsearch.url", "http://localhost:9200/pass/");
        }
    }

    /** All PassEntity classes from pass-model */
    @SuppressWarnings("unchecked")
    protected static final Collection<Class<? extends PassEntity>> PASS_TYPES = enumerateClassesByExtendingType(
            "org.dataconservancy.pass.model", PassEntity.class, null)
                    .stream()
                    .map(PojoClass::getClazz)
                    .map(cls -> (Class<? extends PassEntity>) cls).collect(Collectors.toList());

    protected final PassClient client = PassClientFactory.getPassClient();

    private static final Random randomIndex = new Random();

    protected OkHttpClient okHttp;

    /**
     * Set up an {@code OkHttp} client, which can be used to perform HTTP calls (vs the Fedora client, which abstracts
     * a lot of HTTP away).
     */
    @Before
    public void okHttpSetup() {
        okHttp = new OkHttpClient();

        if (FedoraConfig.getUserName() != null) {
            OkHttpClient.Builder okBuilder = okHttp.newBuilder().addInterceptor(chain -> {
                Request request = chain.request();
                Request.Builder reqBuilder = request.newBuilder();
                byte[] bytes = String.format("%s:%s",
                        FedoraConfig.getUserName(), FedoraConfig.getPassword()).getBytes();
                return chain.proceed(reqBuilder
                        .addHeader("Authorization",
                                "Basic " + getEncoder().encodeToString(bytes)).build());
            });

            okHttp = okBuilder.build();
        }

        if (LOG.isDebugEnabled()) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            okHttp = okHttp.newBuilder().addInterceptor(loggingInterceptor).build();
        }
    }

    /**
     * Deletes resources listed in createdUris map, then waits for confirmation from indexer that they are processed
     * @throws InterruptedException
     */
    @After
    public void cleanUpResources() throws Exception {
        //need to log fail if this doesn't work as it could mess up re-testing if data isn't cleaned out
        try {
            if (createdUris.size()>0) {
                URI uriCheck = null;
                for (URI uri:createdUris.keySet()) {
                  client.deleteResource(uri);
                  uriCheck = uri;
                }          
                final URI finalUriCheck = uriCheck;
                attempt(RETRIES, () -> {
                    final URI uri = client.findByAttribute(createdUris.get(finalUriCheck), "@id", finalUriCheck);
                    assertEquals(null, uri);
                });
                createdUris.clear();
            }
        } catch (Exception ex) {
            fail("Could not clean up from test, this may interfere with results of other tests");
        }
    }
    
    /**
     * Normalize the context and ID so entities created locally can be compared with entities from the repository.
     *
     * @param e entity
     * @return Entity with context and ID normalized.
     */
    public static PassEntity normalized(PassEntity e) {
        try {
            final PassEntity cloned = (PassEntity) BeanUtils.cloneBean(e);
            cloned.setContext("");
            cloned.setId(URI.create(""));
            cloned.setVersionTag("");
            return cloned;
        } catch (final Exception x) {
            throw new RuntimeException(x);
        }
    }

    /**
     * Create an empty PASS entity
     *
     * @param obj Class of pass entity.
     * @return An instantiated, but empty, pass entity.
     */
    public static <T extends PassEntity> T empty(Class<T> obj) {
        try {
            return obj.newInstance();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create an instance of a Pass Entity, filled with random data.
     * <p>
     * Uses reflection to populate the fields with type-appropriate data.
     * </p>
     *
     * @param obj Class of pass entity.
     * @param sizeOfLists For fields that are lists, fill them with this many values.
     * @return Populated pass entity.
     */
    public static <T extends PassEntity> T random(Class<T> obj, int sizeOfLists) {
        try {
            final T entity = obj.newInstance();

            for (final Method m : obj.getMethods()) {
                if (m.getName().startsWith("set") && !m.getName().equals("setId") && !m.getName().equals("setVersionTag")) {
                    final Class<?> type = m.getParameterTypes()[0];

                    if (String.class.isAssignableFrom(type)) {
                        m.invoke(entity, UUID.randomUUID().toString());
                    } else if (Enum.class.isAssignableFrom(type)) {
                        final Object[] constants = type.getEnumConstants();
                        m.invoke(entity, constants[randomIndex.nextInt(constants.length)]);
                    } else if (URI.class.isAssignableFrom(type)) {
                        m.invoke(entity, URI.create("urn:uuid:" + UUID.randomUUID()));
                    } else if (DateTime.class.isAssignableFrom(type)) {
                        m.invoke(entity, new DateTime(randomIndex.nextInt(Integer.MAX_VALUE)).withZone(
                                DateTimeZone.UTC));
                    } else if (List.class.isAssignableFrom(type)) {
                        m.invoke(entity, createList(m, sizeOfLists));
                    } else if (Boolean.class.isAssignableFrom(type)) {
                        m.invoke(entity, true);
                    } else {
                        throw new UnsupportedOperationException(type.getName());
                    }
                }
            }

            return entity;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    

    /* We can't infer types of lists due to erasure. so we cheat here. */
    @SuppressWarnings("unchecked")
    private static List<?> createList(Method method, int size) throws Exception {
        @SuppressWarnings("rawtypes")
        final List list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (method.getName().equals("setIssns") || method.getName().equals("setExternalIds") || method.getName().equals("setLocatorIds")) {
                list.add(UUID.randomUUID().toString());
            } else if (method.getName().equals("setRoles")) {
                final Object role = method.getDeclaringClass().newInstance();
                if (role instanceof User) {
                    final User.Role[] values = User.Role.values();
                    list.add(values[i]);
                } else if (role instanceof Contributor) {
                    final Contributor.Role[] values = Contributor.Role.values();
                    list.add(values[i]);
                }
            } else {
                list.add(URI.create("urn:uuid:" + UUID.randomUUID().toString()));
            }
        }

        return list;
    }

    /**
     * Try invoking a runnable until it succeeds.
     *
     * @param times The number of times to run
     * @param thingy The runnable.
     */
    void attempt(final int times, final Runnable thingy) {
        attempt(times, () -> {
            thingy.run();
            return null;
        });
    }

    /**
     * Try invoking a callable until it succeeds.
     *
     * @param times Number of times to try
     * @param it the thing to call.
     * @return the result from the callable, when successful.
     */
    <T> T attempt(final int times, final Callable<T> it) {

        Throwable caught = null;

        for (int tries = 0; tries < times; tries++) {
            try {
                return it.call();
            } catch (final Throwable e) {
                caught = e;
                try {
                    Thread.sleep(3000);
                    System.out.println("... waiting for index to update");
                } catch (final InterruptedException i) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }
        throw new RuntimeException("Failed executing task", caught);
    }
    
    static CloseableHttpClient getHttpClient() {
        final CredentialsProvider provider = new BasicCredentialsProvider();
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(FedoraConfig.getUserName(),
                FedoraConfig.getPassword());
        provider.setCredentials(AuthScope.ANY, credentials);

        return HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();

    }

}
