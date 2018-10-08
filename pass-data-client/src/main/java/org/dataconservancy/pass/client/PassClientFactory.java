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

package org.dataconservancy.pass.client;

/**
 * PASS client factory.
 *
 * @author Karen Hanson
 */
public class PassClientFactory {

    /**
     * Provide an instance a PassClient.
     * <p>
     * Defaults to overwriteOnUpdate = false.
     * </p>
     * 
     * @return PASS client
     */
    public static PassClient getPassClient() {
        return new PassClientDefault();
    }

    /**
     * Provide an instance of a PassClient, using a prpvided update policy.
     * <p>
     * Includes option to overwrite during an update. The current default is to only update fields that have changed
     * thus allowing slightly different versions of the model to function together without overwriting each other.
     * This new option supports the scenario where you actually do want to completely overwrite a record with model
     * changes and all - to do this, you would pass in "overwriteOnUpdate=true".
     * </p>
     * 
     * @param overwriteOnUpdate - true if you would like updates to completely overwrite the record, false if you
     *        would like only fields that have changed to be updated
     * @return PASS client
     */
    public static PassClient getPassClient(boolean overwriteOnUpdate) {
        return new PassClientDefault().overWriteOnUpdate(overwriteOnUpdate);
    }

}
