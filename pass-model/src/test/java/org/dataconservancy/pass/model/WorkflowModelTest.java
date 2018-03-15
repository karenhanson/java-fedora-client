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

import org.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Model has been annotated with JSON tags. These tests do a simple check to ensure the
 * Jackson integration is functional and the equals / hashcode functions work
 * @author Karen Hanson
 */
public class WorkflowModelTest {
            
    /**
     * Simple verification that JSON file can be converted to Workflow model
     * @throws Exception
     */
    @Test
    public void testWorkflowFromJsonConversion() throws Exception {
        
        InputStream json = WorkflowModelTest.class.getResourceAsStream("/workflow.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Workflow workflow = objectMapper.readValue(json, Workflow.class);
        
        assertEquals(TestValues.WORKFLOW_ID_1, workflow.getId().toString());
        assertEquals("Workflow", workflow.getType());
        assertEquals(TestValues.WORKFLOW_NAME, workflow.getName());
        assertEquals(TestValues.WORKFLOW_STEP, workflow.getStep());
        assertEquals(TestValues.WORKFLOW_STEPS, workflow.getSteps());
    }

    /**
     * Simple verification that Workflow model can be converted to JSON
     * @throws Exception
     */
    @Test
    public void testWorkflowToJsonConversion() throws Exception {

        Workflow workflow = createWorkflow();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonWorkflow = objectMapper.writeValueAsString(workflow);

        JSONObject root = new JSONObject(jsonWorkflow);

        assertEquals(root.getString("@id"),TestValues.WORKFLOW_ID_1);
        assertEquals(root.getString("@type"),"Workflow");
        assertEquals(root.getString("name"),TestValues.WORKFLOW_NAME);
        assertEquals(root.getString("step"),TestValues.WORKFLOW_STEP);
        assertEquals(root.getString("steps"),TestValues.WORKFLOW_STEPS);        
    }
    
    /**
     * Creates two identical Workflows and checks the equals and hashcodes match. 
     * Modifies one field on one of the workflows and verifies they no longer are 
     * equal or have matching hashcodes.
     * @throws Exception
     */
    @Test
    public void testWorkflowEqualsAndHashCode() throws Exception {

        Workflow workflow1 = createWorkflow();
        Workflow workflow2 = createWorkflow();
        
        assertEquals(workflow1,workflow2);
        workflow1.setName("different");
        assertTrue(!workflow1.equals(workflow2));
        
        assertTrue(workflow1.hashCode()!=workflow2.hashCode());
        workflow1 = workflow2;
        assertEquals(workflow1.hashCode(),workflow2.hashCode());
        
    }
    
    private Workflow createWorkflow() throws Exception {
        Workflow workflow = new Workflow();
        workflow.setId(new URI(TestValues.WORKFLOW_ID_1));
        workflow.setName(TestValues.WORKFLOW_NAME);
        workflow.setStep(TestValues.WORKFLOW_STEP);
        workflow.setSteps(TestValues.WORKFLOW_STEPS);
        
        return workflow;
    }
    
}
