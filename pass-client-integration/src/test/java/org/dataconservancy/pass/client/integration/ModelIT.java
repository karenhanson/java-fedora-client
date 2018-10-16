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

import static org.junit.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

import java.lang.reflect.Constructor;

import org.dataconservancy.pass.model.PassEntity;

import org.junit.Test;
import org.unitils.reflectionassert.ReflectionComparatorMode;

/**
 * @author apb@jhu.edu
 */
public class ModelIT extends ClientITBase {

    @Test
    public void cloningTest() {
        PASS_TYPES.stream().forEach(type -> {
            try {
                final PassEntity entity = random(type, 2);
                final Constructor<? extends PassEntity> constructor = type.getConstructor(type);
                final PassEntity clone = constructor.newInstance(entity);
                assertEquals(entity, clone);
                assertReflectionEquals(entity, clone, ReflectionComparatorMode.LENIENT_ORDER);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
