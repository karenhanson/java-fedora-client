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

import java.net.URI;

import java.util.Map;
import java.util.Set;

import org.dataconservancy.pass.client.PassClient;
import org.dataconservancy.pass.model.PassEntity;

/**
 * Creates instances of objects needed to perform PassClient requirements, and redirects to appropriate
 * service (Index client or CRUD client)
 * @author Karen Hanson
 */
public class FedoraPassClient implements PassClient {

    /** 
     * Client that interacts with Fedora repo to carry out CRUD operations 
     */
    private FedoraPassCrudClient crudClient;
    
    /** 
     * Client that interacts with Index repo to do lookups and searches 
     */
    private FedoraPassIndexClient indexClient;
        
    public FedoraPassClient() {
        crudClient = new FedoraPassCrudClient();
        indexClient = new FedoraPassIndexClient();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public URI createResource(PassEntity modelObj) {
        return crudClient.createResource(modelObj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateResource(PassEntity modelObj) {
        crudClient.updateResource(modelObj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteResource(URI modelObj) {
        crudClient.deleteResource(modelObj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PassEntity> T readResource(URI uri, Class<T> modelClass) {
        return crudClient.readResource(uri, modelClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PassEntity> URI findByAttribute(Class<T> modelClass, String attribute, Object value) {
        return indexClient.findByAttribute(modelClass, attribute, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PassEntity> Set<URI> findAllByAttribute(Class<T> modelClass, String attribute, Object value) {
        return indexClient.findAllByAttribute(modelClass, attribute, value);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PassEntity> Set<URI> findAllByAttributes(Class<T> modelClass, Map<String, Object> valueAttributesMap) {
        return indexClient.findAllByAttributes(modelClass, valueAttributesMap);
    }

}
