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

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.dataconservancy.pass.client.PassClient;
import org.dataconservancy.pass.client.PassClientFactory;
import org.dataconservancy.pass.model.Grant;
import org.dataconservancy.pass.model.User;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Karen Hanson
 */
public class IndexClientIT extends ClientITBase {
    
    //TODO:remove these when docker containers working properly along with all string.replace in tests
    private static final String FCREPO_URL = "http://localhost:8080/fcrepo/rest/";
    private static final String DOCKER_FCREPO_URL = "http://fcrepo:8080/fcrepo/rest/";
    
    @Test
    public void testMixedCaseAwardNumber() throws Exception {

        Grant grant = random(Grant.class, 1);
        URI grantId = client.createResource(grant);

        attempt(3, () -> {
            URI foundGrantId1 = client.findByAttribute(Grant.class, "awardNumber", grant.getAwardNumber().toLowerCase());
            assertEquals(grantId, foundGrantId1);
        });
        
        attempt(3, () -> {
            URI foundGrantId2 = client.findByAttribute(Grant.class, "awardNumber", grant.getAwardNumber().toUpperCase());
            assertEquals(grantId, foundGrantId2);
        });
    @Test
    public void testSpecialCharacterSearch() throws InterruptedException {
        PassClient client = PassClientFactory.getPassClient();
        
        User user = new User();
        user.setFirstName("Mary-Ann");
        user.setLastName("Schäfer");
        user.setDisplayName("Mary \"The Shark\" Schäfer");
        user.setAffiliation("Lamar & Schäfer Laboratory, Nürnberg");
        URI userId = client.createResource(user);
        
        TimeUnit.SECONDS.sleep(5); // wait for indexing to happen (need to do something better here)
        
        URI foundUserId1 = client.findByAttribute(User.class, "firstName", user.getFirstName());
        assertEquals(userId.toString(), foundUserId1.toString().replace(DOCKER_FCREPO_URL, FCREPO_URL));
        
        URI foundUserId2 = client.findByAttribute(User.class, "lastName", user.getLastName());
        assertEquals(userId.toString(), foundUserId2.toString().replace(DOCKER_FCREPO_URL, FCREPO_URL));
        
        URI foundUserId3 = client.findByAttribute(User.class, "displayName", user.getDisplayName());
        assertEquals(userId.toString(), foundUserId3.toString().replace(DOCKER_FCREPO_URL, FCREPO_URL));
        
        URI foundUserId4 = client.findByAttribute(User.class, "affiliation", user.getAffiliation());
        assertEquals(userId.toString(), foundUserId4.toString().replace(DOCKER_FCREPO_URL, FCREPO_URL));
        
        client.deleteResource(userId);
    }

    @AfterClass
    public static void close() throws Exception {
        client.close();
    
    @Test
    public void testNoMatchFound() throws InterruptedException {
        PassClient client = PassClientFactory.getPassClient();
        
        Grant grant = random(Grant.class, 1);
        URI grantId = client.createResource(grant); //create something so it's not empty
        
        TimeUnit.SECONDS.sleep(5); // wait for indexing to happen (need to do something better here)
        URI foundGrantId1 = client.findByAttribute(Grant.class, "awardNumber", "no match");
        assertEquals(null, foundGrantId1);
        
        client.deleteResource(grantId);
    }
    
}
