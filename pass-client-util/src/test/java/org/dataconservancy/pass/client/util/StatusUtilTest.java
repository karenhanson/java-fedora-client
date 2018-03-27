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
package org.dataconservancy.pass.client.util;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import org.dataconservancy.pass.model.Deposit;
import org.dataconservancy.pass.model.Deposit.DepositStatus;
import org.dataconservancy.pass.model.Submission.AggregatedDepositStatus;
import org.dataconservancy.pass.model.Submission.Source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests Status Util
 * @author Karen Hanson
 */
public class StatusUtilTest {
    
    private Set<Deposit> deposits = new HashSet<Deposit>();
    private Deposit deposit1 = new Deposit();
    private Deposit deposit2 = new Deposit();
    private Deposit deposit3 = new Deposit();
    private Deposit deposit4 = new Deposit();    
    
    
    /**
     * Add deposits in variety of stages of progress, should be IN_PROGRESS in all cases
     */
    @Test
    public void calcSubmissionStatusForDepositsInProgressTest() {
        deposit1.setDepositStatus(DepositStatus.SUBMITTED);
        deposits.add(deposit1);        
        
        assertEquals(AggregatedDepositStatus.IN_PROGRESS, StatusUtil.calcAggregatedDepositStatus(deposits, Source.PASS));
        
        deposit2.setDepositStatus(DepositStatus.ACCEPTED);
        deposits.add(deposit2);
        
        assertEquals(AggregatedDepositStatus.IN_PROGRESS, StatusUtil.calcAggregatedDepositStatus(deposits, Source.PASS));
        
        deposit3.setDepositStatus(DepositStatus.REJECTED);
        deposits.add(deposit3);
        
        assertEquals(AggregatedDepositStatus.IN_PROGRESS, StatusUtil.calcAggregatedDepositStatus(deposits, Source.PASS));   
        
        //should be in progress even if a deposit doesn't have a status for some reason
        deposit4.setDepositStatus(null);
        deposits.add(deposit4);
        
        assertEquals(AggregatedDepositStatus.IN_PROGRESS, StatusUtil.calcAggregatedDepositStatus(deposits, Source.PASS));   
    }
    
    
    /**
     * Adds 3 deposits, each one accepted, so should have an aggregated status of ACCEPTED
     * Then adds one that is not accepted to make sure status changes to IN_PROGRESS.
     */
    @Test
    public void calcSubmissionStatusForAcceptedDepositsTest() {
        deposit1.setDepositStatus(DepositStatus.ACCEPTED);
        deposits.add(deposit1);
        
        assertEquals(AggregatedDepositStatus.ACCEPTED,StatusUtil.calcAggregatedDepositStatus(deposits, Source.PASS));
                
        deposit2.setDepositStatus(DepositStatus.ACCEPTED);
        deposits.add(deposit2);
        
        assertEquals(AggregatedDepositStatus.ACCEPTED,StatusUtil.calcAggregatedDepositStatus(deposits, Source.PASS));
        
        deposit3.setDepositStatus(DepositStatus.ACCEPTED);
        deposits.add(deposit3);
        
        assertEquals(AggregatedDepositStatus.ACCEPTED,StatusUtil.calcAggregatedDepositStatus(deposits, Source.PASS));

        deposit4.setDepositStatus(DepositStatus.SUBMITTED);
        deposits.add(deposit4);
        
        assertEquals(AggregatedDepositStatus.IN_PROGRESS,StatusUtil.calcAggregatedDepositStatus(deposits, Source.PASS));        
    }

    
    /**
     * Confirms that a null list returns a status of not started
     */
    @Test
    public void calcSubmissionStatusNullListOkTest() {
        assertEquals(AggregatedDepositStatus.NOT_STARTED,StatusUtil.calcAggregatedDepositStatus(null, Source.PASS));
    }


    /**
     * Confirms that an empty list returns a status of not started
     */
    @Test
    public void calcSubmissionStatusEmptyListOkTest() {
        assertEquals(AggregatedDepositStatus.NOT_STARTED,StatusUtil.calcAggregatedDepositStatus(deposits, Source.PASS));  
    }


    /**
     * Confirms that non-pass submission returns null status
     */
    @Test
    public void calcSubmissionStatusNonPassSourceTest() {
        assertNull(StatusUtil.calcAggregatedDepositStatus(deposits, null)); 
        assertNull(StatusUtil.calcAggregatedDepositStatus(deposits, Source.OTHER)); 
    }
    

    /**
     * A null deposit in the list should return an exception, suggests something has gone wrong
     */
    @Test(expected=RuntimeException.class)
    public void calcSubmissionStatusNullDepositNotOkTest() {
        
        deposit1.setDepositStatus(DepositStatus.SUBMITTED);
        deposits.add(deposit1);        
        
        assertEquals(AggregatedDepositStatus.IN_PROGRESS,StatusUtil.calcAggregatedDepositStatus(deposits, Source.PASS));
        
        deposits.add(null);
        //should throw exception
        StatusUtil.calcAggregatedDepositStatus(deposits, Source.PASS);
        
    }
    
}
