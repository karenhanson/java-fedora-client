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
import org.dataconservancy.pass.model.Deposit.DepositStatus;
import org.dataconservancy.pass.model.PassEntityType;
import org.dataconservancy.pass.model.TestValues;
import org.json.JSONObject;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Karen Hanson
 */
public class JsonAdapterTests {

    private final static String CONTEXT_PROPKEY = "pass.jsonld.context";
    private final static String CONTEXT= "http://testurl.org/context.jsonld";
    
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
    @Test
    public void testDepositJsonToModel() {
        String filepath = "/deposit.json";
        InputStream compactJsonLd = JsonAdapterTests.class.getResourceAsStream(filepath);
        
        PassJsonAdapter adapter = new PassJsonAdapterBasic();
        Deposit deposit = (Deposit) adapter.toModel(compactJsonLd, Deposit.class);

        assertEquals(TestValues.DEPOSIT_ID_1, deposit.getId().toString());
        assertEquals(TestValues.DEPOSIT_STATUS, deposit.getDepositStatus().getValue());
        assertEquals(TestValues.DEPOSIT_ASSIGNEDID, deposit.getAssignedId());
        assertEquals(TestValues.DEPOSIT_ACCESSURL, deposit.getAccessUrl());
        assertEquals(TestValues.DEPOSIT_REQUESTED, deposit.getRequested());  
        
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

        assertEquals(root.getString("@id"),TestValues.DEPOSIT_ID_1.toString());
        assertEquals(root.getString("@type"),PassEntityType.DEPOSIT.getName());
        assertEquals(root.getString("@context"),CONTEXT);
        assertEquals(root.getString("depositStatus"),TestValues.DEPOSIT_STATUS);
        assertEquals(root.getString("repository"),TestValues.REPOSITORY_ID_1);
        assertEquals(root.getString("assignedId"),TestValues.DEPOSIT_ASSIGNEDID);
        assertEquals(root.getString("accessUrl"),TestValues.DEPOSIT_ACCESSURL);
        assertEquals(root.getBoolean("requested"),TestValues.DEPOSIT_REQUESTED);     
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

        assertEquals(root.getString("@id"),TestValues.DEPOSIT_ID_1);
        assertEquals(root.getString("@type"),PassEntityType.DEPOSIT.getName());
        assertEquals(root.has("@context"),false);
        assertEquals(root.getString("depositStatus"),TestValues.DEPOSIT_STATUS);
        assertEquals(root.getString("repository"),TestValues.REPOSITORY_ID_1);
        assertEquals(root.getString("assignedId"),TestValues.DEPOSIT_ASSIGNEDID);
        assertEquals(root.getString("accessUrl"),TestValues.DEPOSIT_ACCESSURL);
        assertEquals(root.getBoolean("requested"),TestValues.DEPOSIT_REQUESTED);  
    }
        
    private Deposit createDeposit() throws Exception {
        Deposit deposit = new Deposit();
        deposit.setId(new URI(TestValues.DEPOSIT_ID_1));
        deposit.setDepositStatus(DepositStatus.of(TestValues.DEPOSIT_STATUS));
        deposit.setRepository(new URI(TestValues.REPOSITORY_ID_1));
        deposit.setAssignedId(TestValues.DEPOSIT_ASSIGNEDID);
        deposit.setAccessUrl(TestValues.DEPOSIT_ACCESSURL);
        deposit.setRequested(TestValues.DEPOSIT_REQUESTED);
        return deposit;
    }
    
}
