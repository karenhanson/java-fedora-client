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
import static org.junit.Assert.assertTrue;

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
        assertEquals(TestValues.DEPOSIT_STATUS, deposit.getDepositStatus().toString());
        assertEquals(TestValues.DEPOSIT_STATUSREF, deposit.getDepositStatusRef());
        
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
        assertEquals(root.getString("depositStatusRef"),TestValues.DEPOSIT_STATUSREF); 
        assertEquals(root.getString("submission"),TestValues.SUBMISSION_ID_1); 
        assertEquals(root.getString("repository"),TestValues.REPOSITORY_ID_1); 
        assertEquals(root.getString("repositoryCopy"),TestValues.REPOSITORYCOPY_ID_1); 
    }


    /**
     * Verify that we can convert a model object to JSON with JSONLD context
     * and that null values are included in the JSON
     * @throws Exception
     */
    @Test
    public void testDepositToJsonWithContextIncludeNulls() throws Exception {
        
        Deposit deposit = createDeposit();
        deposit.setDepositStatusRef(null);
        deposit.setRepositoryCopy(null);
        
        PassJsonAdapter adapter = new PassJsonAdapterBasic();
        String jsonDeposit = new String(adapter.toJson(deposit, true));
        
        JSONObject root = new JSONObject(jsonDeposit);

        assertEquals(root.getString("@id"),TestValues.DEPOSIT_ID_1.toString());
        assertEquals(root.getString("@type"),PassEntityType.DEPOSIT.getName());
        assertEquals(root.getString("@context"),CONTEXT);
        assertEquals(root.getString("depositStatus"),TestValues.DEPOSIT_STATUS);
        assertTrue(root.has("depositStatusRef")); 
        assertEquals(root.get("depositStatusRef"),null); 
        assertEquals(root.getString("submission"),TestValues.SUBMISSION_ID_1); 
        assertEquals(root.getString("repository"),TestValues.REPOSITORY_ID_1);
        assertTrue(root.has("repositoryCopy"));  
        assertEquals(root.get("repositoryCopy"),null); 
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
        assertEquals(root.getString("submission"),TestValues.SUBMISSION_ID_1);
        assertEquals(root.getString("repository"),TestValues.REPOSITORY_ID_1);
        assertEquals(root.getString("repositoryCopy"),TestValues.REPOSITORYCOPY_ID_1);
    }
        
    private Deposit createDeposit() throws Exception {
        Deposit deposit = new Deposit();
        deposit.setId(new URI(TestValues.DEPOSIT_ID_1));
        deposit.setDepositStatusRef(TestValues.DEPOSIT_STATUSREF);
        deposit.setDepositStatus(DepositStatus.of(TestValues.DEPOSIT_STATUS));
        deposit.setSubmission(new URI(TestValues.SUBMISSION_ID_1));
        deposit.setRepository(new URI(TestValues.REPOSITORY_ID_1));
        deposit.setRepositoryCopy(new URI(TestValues.REPOSITORYCOPY_ID_1));
        return deposit;
    }
    
}
