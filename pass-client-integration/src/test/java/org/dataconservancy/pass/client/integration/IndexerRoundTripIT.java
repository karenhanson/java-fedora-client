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
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.dataconservancy.pass.model.Deposit.DepositStatus;
import org.dataconservancy.pass.model.PassEntity;
import org.dataconservancy.pass.model.PmcParticipation;
import org.dataconservancy.pass.model.RepositoryCopy.CopyStatus;
import org.dataconservancy.pass.model.Submission.AggregatedDepositStatus;
import org.joda.time.DateTime;

import static org.junit.Assert.assertTrue;

/**
 * Creates PASS entities, and makes sure they can be roundtripped to the repository.
 *
 * @author apb@jhu.edu
 */

public class IndexerRoundTripIT extends ClientITBase {

    //TODO:remove these when docker containers working properly along with all string.replace in tests
    private static final String FCREPO_URL = "http://localhost:8080/fcrepo/rest/";
    private static final String DOCKER_FCREPO_URL = "http://fcrepo:8080/fcrepo/rest/";
    
    /* Roundtrip with all lists containing ONE entry.. an edge case */
    @Test
    public void roundTripWithSingleListsTest() {

        PASS_TYPES.stream()
                .map(cls -> random(cls, 1))
                .forEach(this::roundTrip);
    }
    
    @SuppressWarnings("rawtypes")
    public void roundTrip(PassEntity forDeposit) {
        URI entityUri = null;   
        try {
            entityUri = client.createResource(forDeposit);
            TimeUnit.SECONDS.sleep(5);
            
            for (final PropertyDescriptor pd : Introspector.getBeanInfo(forDeposit.getClass(), PassEntity.class).getPropertyDescriptors()) {
                Method m = pd.getReadMethod();
                if (!m.getName().equals("getType")) {
                    if (m.getName().equals("getInstitutionalId")) {
                        TimeUnit.SECONDS.sleep(1);
                    }
                    
                    final Class<?> type = m.getReturnType();
                    Set<URI> uris = null;

                    System.out.println(String.format("Looking at %s with type %s", m.getName(), type));
                    
                    if (String.class.isAssignableFrom(type)) {
                        String val = (String) m.invoke(forDeposit);
                        uris = client.findAllByAttribute(forDeposit.getClass(), fieldName(m.getName()), val);
                    } else if (Enum.class.isAssignableFrom(type)) {
                        Enum val = (Enum) m.invoke(forDeposit);
                        uris = client.findAllByAttribute(forDeposit.getClass(), fieldName(m.getName()), getEnumString(val));
                    } else if (URI.class.isAssignableFrom(type)) {
                        URI val = (URI) m.invoke(forDeposit);
                        uris = client.findAllByAttribute(forDeposit.getClass(), fieldName(m.getName()), val);
                    } else if (DateTime.class.isAssignableFrom(type)) {
                        DateTime val = (DateTime) m.invoke(forDeposit);
                        uris = client.findAllByAttribute(forDeposit.getClass(), fieldName(m.getName()), val);
                    } else if (List.class.isAssignableFrom(type)) {
                        List val = (List) m.invoke(forDeposit);
                        Object rowVal = val.get(0);
                        uris = client.findAllByAttribute(forDeposit.getClass(), fieldName(m.getName()), rowVal);
                    } else if (Boolean.class.isAssignableFrom(type)) {
                        Boolean val = (Boolean) m.invoke(forDeposit);
                        uris = client.findAllByAttribute(forDeposit.getClass(), fieldName(m.getName()), val);
                    } else {
                        throw new UnsupportedOperationException(type.getName());
                    }
                    
                    System.out.println(String.format("Comparing entity uri (%s) to found uri (%s) for method %s", entityUri, uris, m.getName()));
                    assertTrue(uris.contains(new URI(entityUri.toString().replace(FCREPO_URL, DOCKER_FCREPO_URL))));
                }
            } 
        } catch (Exception ex) {
            throw new RuntimeException("Round trip check of indexer failed", ex);
        } finally {
            if (entityUri!=null) {
                client.deleteResource(entityUri);
            }
        }
        
    }
    
    private String getEnumString(Enum val) {
        if (val instanceof AggregatedDepositStatus) {
            return ((AggregatedDepositStatus) val).getValue();
        }

        if (val instanceof DepositStatus) {
            return ((DepositStatus) val).getValue();
        }

        if (val instanceof CopyStatus) {
            return ((CopyStatus) val).getValue();
        }

        if (val instanceof PmcParticipation) {
            return ((PmcParticipation) val).name();
        }
        
        return null;        
    }
    
    
    private String fieldName(String methodname) {
        String fldname = methodname.substring(3, 4).toLowerCase() + methodname.substring(4);
        return fldname;
    }
    
}
