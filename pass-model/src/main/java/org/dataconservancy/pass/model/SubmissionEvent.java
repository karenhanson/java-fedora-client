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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.dataconservancy.pass.model.support.ZuluDateTimeDeserializer;
import org.dataconservancy.pass.model.support.ZuluDateTimeSerializer;
import org.joda.time.DateTime;

/**
 * The SubmissionEvent model captures significant events that are performed by an agent and occur against a Submission. 
 * @author Karen Hanson
 */

public class SubmissionEvent extends PassEntity {

    /** 
     * The type of event
     */
    private EventType eventType;

    /** 
     * Date the event was performed by the User
     */
    @JsonSerialize(using = ZuluDateTimeSerializer.class)
    @JsonDeserialize(using = ZuluDateTimeDeserializer.class)
    private DateTime performedDate;
    
    /**
     * URI of the User responsible for performing the event
     */
    private URI performedBy;

    /** 
     * Role of the person performing the event
     */
    private PerformerRole performerRole;
    
    /** 
     * List of repositories that the submission will be deposited to 
     */
    private URI submission;

    /**
     * A comment relevant to the SubmissionEvent. For example, when a `changes-requested` event occurs, 
     * this might be added by the User through the UI to communicate what changes should be made
     */
    private String comment;

    /** 
     * A resource relevant to the SubmissionEvent. For example, when a `changes-requested` event occurs, 
     * this may contain an Ember application URL to the affected Submission.
     */
    private URI link;
    
    /** 
     * The types of events that might be recorded as SubmissionEvents
     */
    public enum EventType {
        /**
         * A Submission was prepared by a preparer on behalf of a person who does not yet have a User 
         * record in PASS. The preparer is requesting that the submitter join PASS and then approve and 
         * submit it or provide feedback.
         */
        @JsonProperty("approval-requested-newuser")
        APPROVAL_REQUESTED_NEWUSER("approval-requested-newuser"),
        
        /**
         * A Submission was prepared by a preparer who is now requesting that the submitter approve and 
         * submit it or provide feedback
         */
        @JsonProperty("approval-requested")
        APPROVAL_REQUESTED("approval-requested"),
        
        /**
         * A Submission was prepared by a preparer, but on review by the submitter, a change was requested. 
         * The Submission has been handed back to the preparer for editing.
         */
        @JsonProperty("changes-requested")
        CHANGES_REQUESTED("changes-requested"),

        /**
         *  A Submission was prepared and then cancelled by the submitter or preparer without being submitted. 
         *  No further edits can be made to the Submission.
         */
        @JsonProperty("cancelled")
        CANCELLED("cancelled"),
        
        /**
         * The submit button has been pressed through the UI.
         */
        @JsonProperty("submitted")
        SUBMITTED("submitted");

        private static final Map<String, EventType> map = new HashMap<>(values().length, 1);  
        static {
          for (EventType s : values()) map.put(s.value, s);
        }
        
        private String value;
        
        private EventType(String value){
            this.value = value;
        }
        
        public static EventType of(String eventType) {
            EventType result = map.get(eventType);
            if (result == null) {
              throw new IllegalArgumentException("Invalid Event Type: " + eventType);
            }
            return result;
          }

        @Override
        public String toString() {
            return this.value;
        }
        
    }


    /** 
     * Roles of agents who might perform a SubmissionEvent
     */
    public enum PerformerRole {
        @JsonProperty("preparer")
        PREPARER("preparer"),
        @JsonProperty("submitter")
        SUBMITTER("submitter");
        
        private String value;
        
        private PerformerRole(String value){
            this.value = value;
        }
        
        @Override
        public String toString() {
            return this.value;
        }
    }
    
    
    /**
     * @return the eventType
     */
    public EventType getEventType() {
        return eventType;
    }

    
    /**
     * @param eventType the eventType to set
     */
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    
    /**
     * @return the performedDate
     */
    public DateTime getPerformedDate() {
        return performedDate;
    }

    
    /**
     * @param performedDate the performedDate to set
     */
    public void setPerformedDate(DateTime performedDate) {
        this.performedDate = performedDate;
    }

    
    /**
     * @return the performedBy
     */
    public URI getPerformedBy() {
        return performedBy;
    }

    
    /**
     * @param performedBy the performedBy to set
     */
    public void setPerformedBy(URI performedBy) {
        this.performedBy = performedBy;
    }

    
    /**
     * @return the performerRole
     */
    public PerformerRole getPerformerRole() {
        return performerRole;
    }

    
    /**
     * @param performerRole the performerRole to set
     */
    public void setPerformerRole(PerformerRole performerRole) {
        this.performerRole = performerRole;
    }

    
    /**
     * @return the submission
     */
    public URI getSubmission() {
        return submission;
    }

    
    /**
     * @param submission the submission to set
     */
    public void setSubmission(URI submission) {
        this.submission = submission;
    }

    
    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    
    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    
    /**
     * @return the link
     */
    public URI getLink() {
        return link;
    }

    
    /**
     * @param link the link to set
     */
    public void setLink(URI link) {
        this.link = link;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SubmissionEvent that = (SubmissionEvent) o;

        if (eventType != null ? !eventType.equals(that.eventType) : that.eventType != null) return false;
        if (performedDate != null ? !performedDate.equals(that.performedDate) : that.performedDate != null) return false;
        if (performedBy != null ? !performedBy.equals(that.performedBy) : that.performedBy != null) return false;
        if (performerRole != null ? !performerRole.equals(that.performerRole) : that.performerRole != null) return false;
        if (submission != null ? !submission.equals(that.submission) : that.submission != null) return false;    
        if (comment != null ? !comment.equals(that.comment) : that.comment != null) return false;            
        if (link != null ? !link.equals(that.link) : that.link != null) return false;
        return true;
    }

    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (eventType != null ? eventType.hashCode() : 0);
        result = 31 * result + (performedDate != null ? performedDate.hashCode() : 0);
        result = 31 * result + (performedBy != null ? performedBy.hashCode() : 0);
        result = 31 * result + (performerRole != null ? performerRole.hashCode() : 0);
        result = 31 * result + (submission != null ? submission.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        return result;
    }

}
