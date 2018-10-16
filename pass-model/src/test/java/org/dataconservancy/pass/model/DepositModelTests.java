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

import org.dataconservancy.pass.model.Deposit.DepositStatus;
import org.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Model has been annotated with JSON tags. These tests do a simple check to ensure the
 * Jackson integration is functional and the equals / hashcode functions work
 * @author Karen Hanson
 */
public class DepositModelTests {
            
    /**
     * Simple verification that JSON file can be converted to Deposit model
     * @throws Exception
     */
    @Test
    public void testDepositFromJsonConversion() throws Exception {
        
        InputStream json = DepositModelTests.class.getResourceAsStream("/deposit.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Deposit deposit = objectMapper.readValue(json, Deposit.class);
        
        assertEquals(TestValues.DEPOSIT_ID_1, deposit.getId().toString());
        assertEquals(DepositStatus.of(TestValues.DEPOSIT_STATUS), deposit.getDepositStatus());
        assertEquals(TestValues.DEPOSIT_STATUSREF, deposit.getDepositStatusRef());
        assertEquals(TestValues.SUBMISSION_ID_1, deposit.getSubmission().toString());
        assertEquals(TestValues.REPOSITORY_ID_1, deposit.getRepository().toString());
        assertEquals(TestValues.REPOSITORYCOPY_ID_1, deposit.getRepositoryCopy().toString());
        
    }

    /**
     * Simple verification that Deposit model can be converted to JSON
     * @throws Exception
     */
    @Test
    public void testDepositToJsonConversion() throws Exception {

        Deposit deposit = createDeposit();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonDeposit = objectMapper.writeValueAsString(deposit);

        JSONObject root = new JSONObject(jsonDeposit);

        assertEquals(root.getString("@id"),TestValues.DEPOSIT_ID_1);
        assertEquals(root.getString("@type"),"Deposit");
        assertEquals(root.getString("depositStatus"),TestValues.DEPOSIT_STATUS);
        assertEquals(root.getString("depositStatusRef"),TestValues.DEPOSIT_STATUSREF);
        assertEquals(root.getString("submission"),TestValues.SUBMISSION_ID_1);
        assertEquals(root.getString("repositoryCopy"),TestValues.REPOSITORYCOPY_ID_1);   
        assertEquals(root.getString("repositoryCopy"),TestValues.REPOSITORYCOPY_ID_1);   
    }
    
    /**
     * Creates two identical Deposits and checks the equals and hashcodes match. 
     * Modifies one field on one of the deposits and verifies they no longer are 
     * equal or have matching hashcodes.
     * @throws Exception
     */
    @Test
    public void testDepositEqualsAndHashCode() throws Exception {

        Deposit deposit1 = createDeposit();
        Deposit deposit2 = createDeposit();
        
        assertEquals(deposit1,deposit2);
        deposit1.setDepositStatusRef("different");
        assertTrue(!deposit1.equals(deposit2));
        
        assertTrue(deposit1.hashCode()!=deposit2.hashCode());
        deposit1 = deposit2;
        assertEquals(deposit1.hashCode(),deposit2.hashCode());
        
    }
    
    /**
     * Test copy constructor creates a valid duplicate that is not the same object
     * @throws Exception
     */
    @Test
    public void testDepositCopyConstructor() throws Exception {
        Deposit deposit = createDeposit();
        Deposit depositCopy = new Deposit(deposit);
        assertEquals(deposit, depositCopy);
        
        depositCopy.setDepositStatus(DepositStatus.REJECTED);
        assertEquals(DepositStatus.of(TestValues.DEPOSIT_STATUS), deposit.getDepositStatus());
        assertEquals(DepositStatus.REJECTED, depositCopy.getDepositStatus());
        
        depositCopy.setId(new URI(TestValues.DEPOSIT_ID_2));
        assertEquals(TestValues.DEPOSIT_ID_1, deposit.getId().toString());
        assertEquals(TestValues.DEPOSIT_ID_2, depositCopy.getId().toString());
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
