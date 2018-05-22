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

import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.MessageDigestCalculatingInputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.dataconservancy.pass.model.File;
import org.dataconservancy.pass.model.Submission;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests related to uploading binary content to repository resources
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class PostBinaryIT extends ClientITBase {

    private static final Logger LOG = LoggerFactory.getLogger(IncomingLinksIT.class);

    private static final String EXPECTED_SHA_1 = "0a2f005e7b3fe1a3a658765bc5654d34625991a3";

    private static final String EXPECTED_SLUG = "druid-hill";

    private static final String EXPECTED_CONTENT_TYPE = "image/jpeg";

    private static final String EXPECTED_FILENAME = "dh.jpg";

    private Submission submission;

    private File file;

    private MessageDigestCalculatingInputStream uploadedIn;

    private URI uploaded;

    @Before
    public void setUp() throws Exception {
        // Add a Submission
        submission = new Submission();
        submission.setSource(Submission.Source.PASS);
        submission = client.readResource(client.createResource(submission), Submission.class);
        createdUris.put(submission.getId(), Submission.class);

        // Add a File that references the Submission
        file = new File();
        file.setName("FileReferencingSubmission");
        file.setSubmission(submission.getId());
        file = client.readResource(client.createResource(file), File.class);
        createdUris.put(file.getId(), File.class);

        InputStream in = this.getClass().getResourceAsStream(EXPECTED_FILENAME);
        uploadedIn = new MessageDigestCalculatingInputStream(in, "SHA-1");
        uploaded = client.upload(submission.getId(), uploadedIn, new HashMap<String, String>() {
            {
                put("slug", EXPECTED_SLUG);
                put("filename", EXPECTED_FILENAME);
                put("sha1", EXPECTED_SHA_1);
                put("content-type", EXPECTED_CONTENT_TYPE);
            }
        });

        assertNotNull("Expected a URI to the uploaded binary.", uploaded);
    }

    /**
     * Insure that binary uploaded to an entity can be retrieved.
     *
     * @throws Exception
     */
    @Test
    public void postSimple() throws Exception {
        try (Response res = okHttp.newCall(getWithAccept(uploaded)).execute()) {
            MessageDigestCalculatingInputStream mdActualIn = new MessageDigestCalculatingInputStream(
                    res.body().byteStream(), "SHA-1");
            IOUtils.copy(mdActualIn, new NullOutputStream());

            byte[] expectedDigest = uploadedIn.getMessageDigest().digest();
            byte[] actualDigest = mdActualIn.getMessageDigest().digest();

            assertTrue("Unexpected response code: " + res.code(), res.isSuccessful());
            assertArrayEquals("The checksum for " + uploaded + " differs from the expected value.",
                    actualDigest, expectedDigest);
        }
    }

    /**
     * Insure the "slug" parameter is honored.
     *
     * @throws Exception
     */
    @Test
    public void postWithSlug() throws Exception {
        assertTrue(uploaded.toString().endsWith(EXPECTED_SLUG));
    }

    /**
     * Insure content type header is honored.
     *
     * @throws Exception
     */
    @Test
    public void postWithContentType() throws Exception {
        try (Response res = okHttp.newCall(getWithAccept(uploaded)).execute()) {
            assertEquals("image/jpeg", res.header("content-type"));
        }
    }

    /**
     * Fedora requires an {@code Accept} header, even when retrieving binary content.
     *
     * @param uri the URI to a Fedora resource
     * @return a Request targeting the resource, which includes the sending of an {@code Accept} header
     */
    private static Request getWithAccept(URI uri) {
        return new Request.Builder()
                .get()
                .url(uri.toString())
                .addHeader("Accept", "*/*")
                .build();
    }

}
