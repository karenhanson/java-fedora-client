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
package org.dataconservancy.pass.client.adapter;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.dataconservancy.pass.client.PassJsonAdapter;
import org.dataconservancy.pass.client.util.ConfigUtil;
import org.dataconservancy.pass.model.PassEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Karen Hanson
 */
public class PassJsonAdapterBasic implements PassJsonAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(PassJsonAdapterBasic.class);
    
    private final static String CONTEXT_PROPKEY = "pass.jsonld.context";
    private final static String DEFAULT_CONTEXT = "http://oa-pass.github.io/pass-data-model/src/main/resources/context.jsonld";
    
    /**
     * {@inheritDoc}
     */
    public byte[] toJson(PassEntity passObj, boolean includePassContext) { 
        if (passObj == null) {
            throw new IllegalArgumentException("passObject cannot be null");
        }
        if (includePassContext) {
            //Assign pass context
            LOG.debug("Converting {} to JSON with context", passObj.getClass().getSimpleName());
            passObj.setContext(getPassJsonLdContext());                
        } else {
            //scrub context if there is one
            LOG.debug("Converting {} to JSON without context", passObj.getClass().getSimpleName());
            passObj.setContext(null);
        }

        byte [] jsonld = null;        
        
        //convert to json
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            jsonld = objectMapper.writeValueAsBytes(passObj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not model convert to JSON", e);
        }
     
        return jsonld;
    }

    /**
     * {@inheritDoc}
     * @param <T>
     */
    public <T> PassEntity toModel(byte[] json, Class<T> valueType) {
        if (json.length == 0) {
            throw new IllegalArgumentException("json cannot be empty");
        }
        if (valueType == null) {
            throw new IllegalArgumentException("valueType cannot be empty");            
        }
        
        //map to model
        PassEntity model = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            model = (PassEntity) objectMapper.readValue(json, valueType);
            LOG.debug("JSON converted to model {}", valueType.getSimpleName());
        } catch (IOException e) {
            throw new RuntimeException("Could not map JSON to " + valueType.getSimpleName(), e);    
        } 

        return model;
    }

    /**
     * {@inheritDoc}
     * @param <T>
     */
    public <T> PassEntity toModel(InputStream json, Class<T> valueType) {
        if (json == null) {
            throw new IllegalArgumentException("jsonLd cannot be empty");
        }
        if (valueType == null) {
            throw new IllegalArgumentException("valueType cannot be empty");            
        }
        
        //map to model
        PassEntity model = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            model = (PassEntity) objectMapper.readValue(json, valueType);
        } catch (IOException e) {
            throw new RuntimeException("Could not map JSON to " + valueType.getSimpleName(), e);    
        }

        return model;
    }
    
    /**
     * Retrieve the context path to add to the JSON for conversion to JSON-LD
     * @return
     */
    private static String getPassJsonLdContext() {
        String context = ConfigUtil.getSystemProperty(CONTEXT_PROPKEY, DEFAULT_CONTEXT);
        LOG.debug("Using JSONLD Context: {}", context);
        return context;
    }
    
}
