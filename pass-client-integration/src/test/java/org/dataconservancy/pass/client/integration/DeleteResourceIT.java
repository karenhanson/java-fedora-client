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

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.dataconservancy.pass.client.fedora.FedoraConfig;

import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

/**
 * Tests delete functionality
 *
 * @author apb@jhu.edu
 */
public class DeleteResourceIT extends ClientITBase {

    CloseableHttpClient http = getHttpClient();

    @Test
    public void deleteTest() {
        PASS_TYPES.stream()
                .map(cls -> client.createResource(random(cls, 2)))
                .forEach(uri -> {
                    assertStatus(uri, HttpStatus.SC_OK);
                    client.deleteResource(uri);
                    assertStatus(uri, HttpStatus.SC_GONE);
                });
    }

    public void assertStatus(URI uri, int status) {
        final HttpGet get = new HttpGet(uri);

        try (CloseableHttpResponse response = http.execute(get)) {
            assertEquals(status, response.getStatusLine().getStatusCode());
            EntityUtils.consume(response.getEntity());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
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
