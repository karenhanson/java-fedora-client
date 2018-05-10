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
package org.dataconservancy.pass.client.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.dataconservancy.pass.model.PassEntity;
import org.dataconservancy.pass.model.PassEntityType;

/**
 * This is a cut down copy of the User model. It is used to verify that by using PATCH for updates
 * we do not lose data when using a cut down model
 * 
 * @author Karen Hanson
 */
public class TestUserModel extends PassEntity {

    /** 
     * This will pretend it is a "User" object for testing.
     */
    @JsonProperty("@type")
    private String type = PassEntityType.USER.getName();
    
    /** 
     * Unique login name used by user 
     */
    private String username;

    /** 
     * Name for display. Separate names may not be available, but a person should always at least 
     * have a display name.
     */
    private String displayName; 
    
    /** 
     * Contact email for User
     */
    private String email;
    
    
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


    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TestUserModel that = (TestUserModel) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        return true;
    }

    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
    

}
