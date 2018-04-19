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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import org.dataconservancy.pass.model.Grant.AwardStatus;
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
public class GrantModelTests {

    private DateTimeFormatter dateFormatter = ISODateTimeFormat.dateTime().withZoneUTC();
    /**
     * Simple verification that JSON file can be converted to Grant model
     * @throws Exception
     */
    @Test
    public void testGrantFromJsonConversion() throws Exception {
        
        InputStream json = GrantModelTests.class.getResourceAsStream("/grant.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Grant grant = objectMapper.readValue(json, Grant.class);
        
        assertEquals(TestValues.GRANT_ID_1, grant.getId().toString());
        assertEquals("Grant", grant.getType());
        assertEquals(TestValues.GRANT_AWARD_NUMBER, grant.getAwardNumber());
        assertEquals(TestValues.GRANT_STATUS, grant.getAwardStatus().getValue());
        assertEquals(TestValues.GRANT_LOCAL_AWARDID, grant.getLocalAwardId());
        assertEquals(TestValues.GRANT_PROJECT_NAME, grant.getProjectName());
        assertEquals(TestValues.FUNDER_ID_1, grant.getPrimaryFunder().toString());
        assertEquals(TestValues.FUNDER_ID_2, grant.getDirectFunder().toString());
        assertEquals(TestValues.USER_ID_1, grant.getPi().toString());
        assertEquals(TestValues.USER_ID_2, grant.getCoPis().get(0).toString());
        assertEquals(TestValues.USER_ID_3, grant.getCoPis().get(1).toString());
        assertEquals(TestValues.GRANT_AWARD_DATE_STR, dateFormatter.print(grant.getAwardDate()));
        assertEquals(TestValues.GRANT_START_DATE_STR, dateFormatter.print(grant.getStartDate()));
        assertEquals(TestValues.GRANT_END_DATE_STR, dateFormatter.print(grant.getEndDate()));
        assertEquals(TestValues.SUBMISSION_ID_1, grant.getSubmissions().get(0).toString());
        assertEquals(TestValues.SUBMISSION_ID_2, grant.getSubmissions().get(1).toString());
        
    }

    /**
     * Simple verification that Grant model can be converted to JSON
     * @throws Exception
     */
    @Test
    public void testGrantToJsonConversion() throws Exception {

        Grant grant = createGrant();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonGrant = objectMapper.writeValueAsString(grant);
        
        JSONObject root = new JSONObject(jsonGrant);
        
        assertEquals(root.getString("@id"),TestValues.GRANT_ID_1);
        assertEquals(root.getString("@type"),"Grant");
        assertEquals(root.getString("awardNumber"),TestValues.GRANT_AWARD_NUMBER);
        assertEquals(root.getString("awardStatus"),TestValues.GRANT_STATUS);
        assertEquals(root.getString("localAwardId"),TestValues.GRANT_LOCAL_AWARDID);
        assertEquals(root.getString("projectName"),TestValues.GRANT_PROJECT_NAME);
        assertEquals(root.getString("primaryFunder"),TestValues.FUNDER_ID_1);
        assertEquals(root.getString("directFunder"),TestValues.FUNDER_ID_2);
        assertEquals(root.getString("pi"),TestValues.CONTRIBUTOR_ID_1);
        assertEquals(root.getJSONArray("coPis").get(0),TestValues.USER_ID_2);
        assertEquals(root.getJSONArray("coPis").get(1),TestValues.USER_ID_3);
        assertEquals(root.getString("awardDate").toString(),TestValues.GRANT_AWARD_DATE_STR);
        assertEquals(root.getString("startDate").toString(),TestValues.GRANT_START_DATE_STR);
        assertEquals(root.getString("endDate").toString(),TestValues.GRANT_END_DATE_STR);
        assertEquals(root.getJSONArray("submissions").get(0),TestValues.SUBMISSION_ID_1);
        assertEquals(root.getJSONArray("submissions").get(1),TestValues.SUBMISSION_ID_2);
    }
    
    /**
     * Creates two identical Grants and checks the equals and hashcodes match. 
     * Modifies one field on one of the Grants and verifies they no longer are 
     * equal or have matching hashcodes.
     * @throws Exception
     */
    @Test
    public void testGrantEqualsAndHashCode() throws Exception {

        Grant grant1 = createGrant();
        Grant grant2 = createGrant();

        assertEquals(grant1,grant2);
        assertEquals(grant1.hashCode(),grant2.hashCode());
        grant1.setAwardNumber("different");
        assertTrue(!grant1.equals(grant2));
        assertTrue(grant1.hashCode()!=grant2.hashCode());
        
    }
    
    private Grant createGrant() throws Exception {
        Grant grant = new Grant();
        grant.setId(new URI(TestValues.GRANT_ID_1));
        grant.setAwardNumber(TestValues.GRANT_AWARD_NUMBER);
        grant.setAwardStatus(AwardStatus.of(TestValues.GRANT_STATUS));
        grant.setLocalAwardId(TestValues.GRANT_LOCAL_AWARDID);
        grant.setProjectName(TestValues.GRANT_PROJECT_NAME);
        grant.setPrimaryFunder(new URI(TestValues.FUNDER_ID_1));
        grant.setDirectFunder(new URI(TestValues.FUNDER_ID_2));
        grant.setPi(new URI(TestValues.CONTRIBUTOR_ID_1));
        List<URI> coPis = new ArrayList<URI>();
        coPis.add(new URI(TestValues.USER_ID_2));
        coPis.add(new URI(TestValues.USER_ID_3));
        grant.setCoPis(coPis);

        DateTime dt = dateFormatter.parseDateTime(TestValues.GRANT_AWARD_DATE_STR);
        grant.setAwardDate(dt);
        dt = dateFormatter.parseDateTime(TestValues.GRANT_START_DATE_STR);
        grant.setStartDate(dt);
        dt = dateFormatter.parseDateTime(TestValues.GRANT_END_DATE_STR);
        grant.setEndDate(dt);
        List<URI> submissions = new ArrayList<URI>();
        submissions.add(new URI(TestValues.SUBMISSION_ID_1));
        submissions.add(new URI(TestValues.SUBMISSION_ID_2));
        grant.setSubmissions(submissions);
        
        return grant;
    }
    
}
