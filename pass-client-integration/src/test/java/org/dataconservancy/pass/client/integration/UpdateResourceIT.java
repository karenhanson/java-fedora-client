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
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import org.junit.Test;

import org.dataconservancy.pass.client.PassClient;
import org.dataconservancy.pass.client.PassClientFactory;
import org.dataconservancy.pass.client.fedora.UpdateConflictException;
import org.dataconservancy.pass.model.Deposit;
import org.dataconservancy.pass.model.Grant;
import org.dataconservancy.pass.model.PassEntity;
import org.dataconservancy.pass.model.User;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
/**
 * Tests client update functionality
 *
 * @author apb@jhu.edu
 */
public class UpdateResourceIT extends ClientITBase {

    /* Create a random resource, and replace all its content */
    @Test
    public void replaceAllTest() {

        PASS_TYPES.stream()
                .forEach(cls -> createAndUpdate(random(cls, 2), random(cls, 2)));
    }

    /* Create a random resource, and clear all its content */
    @Test
    public void deleteAllTest() {

        PASS_TYPES.stream()
                .forEach(cls -> createAndUpdate(random(cls, 2), empty(cls)));
    }

    /* Create a random resource, and update only its relationsips */
    @Test
    public void partialUpdateTest() {

        PASS_TYPES.stream()
                .map(cls -> random(cls, 2))
                .forEach(resource -> createAndUpdate(resource, removeRelationships(resource)));
    }
    
    /**
     * Checks the behavior of PATCH - PATCH should allow partial updates of the data model, 
     * in which only the fields that are available should be affected by changes. Here we use
     * a partial User model to do an update on a whole User, including assigning a null value to username.
     */
    @Test
    public void patchUpdateWithDifferentModelsTest() {        
        //create a complete user
        User user = random(User.class, 1);
        final URI userId = client.createResource(user);
        createdUris.put(userId, User.class);

        //adding a pause for indexer to catchup
        attempt(RETRIES, () -> {
            final URI foundUri = client.findByAttribute(User.class, "@id", userId);
            assertEquals(userId, foundUri);
        });
        
        //update using an incomplete user
        org.dataconservancy.pass.client.integration.User incompleteUser = new org.dataconservancy.pass.client.integration.User();
        incompleteUser.setId(userId);
        incompleteUser.setDisplayName("Ms Tester");
        incompleteUser.setEmail("mtester@blahblahetc.test");
        incompleteUser.setUsername(null);
        client.updateResource(incompleteUser);
        
        //verify user still has original fields and has new field values incorporated including the null username
        User updatedUser = client.readResource(userId, User.class);
        assertEquals(userId, updatedUser.getId());
        assertEquals(user.getAffiliation(), updatedUser.getAffiliation());
        assertEquals(user.getFirstName(), updatedUser.getFirstName());
        assertEquals(user.getLastName(), updatedUser.getLastName());
        assertEquals(incompleteUser.getDisplayName(), updatedUser.getDisplayName());
        assertEquals(incompleteUser.getEmail(), updatedUser.getEmail());
        assertEquals(null, updatedUser.getUsername());
    }    
    

    
    /**
     * Checks the behavior of PUT - PUT should not allow partial updates of the data model.
     * The model should be completely overwritten every time
     */
    @Test
    public void putUpdateWithDifferentModelsTest() {
        //create an instance that uses the PUT update
        final PassClient overwriteOnUpdateClient = PassClientFactory.getPassClient(true);
        
        //create a complete user
        User user = random(User.class, 1);
        final URI userId = overwriteOnUpdateClient.createResource(user);
        createdUris.put(userId, User.class);

        //adding a pause for indexer to catchup
        attempt(RETRIES, () -> {
            final URI foundUri = overwriteOnUpdateClient.findByAttribute(User.class, "@id", userId);
            assertEquals(userId, foundUri);
        });
        
        //update using an incomplete user
        org.dataconservancy.pass.client.integration.User incompleteUser = new org.dataconservancy.pass.client.integration.User();
        incompleteUser.setId(userId);
        incompleteUser.setDisplayName("Ms Tester");
        incompleteUser.setEmail("mtester@blahblahetc.test");
        incompleteUser.setUsername(null);
        overwriteOnUpdateClient.updateResource(incompleteUser);
        
        //verify User record does not have original fields, they have been completely overwritten with the partial model
        User updatedUser = overwriteOnUpdateClient.readResource(userId, User.class);
        assertEquals(userId, updatedUser.getId());
        assertEquals(null, updatedUser.getAffiliation());
        assertEquals(null, updatedUser.getFirstName());
        assertEquals(null, updatedUser.getLastName());
        assertEquals(incompleteUser.getDisplayName(), updatedUser.getDisplayName());
        assertEquals(incompleteUser.getEmail(), updatedUser.getEmail());
        assertEquals(null, updatedUser.getUsername());        
    }    
    

