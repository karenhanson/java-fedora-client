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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Karen Hanson
 */
public class FedoraPassIndexClient {


    private static final Logger LOG = LoggerFactory.getLogger(FedoraPassIndexClient.class);

    /**
     * @see org.dataconservancy.pass.client.PassClient#findByAttribute(Class, String, Object)
     */
    public <T> URI findByAttribute(Class<T> modelClass, String attribute, Object value) {
        if (modelClass==null) {throw new IllegalArgumentException("modelClass cannot be null");}
        if (attribute==null || attribute.length()==0) {throw new IllegalArgumentException("attribute cannot be null");}
        if (value==null) {throw new IllegalArgumentException("value cannot be null");}
        
        URI passEntityUri = null;
        
        LOG.debug("Searching for {} where {} is \"{}\"", modelClass.getSimpleName(), attribute, value.toString());
        
        //TODO: do stuff
        
        return passEntityUri;
    }

    
    /**
     * @see org.dataconservancy.pass.client.PassClient#findAllByAttribute(Class, String, Object)
     */
    public <T> Set<URI> findAllByAttribute(Class<T> modelClass, String attribute, Object value) {
        if (modelClass==null) {throw new IllegalArgumentException("modelClass cannot be null");}
        if (attribute==null || attribute.length()==0) {throw new IllegalArgumentException("attribute cannot be null");}
        if (value==null) {throw new IllegalArgumentException("value cannot be null");}
        
        Set<URI> passEntityUris = new HashSet<URI>();
        
        //TODO: do stuff

        LOG.debug("Searching for {} where {} is \"{}\"", modelClass.getSimpleName(), attribute, value.toString());
        
        return passEntityUris;
    }


    /**
     * @see org.dataconservancy.pass.client.PassClient#findAllByAttributes(Class, Map<String, Object>)
     */
    public <T> Set<URI> findAllByAttributes(Class<T> modelClass, Map<String, Object> valueAttributesMap) {
        if (modelClass==null) {throw new IllegalArgumentException("modelClass cannot be null");}
        if (valueAttributesMap==null || valueAttributesMap.size()==0) {throw new IllegalArgumentException("valueAttributesMap cannot be empty");}
        //TODO:check map fields are valid
        
        Set<URI> passEntityUris = new HashSet<URI>();
        
        //TODO: do stuff

        LOG.debug("Searching for {} using multiple filters", modelClass.getSimpleName());
        
        return passEntityUris;
    }
    
}
