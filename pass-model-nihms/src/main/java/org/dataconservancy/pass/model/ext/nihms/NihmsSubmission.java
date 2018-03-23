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
package org.dataconservancy.pass.model.ext.nihms;

import org.dataconservancy.pass.model.Submission;

/**
 * Additional fields required for NIHMS 
 * @author Karen Hanson
 */
public class NihmsSubmission extends Submission {

    /** 
     * PubMed ID 
     */
    private String pmid;
    
    /**
     * @return the PMID
     */
    public String getPmid() {
        return pmid;
    }
    
    /**
     * @param pmid
     */
    public void setPmid(String pmid) {
        this.pmid = pmid;
    }       

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        NihmsSubmission that = (NihmsSubmission) o;
       
        return (pmid != null ? !pmid.equals(that.pmid) : that.pmid != null);
    }

    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (pmid != null ? pmid.hashCode() : 0);
        return result;
    }       
}
