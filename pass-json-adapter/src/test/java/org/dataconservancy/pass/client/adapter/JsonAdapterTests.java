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
package org.dataconservancy.pass.client.adapter;

import java.io.InputStream;

import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.dataconservancy.pass.client.PassJsonAdapter;
import org.dataconservancy.pass.model.Deposit;
import org.json.JSONObject;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Karen Hanson
 */
public class JsonAdapterTests {

    private final static String CONTEXT_PROPKEY = "pass.jsonld.context";
    private final static String CONTEXT= "http://testurl.org/context.jsonld";
    
    private static final String DEPOSIT_ID_1 = "https://example.org/fedora/deposits/1";
    private static final String REPOSITORY_ID_1 = "https://example.org/fedora/repositories/1";
    private static final Deposit.Status DEPOSIT_STATUS = Deposit.Status.PREPARED;
    private static final String DEPOSIT_ASSIGNEDID = "PMC12345";
    private static final String DEPOSIT_ACCESSURL = "https://www.ncbi.nlm.nih.gov/pmc/articles/PMC12345/";
    private static final Boolean DEPOSIT_REQUESTED = true;
    
    @Before
    public void startup(){
        System.setProperty(CONTEXT_PROPKEY, CONTEXT);
    }
    
    @After
    public void close(){
        System.clearProperty(CONTEXT_PROPKEY);
    }
    
    /**
     * Simple verification that JSON file can be converted to Deposit model
     * @throws Exception
     */
    public void testDepositJsonToModel() {
        String filepath = "/deposit.json";
        InputStream compactJsonLd = JsonAdapterTests.class.getResourceAsStream(filepath);
        
        PassJsonAdapter adapter = new PassJsonAdapterBasic();
        Deposit deposit = (Deposit) adapter.toModel(compactJsonLd, Deposit.class);

        assertEquals(DEPOSIT_ID_1, deposit.getId());
        assertEquals(DEPOSIT_STATUS, deposit.getStatus());
        assertEquals(DEPOSIT_ASSIGNEDID, deposit.getAssignedId());
        assertEquals(DEPOSIT_ACCESSURL, deposit.getAccessUrl());
        assertEquals(DEPOSIT_REQUESTED, deposit.getRequested());  
        
    }


    /**
     * Verify that we can convert a model object to JSON with JSONLD context
     * @throws Exception
     */
    @Test
    public void testDepositToJsonWithContext() throws Exception {
        
        PassJsonAdapter adapter = new PassJsonAdapterBasic();
        String jsonDeposit = new String(adapter.toJson(createDeposit(), true));
        
        JSONObject root = new JSONObject(jsonDeposit);

        assertEquals(root.getString("@id"),DEPOSIT_ID_1);
        assertEquals(root.getString("@type"),"Deposit");
        assertEquals(root.getString("@context"),CONTEXT);
        assertEquals(root.getString("status"),DEPOSIT_STATUS.name().toLowerCase());
        assertEquals(root.getString("repository"),REPOSITORY_ID_1);
        assertEquals(root.getString("assignedId"),DEPOSIT_ASSIGNEDID);
        assertEquals(root.getString("accessUrl"),DEPOSIT_ACCESSURL);
        assertEquals(root.getBoolean("requested"),DEPOSIT_REQUESTED);     
    }

    
    /**
     * Verify that we can convert a model object to JSON minus the context
     * @throws Exception
     */
    @Test
    public void testDepositToJsonWithoutContext() throws Exception {
        PassJsonAdapter adapter = new PassJsonAdapterBasic();
        String jsonDeposit = new String(adapter.toJson(createDeposit(), false));

        JSONObject root = new JSONObject(jsonDeposit);

        assertEquals(root.getString("@id"),DEPOSIT_ID_1);
        assertEquals(root.getString("@type"),"Deposit");
        assertEquals(root.has("@context"),false);
        assertEquals(root.getString("status"),DEPOSIT_STATUS.name().toLowerCase());
        assertEquals(root.getString("repository"),REPOSITORY_ID_1);
        assertEquals(root.getString("assignedId"),DEPOSIT_ASSIGNEDID);
        assertEquals(root.getString("accessUrl"),DEPOSIT_ACCESSURL);
        assertEquals(root.getBoolean("requested"),DEPOSIT_REQUESTED);  
    }
    
    
    private Deposit createDeposit() throws Exception {
        Deposit deposit = new Deposit();
        deposit.setId(new URI(DEPOSIT_ID_1));
        deposit.setStatus(DEPOSIT_STATUS);
        deposit.setRepository(new URI(REPOSITORY_ID_1));
        deposit.setAssignedId(DEPOSIT_ASSIGNEDID);
        deposit.setAccessUrl(DEPOSIT_ACCESSURL);
        deposit.setRequested(DEPOSIT_REQUESTED);
        return deposit;
    }
    
}
