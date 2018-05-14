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

import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.lang.reflect.Method;

import java.net.URI;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import org.dataconservancy.pass.model.PassEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Creates PASS entities, and makes sure they can be searched on by each field 
 * in the indexer
 *
 * @author Karen Hanson
 */

public class CreateFindByRoundTripIT extends ClientITBase {

    /**
    * Creates a resource and then checks that resource can be searched on in the indexer
    * using each of its field in the model. All lists contain ONE entry
    */
    @Test
    public void indexerRoundTripTest() {
        PASS_TYPES.stream()
                .map(cls -> random(cls, 2))
                .forEach(this::roundTrip);
    }

    /**
     * Creates the object of the type provided, and then uses the getters for that Object
     * to a findByAttribute call to the index for each field
     * @param forDeposit
     */
    @SuppressWarnings("rawtypes")
    private void roundTrip(PassEntity forDeposit) {
        final URI entityUri = client.createResource(forDeposit);   
        try {
            //check for record in indexer before proceeding to other tests
            attempt(RETRIES, () -> {
                URI uri = client.findByAttribute(forDeposit.getClass(), "@id", entityUri);   
                assertEquals(entityUri, uri);
            });
            createdUris.put(entityUri, forDeposit.getClass());

            for (final PropertyDescriptor pd : Introspector.getBeanInfo(forDeposit.getClass(), PassEntity.class).getPropertyDescriptors()) {
                Method m = pd.getReadMethod();
                
                if (!m.getName().equals("getType")) {                   
                    final Class<?> type = m.getReturnType();
                    System.out.println(String.format("Looking at %s with type %s", m.getName(), type));
                    Object val = null;
                    
                    if (List.class.isAssignableFrom(type)) {
                        List listval = (List) m.invoke(forDeposit);
                        val = listval.get(0);
                    } else {
                        val = m.invoke(forDeposit);
                    } 

                    Set<URI> uris = client.findAllByAttribute(forDeposit.getClass(), fieldName(m.getName()), val);
                    System.out.println(String.format("Comparing entity uri (%s) to found uri (%s) for method %s", entityUri, uris, m.getName()));
                    assertTrue(uris.contains(entityUri));
                }
            } 
        } catch (Exception ex) {
            throw new RuntimeException("Round trip check of indexer failed", ex);
        }
        
    }
    
    /**
     * Get json fieldname corresponding to GET
     * @param methodname
     * @return
     */
    private String fieldName(String methodname) {                    
        if (methodname.equals("getPublicationAbstract")) { //the only exception, could not use keyword "abstract"
            return "abstract";
        }
        String fldname = methodname.substring(3, 4).toLowerCase() + methodname.substring(4);
        return fldname;
    }
    
}
