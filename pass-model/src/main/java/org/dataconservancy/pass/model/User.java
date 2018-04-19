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

import java.net.URI;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User model for users logging into PASS
 * @author Karen Hanson
 */

public class User extends PassEntity {

    /** 
     * String type name, specifically used to set "@type" in JSON serialization
     */
    @JsonProperty("@type")
    private String type = PassEntityType.USER.getName();
    
    /** 
     * Unique login name used by user 
     */
    private String username;

    /** 
     * User role 
     */
    private Role role;

    /** 
     * Person associated with the account 
     */
    private URI person;

    /** 
     * list of possible user Roles 
     */
    public enum Role {
        @JsonProperty("admin")
        ADMIN("admin"),
        @JsonProperty("pi")
        PI("pi");

        private static final Map<String, Role> map = new HashMap<>(values().length, 1);  
        static {
          for (Role r : values()) map.put(r.value, r);
        }
        
        private String value;
        
        private Role(String value){
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public static Role of(String role) {
            Role result = map.get(role);
            if (result == null) {
              throw new IllegalArgumentException("Invalid Deposit Status: " + role);
            }
            return result;
          }
    }

    
    @Override
    public String getType() {
        return type;
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
     * @return the role
     */
    public Role getRole() {
        return role;
    }

    
    /**
     * @param role the role to set
     */
    public void setRole(Role role) {
        this.role = role;
    }

    
    /**
     * @return the person
     */
    public URI getPerson() {
        return person;
    }

    
    /**
     * @param person the person to set
     */
    public void setPerson(URI person) {
        this.person = person;
    }

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        User that = (User) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (role != null ? !role.equals(that.role) : that.role != null) return false;
        if (person != null ? !person.equals(that.person) : that.person != null) return false;
        return true;
    }

    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (person != null ? person.hashCode() : 0);
        return result;
    }
       
    
}
