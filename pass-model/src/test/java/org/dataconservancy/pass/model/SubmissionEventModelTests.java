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
package org.dataconservancy.pass.model;

import java.io.InputStream;

import java.net.URI;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import org.dataconservancy.pass.model.SubmissionEvent.EventType;
import org.dataconservancy.pass.model.SubmissionEvent.PerformerRole;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Model has been annotated with JSON tags. These tests do a simple check to ensure the
 * Jackson integration is functional and the equals / hashcode functions work
 * @author Karen Hanson
 */
public class SubmissionEventModelTests {

    private DateTimeFormatter dateFormatter = ISODateTimeFormat.dateTime().withZoneUTC();
    
    /**
     * Simple verification that JSON file can be converted to SubmissionEvent model
     * @throws Exception
     */
    @Test
    public void testSubmissionEventFromJsonConversion() throws Exception {

        InputStream json = SubmissionEventModelTests.class.getResourceAsStream("/submissionevent.json");
        ObjectMapper objectMapper = new ObjectMapper();
        SubmissionEvent submissionEvent = objectMapper.readValue(json, SubmissionEvent.class);
        
        assertEquals(TestValues.SUBMISSIONEVENT_ID, submissionEvent.getId().toString());
        assertEquals(TestValues.USER_ID_1, submissionEvent.getPerformedBy().toString());
        assertEquals(TestValues.SUBMISSIONEVENT_PERFORMED_DATE_STR, dateFormatter.print(submissionEvent.getPerformedDate()));
        assertEquals(TestValues.SUBMISSIONEVENT_PERFORMER_ROLE, submissionEvent.getPerformerRole().toString());
        assertEquals(TestValues.SUBMISSION_ID_1, submissionEvent.getSubmission().toString());
        assertEquals(TestValues.SUBMISSIONEVENT_EVENT_TYPE, submissionEvent.getEventType().toString());
        assertEquals(TestValues.SUBMISSIONEVENT_COMMENT, submissionEvent.getComment().toString());
        assertEquals(TestValues.SUBMISSIONEVENT_LINK, submissionEvent.getLink().toString());
        
    }

    /**
     * Simple verification that SubmissionEvent model can be converted to JSON
     * @throws Exception
     */
    @Test
    public void testSubmissionEventToJsonConversion() throws Exception {

        SubmissionEvent submissionEvent = createSubmissionEvent();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonSubmissionEvent = objectMapper.writeValueAsString(submissionEvent);

        JSONObject root = new JSONObject(jsonSubmissionEvent);

        assertEquals(root.getString("@id"),TestValues.SUBMISSIONEVENT_ID);
        assertEquals(root.getString("@type"),"SubmissionEvent");
        assertEquals(root.getString("eventType"),TestValues.SUBMISSIONEVENT_EVENT_TYPE);
        assertEquals(root.getString("performedDate"),TestValues.SUBMISSIONEVENT_PERFORMED_DATE_STR);    
        assertEquals(root.getString("performedBy"),TestValues.USER_ID_1);
        assertEquals(root.getString("performerRole"),TestValues.SUBMISSIONEVENT_PERFORMER_ROLE);
        assertEquals(root.getString("submission"),TestValues.SUBMISSION_ID_1);
        assertEquals(root.getString("comment"),TestValues.SUBMISSIONEVENT_COMMENT);
        assertEquals(root.getString("link"),TestValues.SUBMISSIONEVENT_LINK);    
    }
    
    /**
     * Creates two identical SubmissionEvents and checks the equals and hashcodes match. 
     * Modifies one field on one of the SubmissionEvents and verifies they no longer are 
     * equal or have matching hashcodes.
     * @throws Exception
     */
    @Test
    public void testSubmissionEqualsAndHashCode() throws Exception {

        SubmissionEvent submissionEvent1 = createSubmissionEvent();
        SubmissionEvent submissionEvent2 = createSubmissionEvent();
        
        assertEquals(submissionEvent1,submissionEvent2);
        submissionEvent1.setPerformerRole(SubmissionEvent.PerformerRole.SUBMITTER);
        assertTrue(!submissionEvent1.equals(submissionEvent2));
        
        assertTrue(submissionEvent1.hashCode()!=submissionEvent2.hashCode());
        submissionEvent1 = submissionEvent2;
        assertEquals(submissionEvent1.hashCode(),submissionEvent2.hashCode());
        
    }
    
    /**
     * Test copy constructor creates a valid duplicate that is not the same object
     * @throws Exception
     */
    @Test
    public void testSubmissionEventCopyConstructor() throws Exception {
        SubmissionEvent submissionEvent = createSubmissionEvent();
        SubmissionEvent submissionEventCopy = new SubmissionEvent(submissionEvent);
        assertEquals(submissionEvent, submissionEventCopy);
        
        URI newLink = new URI("different:link");
        submissionEventCopy.setLink(newLink);
        assertEquals(new URI(TestValues.SUBMISSIONEVENT_LINK), submissionEvent.getLink());
        assertEquals(newLink, submissionEventCopy.getLink());
        
        submissionEventCopy.setEventType(EventType.CANCELLED);
        assertEquals(SubmissionEvent.EventType.of(TestValues.SUBMISSIONEVENT_EVENT_TYPE), submissionEvent.getEventType());
        assertEquals(EventType.CANCELLED, submissionEventCopy.getEventType());
    }
    
    private SubmissionEvent createSubmissionEvent() throws Exception {
        SubmissionEvent submissionEvent = new SubmissionEvent();
        submissionEvent.setId(new URI(TestValues.SUBMISSIONEVENT_ID));
        submissionEvent.setEventType(SubmissionEvent.EventType.of(TestValues.SUBMISSIONEVENT_EVENT_TYPE));
        DateTime dt = dateFormatter.parseDateTime(TestValues.SUBMISSIONEVENT_PERFORMED_DATE_STR);
        submissionEvent.setPerformedDate(dt);
        submissionEvent.setPerformedBy(new URI(TestValues.USER_ID_1));
        submissionEvent.setPerformerRole(PerformerRole.PREPARER);
        submissionEvent.setSubmission(new URI(TestValues.SUBMISSION_ID_1));
        submissionEvent.setComment(TestValues.SUBMISSIONEVENT_COMMENT);
        submissionEvent.setLink(new URI(TestValues.SUBMISSIONEVENT_LINK));
        
        return submissionEvent;
    }
    
}
