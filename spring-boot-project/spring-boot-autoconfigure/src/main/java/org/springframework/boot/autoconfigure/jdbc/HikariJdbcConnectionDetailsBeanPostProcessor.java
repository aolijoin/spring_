/*
 * Copyright 2012-2023 the original author or authors.
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

package org.springframework.boot.autoconfigure.jdbc;

import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.ObjectProvider;

/**
 * Post-processes beans of type {@link HikariDataSource} and name 'dataSource' to apply
 * the values from {@link JdbcConnectionDetails}.
 *
 * @author Moritz Halbritter
 * @author Andy Wilkinson
 * @author Phillip Webb
 */
class HikariJdbcConnectionDetailsBeanPostProcessor extends JdbcConnectionDetailsBeanPostProcessor<HikariDataSource> {

	/**
     * Constructs a new HikariJdbcConnectionDetailsBeanPostProcessor with the specified connectionDetailsProvider.
     * 
     * @param connectionDetailsProvider the provider for JdbcConnectionDetails objects
     */
    HikariJdbcConnectionDetailsBeanPostProcessor(ObjectProvider<JdbcConnectionDetails> connectionDetailsProvider) {
		super(HikariDataSource.class, connectionDetailsProvider);
	}

	/**
     * Sets the JDBC URL, username, password, and driver class name for the given HikariDataSource object.
     * 
     * @param dataSource the HikariDataSource object to be processed
     * @param connectionDetails the JdbcConnectionDetails object containing the connection details
     * @return the processed HikariDataSource object
     */
    @Override
	protected Object processDataSource(HikariDataSource dataSource, JdbcConnectionDetails connectionDetails) {
		dataSource.setJdbcUrl(connectionDetails.getJdbcUrl());
		dataSource.setUsername(connectionDetails.getUsername());
		dataSource.setPassword(connectionDetails.getPassword());
		dataSource.setDriverClassName(connectionDetails.getDriverClassName());
		return dataSource;
	}

}
