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
 * The Workflow tracks the current status of a Submission process in the application
 * @author Karen Hanson
 */

public class Workflow extends PassEntity {

    /** 
     * String type name, specifically used to set "@type" in JSON serialization
     */
    @JsonProperty("@type")
    private String type = PassEntityType.WORKFLOW.getName();
    
    /** 
     * Name of workflow 
     */
    private String name;
    
    /** 
     * Current step in workflow 
     */
    private String step;
    
    /** 
     * Comma-separated list of steps required in workflow 
     */
    private String steps;

    
    @Override
    public String getType() {
        return type;
    }
    
        
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    
    /**
     * @return the step
     */
    public String getStep() {
        return step;
    }

    
    /**
     * @param step the step to set
     */
    public void setStep(String step) {
        this.step = step;
    }

    
    /**
     * @return the steps
     */
    public String getSteps() {
        return steps;
    }

    
    /**
     * @param steps the steps to set
     */
    public void setSteps(String steps) {
        this.steps = steps;
    }

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Workflow that = (Workflow) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (step != null ? !step.equals(that.step) : that.step != null) return false;
        if (steps != null ? !steps.equals(that.steps) : that.steps != null) return false;
        return true;
    }

    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (step != null ? step.hashCode() : 0);
        result = 31 * result + (steps != null ? steps.hashCode() : 0);
        return result;
    }        
}
