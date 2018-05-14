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

import org.dataconservancy.pass.model.PassEntity;
import org.dataconservancy.pass.model.User;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import static org.junit.Assert.assertEquals;
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
            BeanUtils.copyProperties(intermediate, updatedContent);
            intermediate.setId(passEntityUri);
            client.updateResource(intermediate);
            final PassEntity asUpdated = client.readResource(passEntityUri, intermediate.getClass());
            assertReflectionEquals(normalized(updatedContent), normalized(asUpdated),
                                   ReflectionComparatorMode.LENIENT_ORDER);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } 
    }
}
