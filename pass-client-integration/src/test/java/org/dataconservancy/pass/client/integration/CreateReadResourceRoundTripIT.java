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

import org.dataconservancy.pass.model.PassEntity;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionComparatorMode;

/**
 * Creates PASS entities, and makes sure they can be roundtripped to the repository.
 *
 * @author apb@jhu.edu
 */

public class CreateReadResourceRoundTripIT extends ClientITBase {

    /* Roundtrip with all lists containing multiple values */
    @Test
    public void roundTripWithFullListsTest() {

        PASS_TYPES.stream()
                .map(cls -> random(cls, 2))
                .forEach(this::roundTrip);
    }

    /* Roundtrip with all lists containing ONE entry.. an edge case */
    @Test
    public void roundTripWithSingleListsTest() {

        PASS_TYPES.stream()
                .map(cls -> random(cls, 1))
                .forEach(this::roundTrip);
    }

    /* Roundtrip with all lists empty - for completeness */
    @Test
    public void roundTripWithEmptyListsTest() {

        PASS_TYPES.stream()
                .map(cls -> random(cls, 0))
                .forEach(this::roundTrip);
    }

    /* Sparse roundtrip - empty objects */
    @Test
    public void roundTripSparseTest() {
        PASS_TYPES.stream()
                .map(ClientITBase::empty)
                .forEach(this::roundTrip);
    }

    void roundTrip(PassEntity asDeposited) {

        final PassEntity retrieved = client.readResource(client.createResource(asDeposited), asDeposited.getClass());

        assertReflectionEquals(normalized(asDeposited), normalized(retrieved),
                ReflectionComparatorMode.LENIENT_ORDER);

    }
}
