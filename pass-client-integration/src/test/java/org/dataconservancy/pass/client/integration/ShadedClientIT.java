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

import java.net.URI;

import org.dataconservancy.pass.client.PassClient;
import org.dataconservancy.pass.client.PassClientFactory;
import org.dataconservancy.pass.model.Submission;

import org.junit.Test;

/**
 * Verifies that the shaded client can function beside the current client
 *
 * @author apb@jhu.edu
 */
public class ShadedClientIT extends ClientITBase {

    @Test
    public void conversionTest() {
        final PassClient newClient = PassClientFactory.getPassClient();
        final org.dataconservancy.pass.v2_3.client.PassClient oldClient =
                org.dataconservancy.pass.v2_3.client.PassClientFactory.getPassClient();

        org.dataconservancy.pass.v2_3.model.Submission older = new org.dataconservancy.pass.v2_3.model.Submission();

        // Now persist a resource the old way;

        // First, we use an incompatible field (present in 2.x absent in 3.x):
        older.setUser(URI.create("http://example.org"));

        // Now, we use a compatible/unchanged field (same in 2.x and 3.x)
        older.setMetadata("boo!");

        // Persist the old object
        final URI submissionUri = oldClient.createResource(older);
        createdUris.put(submissionUri, Submission.class);

        // verify that the old client can read it.
        older = oldClient.readResource(submissionUri, org.dataconservancy.pass.v2_3.model.Submission.class);

        // Now, read the data with the new client
        final Submission newer = newClient.readResource(submissionUri, Submission.class);

        // The compatible/unchanged field should be the same
        assertEquals(older.getMetadata(), newer.getMetadata());

        // Now, set the new field "submitter" to the old field "user"
        newer.setSubmitter(older.getUser());

        // Save!
        final Submission updated = newClient.updateAndReadResource(newer, Submission.class);

        // Now, make sure the migrated field is now the same, and the compatible/unchanged fields are the same
        assertEquals(older.getUser(), updated.getSubmitter());
        assertEquals(older.getMetadata(), updated.getMetadata());
    }

}
