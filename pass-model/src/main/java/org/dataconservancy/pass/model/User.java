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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User model for users logging into PASS
 * @author Karen Hanson
 */

public class User extends PassEntity {
    
    /** 
     * Unique login name used by user 
     */
    private String username;

    /** 
     * First name(s) of User 
     */
    private String firstName;

    /** 
     * Middle name(s) of User 
     */
    private String middleName;

    /** 
     * Last name(s) of User 
     */
    private String lastName;
    
    /** 
     * Name for display. Separate names may not be available, but a person should always at least 
     * have a display name.
     */
    private String displayName; 
    
    /** 
     * Contact email for User
     */
    private String email;
    
    /** 
     * Affiliation string for person. Where Person is embedded in Submission or Grant, 
     * this is the affiliation relevant to that item 
     */
    private String affiliation; 
    
    /** 
     * A list of ids associated with the user by various system that PASS interacts with. 
     * The value of each entry would be in the form of : {@code domain:type:value}. 
     * For example, @{code ["johnshopkins.edu:hopkinsid:DRA2D", "johnshopkins.edu:employeeid:12345", 
     * "johnshopkins.edu:jhed:bostaur1"]}
     */
    private List<String> locatorIds = new ArrayList<String>();
    
    /** 
     * ORCID ID for User 
     */
    private String orcidId;
    
    /** 
     * User's system roles in PASS
     */
    private List<Role> roles = new ArrayList<Role>();

    /** 
     * list of possible user Roles 
     */
    public enum Role {
        @JsonProperty("admin")
        ADMIN("admin"),
        @JsonProperty("submitter")
        SUBMITTER("submitter");

        private static final Map<String, Role> map = new HashMap<>(values().length, 1);  
        static {
          for (Role r : values()) map.put(r.value, r);
        }
        
        private String value;
        
        private Role(String value){
            this.value = value;
        }
        
        public static Role of(String role) {
            Role result = map.get(role);
            if (result == null) {
              throw new IllegalArgumentException("Invalid Role: " + role);
            }
            return result;
          }
        
        @Override
        public String toString() {
            return this.value;
        }
    }
    
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    
    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
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

    
    /**
     * @return the locatorIds
     */
    public List<String> getLocatorIds() {
        return locatorIds;
    }

    
    /**
     * @param localKey the localKey to set
     */
    public void setLocatorIds(List<String> locatorIds) {
        this.locatorIds = locatorIds;
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
     * @return the list of roles
     */
    public List<Role> getRoles() {
        return roles;
    }

    
    /**
     * @param role the roles list to set
     */
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        User that = (User) o;

        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (middleName != null ? !middleName.equals(that.middleName) : that.middleName != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (affiliation != null ? !affiliation.equals(that.affiliation) : that.affiliation != null) return false;
        if (locatorIds != null ? !locatorIds.equals(that.locatorIds) : that.locatorIds != null) return false;
        if (orcidId != null ? !orcidId.equals(that.orcidId) : that.orcidId != null) return false;
        if (roles != null ? !roles.equals(that.roles) : that.roles != null) return false;
        return true;
    }

    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (affiliation != null ? affiliation.hashCode() : 0);
        result = 31 * result + (locatorIds != null ? locatorIds.hashCode() : 0);
        result = 31 * result + (orcidId != null ? orcidId.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        return result;
    }
       
    
}
