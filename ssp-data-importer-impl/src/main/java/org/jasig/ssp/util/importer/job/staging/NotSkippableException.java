/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.ssp.util.importer.job.staging;
/**
 * Because we execute the upsert as part of the 'staging' step and want an error on upsert to 
 * be fatal.  We create a special exception that will be thrown on upsert what is configured in 
 * the step as excluded.  This will allow skip tolerance on staging where we expect some errors
 * but not want to fail fatally while still failing fast on upsert.
 */
public class NotSkippableException extends RuntimeException{

    public NotSkippableException() {
        super();
    }

    public NotSkippableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSkippableException(String message) {
        super(message);
    }

    public NotSkippableException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = -7146535941635015199L;

}
