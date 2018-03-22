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

import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.dataconservancy.pass.model.PassEntity;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionComparatorMode;

/**
 * Tests client update functionality
 *
 * @author apb@jhu.edu
 */
public class UpdateIT extends ClientITBase {

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

        final PassEntity intermediate = client.readResource(client.createResource(toDeposit), toDeposit.getClass());

        try {
            final URI id = intermediate.getId();
            BeanUtils.copyProperties(intermediate, updatedContent);
            intermediate.setId(id);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        client.updateResource(intermediate);

        final PassEntity asUpdated = client.readResource(intermediate.getId(), intermediate.getClass());

        assertReflectionEquals(normalized(updatedContent), normalized(asUpdated),
                ReflectionComparatorMode.LENIENT_ORDER);

    }
}
