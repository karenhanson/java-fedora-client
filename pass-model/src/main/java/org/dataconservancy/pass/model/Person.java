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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Description of Person. A person be a PI or co-PI for a Grant, be an author for a Submission, or be associated with a User.
 * @author Karen Hanson
 */

public class Person extends PassEntity {

    /** 
     * String type name, specifically used to set "@type" in JSON serialization
     */
    @JsonProperty("@type")
    private String type = PassEntityType.PERSON.getName();
    
    /** 
     * First name(s) of person 
     */
    private String firstName;

    /** 
     * Middle name(s) of person 
     */
    private String middleName;

    /** 
     * Last name(s) of person 
     */
    private String lastName;
    
    /** 
     * Name for display. Separate names may not be available, but a person should always at least 
     * have a display name.
     */
    private String displayName; 
    
    /** 
     * Contact email for person 
     */
    private String email;
    
    /** 
     * ID assigned by person's institution 
     */
    private String institutionalId;
    
    /** 
     * ORCID ID for person 
     */
    private String orcidId;
    
    /** 
     * Affiliation string for person. Where Person is embedded in Submission or Grant, 
     * this is the affiliation relevant to that item 
     */
    private String affiliation; 

    
    @Override
    public String getType() {
        return type;
    }
    
    
    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    
    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    
    /**
     * @return the middleName
     */
    public String getMiddleName() {
        return middleName;
    }

    
    /**
     * @param middleName the middleName to set
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    
    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    
    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    
    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    
    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    
    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    
    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    
    /**
     * @return the institutionalId
     */
    public String getInstitutionalId() {
        return institutionalId;
    }

    
    /**
     * @param institutionalId the institutionalId to set
     */
    public void setInstitutionalId(String institutionalId) {
        this.institutionalId = institutionalId;
    }

    
    /**
     * @return the orcidId
     */
    public String getOrcidId() {
        return orcidId;
    }

    
    /**
     * @param orcidId the orcidId to set
     */
    public void setOrcidId(String orcidId) {
        this.orcidId = orcidId;
    }

    
    /**
     * @return the affiliation
     */
    public String getAffiliation() {
        return affiliation;
    }

    
    /**
     * @param affiliation the affiliation to set
     */
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Person that = (Person) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (middleName != null ? !middleName.equals(that.middleName) : that.middleName != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (institutionalId != null ? !institutionalId.equals(that.institutionalId) : that.institutionalId != null) return false;
        if (orcidId != null ? !orcidId.equals(that.orcidId) : that.orcidId != null) return false;
        if (affiliation != null ? !affiliation.equals(that.affiliation) : that.affiliation != null) return false;
        return true;
    }

    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (institutionalId != null ? institutionalId.hashCode() : 0);
        result = 31 * result + (orcidId != null ? orcidId.hashCode() : 0);
        result = 31 * result + (affiliation != null ? affiliation.hashCode() : 0);
        return result;
    }
       
}
