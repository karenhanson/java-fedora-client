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

import java.net.URI;

import java.util.HashSet;
import java.util.Set;

import org.dataconservancy.pass.model.Deposit;
import org.junit.Test;

import org.dataconservancy.pass.model.File;
import org.dataconservancy.pass.model.Grant;
import org.dataconservancy.pass.model.Submission;
import org.dataconservancy.pass.model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for PassClient.findAllByAttribute
 * @author Karen Hanson
 */
public class FindAllByAttributeIT extends ClientITBase {
        
    /**
     * Ensures that index search can handle special characters including umlauts, double quotes, 
     * dashes, and ampersands
     */
    @Test
    public void testSpecialCharacterSearch() {
        User user = new User();
        user.setFirstName("Mary-Ann");
        user.setLastName("Schäfer");
        user.setDisplayName("Mary \"The Shark\" Schäfer");
        user.setAffiliation("Lamar & Schäfer Laboratory, Nürnberg");
        URI userId1 = client.createResource(user);
        URI userId2 = client.createResource(user);
        try {
            //make sure records are in the indexer before continuing
            attempt(RETRIES, () -> {
                final URI uri = client.findByAttribute(User.class, "@id", userId1);
                assertEquals(userId1, uri);
            });        
            attempt(RETRIES, () -> {
                final URI uri = client.findByAttribute(User.class, "@id", userId2);
                assertEquals(userId2, uri);
            });        
            
    
            Set<URI> uris = client.findAllByAttribute(User.class, "firstName", user.getFirstName());
            assertEquals(2, uris.size());
            assertTrue(uris.contains(userId1));
            assertTrue(uris.contains(userId2));
    
            uris = client.findAllByAttribute(User.class, "lastName", user.getLastName());
            assertEquals(2, uris.size());
            assertTrue(uris.contains(userId1));
            assertTrue(uris.contains(userId2));
            
            uris = client.findAllByAttribute(User.class, "displayName", user.getDisplayName());
            assertEquals(2, uris.size());
            assertTrue(uris.contains(userId1));
            assertTrue(uris.contains(userId2));
            
            uris = client.findAllByAttribute(User.class, "affiliation", user.getAffiliation());
            assertEquals(2, uris.size());
            assertTrue(uris.contains(userId1));
            assertTrue(uris.contains(userId2));
            
        } finally {
            //need to log fail if this doesn't work as it could mess up re-testing if data isn't cleaned out
            try {
                if (userId1 != null) {
                    client.deleteResource(userId1);
                }
                if (userId2 != null) {
                    client.deleteResource(userId2);
                }
            } catch (Exception ex) {
                fail("Could not clean up from FindAllByAttributeIT.testSpecialCharacterSearch(), this may cause errors if test is rerun on the same dbs");
            }
        }
        
    }
    
    /**
     * Adds 10 records, then retrieves them in chunks using limit and offet to verify they are working
     * @throws Exception
     */
    @Test
    public void testLimitAndOffset() throws Exception {
        String descrip = "short fake description";
        String descripFld = "description";
        try {
            URI uri = null;
            for(int i = 0; i < 10; i++){
                File file = random(File.class, 2);
                file.setDescription(descrip);
                uri = client.createResource(file);
            }
            
            final URI searchUri = uri;
            
            attempt(20, () -> { //make sure last one is in the index
                final URI matchedUri = client.findByAttribute(File.class, "@id", searchUri);
                assertEquals(searchUri, matchedUri);
            }); 
    
            Set<URI> matches = client.findAllByAttribute(File.class, descripFld, descrip, 4, 0);
            assertEquals(4, matches.size());
            matches = client.findAllByAttribute(File.class, descripFld, descrip, 4, 0);
            assertEquals(4, matches.size());
            matches = client.findAllByAttribute(File.class, descripFld, descrip, 2, 0);
            assertEquals(2, matches.size());       
        } finally {
            Set <URI> matches = client.findAllByAttribute(File.class, descripFld, descrip);
            for (URI match : matches) {
                client.deleteResource(match);
            }
        }
    }    
    
    /**
     * Ensures no match found returns empty Set instead of exception
     */
    @Test
    public void testNoMatchFound() {
        Grant grant = random(Grant.class, 1);
        URI grantId = client.createResource(grant); //create something so it's not empty index
        
        try {
            attempt(RETRIES, () -> {
                final URI uri = client.findByAttribute(Grant.class, "@id", grantId);
                assertEquals(grantId, uri);
            });        
            
            Set<URI> matchedIds = client.findAllByAttribute(Grant.class, "awardNumber", "no match");
            assertNotNull(matchedIds);
            assertEquals(0, matchedIds.size());
        } finally {
            client.deleteResource(grantId);
        }
    }

    @Test
    public void testFindDepositWithNoStatus() throws Exception {
        Deposit deposit = random(Deposit.class, 1);
        deposit.setDepositStatus(null);
        URI expectedUri = client.createResource(deposit);

        try {
            attempt(20, () -> {
                assertEquals(expectedUri.getPath(),
                        client.findByAttribute(Deposit.class, "@id", expectedUri).getPath());
            });

            assertEquals(expectedUri.getPath(),
                    client.findByAttribute(Deposit.class, "depositStatus", null).getPath());
            Set<URI> deposits = client.findAllByAttribute(Deposit.class, "depositStatus", null);
            assertEquals(1, deposits.size());
            assertEquals(expectedUri.getPath(), deposits.iterator().next().getPath());
        } finally {
            client.deleteResource(expectedUri);
        }
    }

    /**
     * Check findAllByAttribute rejects a value that is a collection
     */
    @Test(expected=IllegalArgumentException.class)
    public void testValueParamAsCollection() {
        try {
            Set<URI> coll = new HashSet<URI>(); 
            client.findByAttribute(Submission.class, "repositories", coll);
        } catch (Exception ex) {
            assertTrue(ex.getMessage().contains("cannot be a Collection"));
            assertTrue(ex instanceof RuntimeException);
            throw ex;
        }
        fail ("Test should have thrown exception");
    }
    
}
