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

import org.dataconservancy.pass.client.PassClient;
import org.dataconservancy.pass.model.Deposit;
import org.dataconservancy.pass.model.File;
import org.dataconservancy.pass.model.Submission;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Insures that the {@link PassClient} can retrieve incoming links, and that callers can successfully sniff the types
 * of the incoming links.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class IncomingLinksIT extends ClientITBase {

    private static final Logger LOG = LoggerFactory.getLogger(IncomingLinksIT.class);

    private String expectedPredicate = "submission";

    private Submission submission;

    private File file;

    private Deposit depositOne;

    private Deposit depositTwo;

    private Submission submissionNoIncoming;

    @Before
    public void setUp() throws Exception {

        // Add a Submission
        submission = new Submission();
        submission.setSource(Submission.Source.PASS);
        submission = client.readResource(client.createResource(submission), Submission.class);

        submissionNoIncoming = new Submission();
        submissionNoIncoming.setSource(Submission.Source.PASS);
        submissionNoIncoming = client.readResource(client.createResource(submissionNoIncoming), Submission.class);

        // Add a File that references the Submission
        file = new File();
        file.setName("FileReferencingSubmission");
        file.setSubmission(submission.getId());
        file = client.readResource(client.createResource(file), File.class);

        // Add two Deposits that reference the Submission
        depositOne = new Deposit();
        depositOne.setDepositStatusRef("http://reference/to/deposit/status/1");
        depositOne.setSubmission(submission.getId());
        depositOne = client.readResource(client.createResource(depositOne), Deposit.class);

        depositTwo = new Deposit();
        depositTwo.setDepositStatusRef("http://reference/to/deposit/status/2");
        depositTwo.setSubmission(submission.getId());
        depositTwo = client.readResource(client.createResource(depositTwo), Deposit.class);

    }

    @After
    public void tearDown() throws Exception {
        client.deleteResource(submission.getId());
        client.deleteResource(submissionNoIncoming.getId());
        client.deleteResource(file.getId());
        client.deleteResource(depositOne.getId());
        client.deleteResource(depositTwo.getId());
    }

    /**
     * Expect three incoming links to the Submission, with the predicate "submission".
     *
     * @throws Exception
     */
    @Test
    public void getIncoming() throws Exception {
        Map<String, Collection<URI>> incomingLinks = client.getIncoming(submission.getId());
        assertNotNull("Returned map must never be null.", incomingLinks);
        assertTrue("Map expected to contain predicate '" + expectedPredicate + "'",
                incomingLinks.containsKey(expectedPredicate));

        Collection<URI> incomingSubmissionLinks = incomingLinks.get("submission");
        assertEquals("Map expected to contain three incoming URIs for predicate '" + expectedPredicate + "'",
                3, incomingSubmissionLinks.size());
        assertTrue(incomingSubmissionLinks.contains(file.getId()));
        assertTrue(incomingSubmissionLinks.contains(depositOne.getId()));
        assertTrue(incomingSubmissionLinks.contains(depositTwo.getId()));
    }

    /**
     * Expect no incoming links to the Submission, result should be an empty map.
     *
     * @throws Exception
     */
    @Test
    public void getGetIncomingNonExisting() throws Exception {
        Map<String, Collection<URI>> incomingLinks = client.getIncoming(submissionNoIncoming.getId());
        assertNotNull("Returned map must never be null.", incomingLinks);
        assertTrue(incomingLinks.isEmpty());
    }

    /**
     * Insure that the types of the incoming links can be reliably sniffed by callers.
     *
     * @throws Exception
     */
    @Test
    public void typeSniffing() throws Exception {
        Map<String, Collection<URI>> incomingLinks = client.getIncoming(submission.getId());
        Set<File> files = incomingLinks.get(expectedPredicate).stream().map((uri) -> {
            try {
                return client.readResource(uri, File.class);
            } catch (Exception e) {
                LOG.error("Error reading resource {}: {}", uri, e.getMessage(), e);
                return null;
            }
        }).collect(HashSet::new, (set, file) -> {
            if (file != null) {
                set.add(file);
            }
        }, HashSet::addAll);

        assertEquals(1, files.size());
        assertEquals(file.getId(), files.iterator().next().getId());

        Set<Deposit> deposits = incomingLinks.get(expectedPredicate).stream().map((uri) -> {
            try {
                return client.readResource(uri, Deposit.class);
            } catch (Exception e) {
                LOG.error("Error reading resource {}: {}", uri, e.getMessage(), e);
                return null;
            }
        }).collect(HashSet::new, (set, deposit) -> {
            if (deposit != null) {
                set.add(deposit);
            }
        }, HashSet::addAll);

        assertEquals(2, deposits.size());
        assertTrue(deposits.stream().anyMatch(d -> d.getId().equals(depositOne.getId())));
        assertTrue(deposits.stream().anyMatch(d -> d.getId().equals(depositTwo.getId())));
    }
}
