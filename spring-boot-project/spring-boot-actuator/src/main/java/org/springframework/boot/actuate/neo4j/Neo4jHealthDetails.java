/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.actuate.neo4j;

import org.neo4j.driver.Record;
import org.neo4j.driver.summary.ResultSummary;

/**
 * Health details for a Neo4j server.
 *
 * @author Andy Wilkinson
 */
class Neo4jHealthDetails {

	private final String version;

	private final String edition;

	private final ResultSummary summary;

	/**
     * Constructs a new Neo4jHealthDetails object with the given record and summary.
     * 
     * @param record the record containing health details
     * @param summary the summary of the health details
     */
    Neo4jHealthDetails(Record record, ResultSummary summary) {
		this.version = record.get("version").asString();
		this.edition = record.get("edition").asString();
		this.summary = summary;
	}

	/**
     * Returns the version of the Neo4jHealthDetails.
     *
     * @return the version of the Neo4jHealthDetails
     */
    String getVersion() {
		return this.version;
	}

	/**
     * Returns the edition of the Neo4j database.
     * 
     * @return the edition of the Neo4j database
     */
    String getEdition() {
		return this.edition;
	}

	/**
     * Returns the summary of the result.
     *
     * @return the summary of the result
     */
    ResultSummary getSummary() {
		return this.summary;
	}

}
