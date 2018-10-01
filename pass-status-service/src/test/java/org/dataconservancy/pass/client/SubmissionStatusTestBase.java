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
package org.dataconservancy.pass.client;

import java.net.URI;

import org.junit.Before;

import org.dataconservancy.pass.model.Deposit;
import org.dataconservancy.pass.model.Deposit.DepositStatus;
import org.dataconservancy.pass.model.RepositoryCopy;
import org.dataconservancy.pass.model.RepositoryCopy.CopyStatus;
import org.dataconservancy.pass.model.SubmissionEvent;
import org.dataconservancy.pass.model.SubmissionEvent.EventType;
import org.joda.time.DateTime;

/**
 * Some basic methods to support Submission Status testing.
 * @author Karen Hanson
 */
public abstract class SubmissionStatusTestBase {

    //some test URIs 
    protected URI repo1Id;
    protected URI repo2Id;
    protected URI repo3Id;
    protected URI publicationId;
    protected URI deposit1Id;
    protected URI deposit2Id;
    protected URI repoCopy1Id;
    protected URI repoCopy2Id;
    protected URI subEvent1Id;
    protected URI subEvent2Id;
    
    
    @Before
    public void initiate() throws Exception {
        repo1Id = new URI("repository:1");
        repo2Id = new URI("repository:2");
        repo3Id = new URI("repository:3");
        publicationId = new URI("publications:1");
        deposit1Id = new URI("deposits:1");
        deposit2Id = new URI("deposits:2");
        repoCopy1Id = new URI("repositoryCopies:1");
        repoCopy2Id = new URI("repositoryCopies:2");
        subEvent1Id = new URI("submissionEvents:1");
        subEvent2Id = new URI("submissionEvents:2");
    }
    
    protected Deposit deposit(DepositStatus status, URI repoUri) {
        Deposit d = new Deposit();
        d.setDepositStatus(status);
        d.setRepository(repoUri);
        return d;
    }


    protected RepositoryCopy repoCopy(CopyStatus status, URI repoUri) {
        RepositoryCopy r = new RepositoryCopy();
        r.setCopyStatus(status);
        r.setRepository(repoUri);
        return r;
    }
    

    protected SubmissionEvent submissionEvent(DateTime dt, EventType eventType) {
        SubmissionEvent event = new SubmissionEvent();
        event.setPerformedDate(dt);
        event.setEventType(eventType);
        return event;
    }
    
}
