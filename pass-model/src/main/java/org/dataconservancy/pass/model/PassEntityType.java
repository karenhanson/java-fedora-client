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
 * Defines names of entity types and their plurals
 * @author Karen Hanson
 */
public enum PassEntityType {
    
    /** Contributor */
    CONTRIBUTOR ("Contributor", "contributors"),
    
    /** Deposit */
    DEPOSIT ("Deposit", "deposits"),
    
    /** File */
    FILE ("File", "files"),
    
    /** Funder */
    FUNDER ("Funder", "funders"),
    
    /** Grant */
    GRANT ("Grant", "grants"),
    
    /** Journal */
    JOURNAL ("Journal", "journals"),
    
    /** Policy */
    POLICY ("Policy", "policies"),
    
    /** Publication */
    PUBLICATION ("Publication", "publications"),
    
    /** Publisher */
    PUBLISHER ("Publisher", "publishers"),
    
    /** Repository */
    REPOSITORY ("Repository", "repositories"),
    
    /** Repository copy */
    REPOSITORY_COPY ("RepositoryCopy", "repositoryCopies"),
    
    /** Submission */
    SUBMISSION ("Submission", "submissions"),
    
    /** Submission event */
    SUBMISSION_EVENT ("SubmissionEvent", "submissionEvents"),
    
    /** User */
    USER ("User", "users");
    
    private String name;
    private String plural;

    PassEntityType(String name, String plural) {
        this.name = name;
        this.plural = plural;
    }
    
    /** 
     * Get the name.
     * 
     * @return The name
     */
    public String getName() {
        return this.name;
    }
    
    /** 
     * Get pluralized name.
     * 
     * @return Pluralized name
     */
    public String getPlural() {
        return this.plural;
    }
    
    /**
     * Match enum using name
     * @param name The name of the type
     * @return matching PassEntityType or null if no matches
     */
    public static PassEntityType getTypeByName(String name) {
        if (name!=null && name.length()>0) {
            for(PassEntityType type : PassEntityType.values()){
                if(name.equals(type.getName())) return type;
            }
        }
        //no match found or name empty, throw argument exception
        throw new IllegalArgumentException(String.format("Entity type \"%s\" is not recognized", name));
    }
    
    
}
