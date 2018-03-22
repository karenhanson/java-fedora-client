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

import java.io.InputStream;

import org.dataconservancy.pass.model.PassEntity;

/**
 *
 * @author Karen Hanson
 */
public interface PassJsonAdapter {
    
    /**
     * Pass in a PASS model object e.g. Deposit, use values to produce JSON
     * You can opt to include the context in the JSON if this is to be used with
     * a Linked Data Platform
     * @param modelObject 
     * @param includeContext true if the JSON-LD context should be included in the JSON
     * @return
     */
    public byte[] toJson(PassEntity modelObject, boolean includeContext);
    
    /**
     * Pass in the JSON data as a byte array and the model class to match it to e.g. Deposit.class, returns populated model
     * @param jsonData
     * @param valueType
     * @return
     */
    public <T extends PassEntity> T toModel(byte[] jsonData, Class<T> valueType);
    
    
    /**
     * Pass in the json data as an InputStream and the model class to match it to e.g. Deposit.class, returns populated model
     * @param jsonData
     * @param valueType
     * @return
     */
    public <T extends PassEntity> T toModel(InputStream jsonData, Class<T> valueType);

}
