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

/**
 * Constants used in test data
 * @author Karen Hanson
 * @version $Id$
 */
public class TestValues {
    public static final String DEPOSIT_ID_1 = "https://example.org/fedora/deposits/1";
    public static final String DEPOSIT_ID_2 = "https://example.org/fedora/deposits/2";
    public static final String FUNDER_ID_1 = "https://example.org/fedora/funders/1";
    public static final String FUNDER_ID_2 = "https://example.org/fedora/funders/2";
    public static final String GRANT_ID_1 = "https://example.org/fedora/grants/1";
    public static final String GRANT_ID_2 = "https://example.org/fedora/grants/2";
    public static final String JOURNAL_ID_1 = "https://example.org/fedora/journals/1";
    public static final String JOURNAL_ID_2 = "https://example.org/fedora/journals/2";
    public static final String PERSON_ID_1 = "https://example.org/fedora/people/1";
    public static final String PERSON_ID_2 = "https://example.org/fedora/people/2";
    public static final String PERSON_ID_3 = "https://example.org/fedora/people/3";
    public static final String POLICY_ID_1 = "https://example.org/fedora/policies/1";
    public static final String PUBLISHER_ID_1 = "https://example.org/fedora/publishers/1";
    public static final String REPOSITORY_ID_1 = "https://example.org/fedora/repositories/1";
    public static final String REPOSITORY_ID_2 = "https://example.org/fedora/repositories/2";
    public static final String SUBMISSION_ID_1 = "https://example.org/fedora/submissions/1";
    public static final String SUBMISSION_ID_2 = "https://example.org/fedora/submissions/2";
    public static final String USER_ID_1 = "https://example.org/fedora/users/1";
    public static final String WORKFLOW_ID_1 = "https://example.org/fedora/workflows/1";

    public static final Deposit.Status DEPOSIT_STATUS = Deposit.Status.PREPARED;
    public static final String DEPOSIT_ASSIGNEDID = "PMC12345";
    public static final String DEPOSIT_ACCESSURL = "https://www.ncbi.nlm.nih.gov/pmc/articles/PMC12345/";
    public static final Boolean DEPOSIT_REQUESTED = true;
        
    public static final String FUNDER_NAME = "Funder A";
    public static final String FUNDER_URL = "https://nih.gov";

    public static final String GRANT_AWARD_NUMBER = "RH1234CDE";
    public static final Grant.AwardStatus GRANT_STATUS = Grant.AwardStatus.ACTIVE;
    public static final String GRANT_STATUS_STR = "active";
    public static final String GRANT_LOCAL_AWARDID = "ABC123";
    public static final String GRANT_PROJECT_NAME = "Project A";
    public static final String GRANT_AWARD_DATE_STR = "2018-01-01T00:00:00.000Z";
    public static final String GRANT_START_DATE_STR = "2018-04-01T00:00:00.000Z";
    public static final String GRANT_END_DATE_STR = "2020-04-30T00:00:00.000Z";
    
    public static final String JOURNAL_NAME = "Test Journal";
    public static final String JOURNAL_ISSN_1 = "1234-5678";
    public static final String JOURNAL_ISSN_2 = "5678-1234";
    public static final String JOURNAL_NLMTA = "TJ";
    public static final PmcParticipation JOURNAL_PMCPARTICIPATION = PmcParticipation.B;
        
    public static final String PERSON_FIRST_NAME = "June";
    public static final String PERSON_MIDDLE_NAME = "Marie";
    public static final String PERSON_LAST_NAME = "Smith";
    public static final String PERSON_DISPLAY_NAME = "June Smith";
    public static final String PERSON_EMAIL = "js@example.com";
    public static final String PERSON_INSTITUTIONAL_ID = "jms001";
    public static final String PERSON_ORCID_ID = "https://orcid.org/0000-1111-2222-3333";
    public static final String PERSON_AFFILIATION = "Johns Hopkins University";
    
    public static final String POLICY_TITLE = "Policy A";
    public static final String POLICY_DESCRIPTION = "You must submit to any OA repo";
    public static final Boolean POLICY_ISDEFAULT = false;
        
    public static final String PUBLISHER_NAME = "Publisher A";
    public static final PmcParticipation PUBLISHER_PMCPARTICIPATION = PmcParticipation.A;

    public static final String REPOSITORY_NAME = "Repository A";
    public static final String REPOSITORY_DESCRIPTION = "An OA repository run by funder A";
    public static final String REPOSITORY_URL = "https://repo-example.org/";
    
    public static final Submission.Status SUBMISSION_STATUS = Submission.Status.IN_PROGRESS;
    public static final String SUBMISSION_TITLE = "Some article";
    public static final String SUBMISSION_ABSTRACT = "An article about something";
    public static final String SUBMISSION_DOI = "10.0101/1234abcd";
    public static final String SUBMISSION_VOLUME = "5";
    public static final String SUBMISSION_ISSUE = "123";
    public static final String SUBMISSION_DATE_STR = "2018-01-05T12:12:12.000Z";
    public static final Submission.Source SUBMISSION_SOURCE = Submission.Source.OTHER;

    public static final String USER_NAME = "am12345";
    public static final User.Role USER_ROLE = User.Role.ADMIN;
    
    public static final String WORKFLOW_NAME = "A workflow";
    public static final String WORKFLOW_STEP = "1";
    public static final String WORKFLOW_STEPS = "1,3,4,5,6";
    
}
