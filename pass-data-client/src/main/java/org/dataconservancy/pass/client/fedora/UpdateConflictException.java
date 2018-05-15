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
package org.dataconservancy.pass.client.fedora;


/**
 * This exception is throw when attempting to update a Resource that has changed since the last 
 * readResource from the database. When read from Fedora, each PassEntity contains an ETag 
 * field that is verified during each update to avoid overwriting changes.
 * 
 * @author Karen Hanson
 */
public class UpdateConflictException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public UpdateConflictException() {
        super();
    }
    public UpdateConflictException(String s) {
        super(s);
    }
    public UpdateConflictException(String s, Throwable throwable) {
        super(s, throwable);
    }
    public UpdateConflictException(Throwable throwable) {
        super(throwable);
    }
}