    /**
     * Checks that if you try to update a resource that was updated elsewhere between your read/write
     * a UpdateConflictException is thrown when you try to update
     */
    @Test(expected=UpdateConflictException.class)
    public void testEtagError() {
        Grant grant = random(Grant.class, 2);
        URI grantId = client.createResource(grant);
        createdUris.put(grantId, Grant.class);
        
        Grant grantCopy1 = client.readResource(grantId, Grant.class);
        Grant grantCopy2 = client.readResource(grantId, Grant.class);
        
        try {
            grantCopy1.setLocalKey("123456");
            client.updateResource(grantCopy1);
        } catch (Exception ex){
            //should not fail here!
            fail(ex.getMessage());
        }
        
        grantCopy2.setLocalKey("abcdefg");
        client.updateResource(grantCopy2);
    }

    /**
     * Documenting behavior: simply writing the same object back to the repository results in a different etag.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateWithNoChanges() throws Exception {
        Deposit deposit = client.readResource(client.createResource(random(Deposit.class, 1)), Deposit.class);
        Deposit updated = client.updateAndReadResource(deposit, Deposit.class);

        assertNotEquals(deposit.getVersionTag(), updated.getVersionTag());
        assertEquals(deposit.getId().toString(), updated.getId().toString());
        createdUris.put(deposit.getId(), Deposit.class);
    }

    /** simple update test using PATCH **/
    @Test
    public void testPatchUpdateWithChange() throws Exception {
        Deposit deposit = client.readResource(client.createResource(random(Deposit.class, 1)), Deposit.class);

        String expectedStatusRef = "http://example.org/status/1";
        deposit.setDepositStatusRef(expectedStatusRef);

        Deposit updated = client.updateAndReadResource(deposit, Deposit.class);

        // Doesn't really matter, it changes anyway
        assertNotEquals(deposit.getVersionTag(), updated.getVersionTag());
        assertEquals(expectedStatusRef, updated.getDepositStatusRef());
        assertEquals(deposit.getId().toString(), updated.getId().toString());
        createdUris.put(deposit.getId(), Deposit.class);
    }


    /** simple update test using PUT **/
    @Test
    public void testPutUpdateWithChange() throws Exception {
        //create an instance that uses the PUT update
        final PassClient overwriteOnUpdateClient = PassClientFactory.getPassClient(true);
        
        Deposit deposit = overwriteOnUpdateClient.readResource(overwriteOnUpdateClient.createResource(random(Deposit.class, 1)), Deposit.class);

        String expectedStatusRef = "http://example.org/status/1";
        deposit.setDepositStatusRef(expectedStatusRef);

        Deposit updated = overwriteOnUpdateClient.updateAndReadResource(deposit, Deposit.class);

        // Doesn't really matter, it changes anyway
        assertNotEquals(deposit.getVersionTag(), updated.getVersionTag());
        assertEquals(expectedStatusRef, updated.getDepositStatusRef());
        assertEquals(deposit.getId().toString(), updated.getId().toString());
        createdUris.put(deposit.getId(), Deposit.class);
    }
    

    PassEntity removeRelationships(PassEntity resource) {
        try {
            final PassEntity entity = resource.getClass().newInstance();
            BeanUtils.copyProperties(entity, resource);

            for (final Method m : resource.getClass().getMethods()) {
                if (m.getName().startsWith("set") && List.class.isAssignableFrom(m.getParameterTypes()[0])) {
                    m.invoke(entity, new ArrayList<>());
                }
            }

            return entity;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    void createAndUpdate(PassEntity toDeposit, PassEntity updatedContent) {
        final URI passEntityUri = client.createResource(toDeposit);
        createdUris.put(passEntityUri, toDeposit.getClass());

        try {
            final PassEntity intermediate = client.readResource(passEntityUri, toDeposit.getClass());
            String versionTag = intermediate.getVersionTag();
            BeanUtils.copyProperties(intermediate, updatedContent);
            intermediate.setId(passEntityUri);
            intermediate.setVersionTag(versionTag);
            client.updateResource(intermediate);
            final PassEntity asUpdated = client.readResource(passEntityUri, intermediate.getClass());
            assertReflectionEquals(normalized(updatedContent), normalized(asUpdated),
                                   ReflectionComparatorMode.LENIENT_ORDER);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } 
    }
}
