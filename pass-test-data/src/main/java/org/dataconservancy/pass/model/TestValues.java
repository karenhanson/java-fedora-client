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
    public static final String CONTRIBUTOR_ID_1 = "https://example.org/fedora/contributors/1";
    public static final String DEPOSIT_ID_1 = "https://example.org/fedora/deposits/1";
    public static final String DEPOSIT_ID_2 = "https://example.org/fedora/deposits/2";
    public static final String FILE_ID_1 = "https://example.org/fedora/files/1";
    public static final String FUNDER_ID_1 = "https://example.org/fedora/funders/1";
    public static final String FUNDER_ID_2 = "https://example.org/fedora/funders/2";
    public static final String GRANT_ID_1 = "https://example.org/fedora/grants/1";
    public static final String GRANT_ID_2 = "https://example.org/fedora/grants/2";
    public static final String JOURNAL_ID_1 = "https://example.org/fedora/journals/1";
    public static final String JOURNAL_ID_2 = "https://example.org/fedora/journals/2";
    public static final String INSTITUTION_ID_1 = "https://example.org/fedora/institutions/1";
    public static final String POLICY_ID_1 = "https://example.org/fedora/policies/1";
    public static final String PUBLICATION_ID_1 = "https://example.org/fedora/publications/1";
    public static final String PUBLISHER_ID_1 = "https://example.org/fedora/publishers/1";
    public static final String REPOSITORY_ID_1 = "https://example.org/fedora/repositories/1";
    public static final String REPOSITORY_ID_2 = "https://example.org/fedora/repositories/2";
    public static final String REPOSITORYCOPY_ID_1 = "https://example.org/fedora/repositoryCopies/1";
    public static final String SUBMISSION_ID_1 = "https://example.org/fedora/submissions/1";
    public static final String SUBMISSION_ID_2 = "https://example.org/fedora/submissions/2";
    public static final String USER_ID_1 = "https://example.org/fedora/users/1";
    public static final String USER_ID_2 = "https://example.org/fedora/users/2";
    public static final String USER_ID_3 = "https://example.org/fedora/users/3";


    public static final String CONTRIBUTOR_ROLE_1 = "first-author";
    public static final String CONTRIBUTOR_ROLE_2 = "author";
    
    public static final String DEPOSIT_STATUS = "submitted";
    public static final String DEPOSIT_STATUSREF = "http://depositstatusref.example/abc";

    public static final String FILE_NAME = "article.pdf";
    public static final String FILE_URI = "https://someplace.dl/a/b/c/article.pdf";
    public static final String FILE_DESCRIPTION = "The file is an article";
    public static final String FILE_ROLE = "manuscript";
    public static final String FILE_MIMETYPE = "application/pdf";
    
    public static final String FUNDER_NAME = "Funder A";
    public static final String FUNDER_URL = "https://nih.gov";
    public static final String FUNDER_LOCALKEY = "A12345";

    public static final String GRANT_AWARD_NUMBER = "RH1234CDE";
    public static final String GRANT_STATUS = "active";
    public static final String GRANT_LOCALKEY = "ABC123";
    public static final String GRANT_PROJECT_NAME = "Project A";
    public static final String GRANT_AWARD_DATE_STR = "2018-01-01T00:00:00.000Z";
    public static final String GRANT_START_DATE_STR = "2018-04-01T00:00:00.000Z";
    public static final String GRANT_END_DATE_STR = "2020-04-30T00:00:00.000Z";
    
    public static final String JOURNAL_NAME = "Test Journal";
    public static final String JOURNAL_ISSN_1 = "1234-5678";
    public static final String JOURNAL_ISSN_2 = "5678-1234";
    public static final String JOURNAL_NLMTA = "TJ";
    public static final String JOURNAL_PMCPARTICIPATION = "B";
    
    public static final String POLICY_TITLE = "Policy A";
    public static final String POLICY_DESCRIPTION = "You must submit to any OA repo";
    public static final String POLICY_URL = "https://somefunder.org/policy";
    
    public static final String PUBLICATION_TITLE = "Some article";
    public static final String PUBLICATION_ABSTRACT = "An article about something";
    public static final String PUBLICATION_PMID = "12345678";
    public static final String PUBLICATION_DOI = "10.0101/1234abcd";
    public static final String PUBLICATION_VOLUME = "5";
    public static final String PUBLICATION_ISSUE = "123";
        
    public static final String PUBLISHER_NAME = "Publisher A";
    public static final String PUBLISHER_PMCPARTICIPATION = "A";

    public static final String REPOSITORY_NAME = "Repository A";
    public static final String REPOSITORY_DESCRIPTION = "An OA repository run by funder A";
    public static final String REPOSITORY_URL = "https://repo-example.org/";
    
    public static final String REPOSITORYCOPY_STATUS = "accepted";
    public static final String REPOSITORYCOPY_EXTERNALID_1 = "PMC12345";
    public static final String REPOSITORYCOPY_EXTERNALID_2 = "NIHMS1234";
    public static final String REPOSITORYCOPY_ACCESSURL = "https://www.ncbi.nlm.nih.gov/pmc/articles/PMC12345/";
    
    public static final String SUBMISSION_STATUS = "in-progress";
    public static final String SUBMISSION_DATE_STR = "2018-01-05T12:12:12.000Z";
    public static final String SUBMISSION_SOURCE = "other";

    public static final String USER_NAME = "am12345";
    public static final String USER_FIRST_NAME = "June";
    public static final String USER_MIDDLE_NAME = "Marie";
    public static final String USER_LAST_NAME = "Smith";
    public static final String USER_DISPLAY_NAME = "June Smith";
    public static final String USER_EMAIL = "js@example.com";
    public static final String USER_INSTITUTIONAL_ID = "jms001";
    public static final String USER_LOCALKEY = "abcdef";
    public static final String USER_ORCID_ID = "https://orcid.org/0000-1111-2222-3333";
    public static final String USER_AFFILIATION = "Johns Hopkins University";
    public static final String USER_ROLE_1 = "admin";
    public static final String USER_ROLE_2 = "submitter";
        
}
