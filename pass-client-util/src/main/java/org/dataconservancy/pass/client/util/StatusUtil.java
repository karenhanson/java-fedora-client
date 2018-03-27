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

import java.util.Set;

import org.dataconservancy.pass.model.Deposit;
import org.dataconservancy.pass.model.Submission.AggregatedDepositStatus;
import org.dataconservancy.pass.model.Submission.Source;

import static org.dataconservancy.pass.model.Deposit.DepositStatus.ACCEPTED;

/**
 * Utility for Status logic 
 * @author Karen Hanson
 */
public class StatusUtil {
    
    /**
     * Calculates the AggregatedDepositStatus based on statuses in current list of Deposits and Submission.source
     * If deposits are empty or we are missing a required deposit, the status will be NOT_STARTED if source is PASS, otherwise null.
     * If all deposits are accepted, the status is ACCEPTED
     * Every other combination of statuses is In Progress
     * 
     * @param deposits - Set of Deposits
     * @param source - the Submission.source value
     * @return value for Submission.aggregatedDepositStatus
     */
    public static AggregatedDepositStatus calcAggregatedDepositStatus(Set<Deposit> deposits, Source source) {
        if (source==null || !source.equals(Source.PASS)) { //status only needs to be calculated when Submission created through PASS
            return null;
        }
        if (deposits==null || deposits.size()==0) {
            return AggregatedDepositStatus.NOT_STARTED;
        }
        boolean allDepositsAccepted = true;
        for (Deposit deposit : deposits) {
            if (deposit==null) {
                // while null deposit list can indicate no Deposits, a null row suggests something went wrong with the data
                throw new RuntimeException("A Deposit in the list was null. This may indicate a system problem. Please verify your deposits.");
            }
            if (deposit.getDepositStatus()==null || !deposit.getDepositStatus().equals(ACCEPTED)) {
                allDepositsAccepted = false;
            }
        }
        
        if (allDepositsAccepted) {
            return AggregatedDepositStatus.ACCEPTED;
        } else {
            return AggregatedDepositStatus.IN_PROGRESS;
        }
    }
    
}
