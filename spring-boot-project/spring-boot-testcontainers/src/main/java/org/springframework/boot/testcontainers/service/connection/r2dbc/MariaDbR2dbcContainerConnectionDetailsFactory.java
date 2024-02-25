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

package org.springframework.boot.testcontainers.service.connection.r2dbc;

import io.r2dbc.spi.ConnectionFactoryOptions;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MariaDBR2DBCDatabaseContainer;

import org.springframework.boot.autoconfigure.r2dbc.R2dbcConnectionDetails;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionDetailsFactory;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionSource;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

/**
 * {@link ContainerConnectionDetailsFactory} to create {@link R2dbcConnectionDetails} from
 * a {@link ServiceConnection @ServiceConnection}-annotated {@link MariaDBContainer}.
 *
 * @author Moritz Halbritter
 * @author Andy Wilkinson
 * @author Phillip Webb
 */
class MariaDbR2dbcContainerConnectionDetailsFactory
		extends ContainerConnectionDetailsFactory<MariaDBContainer<?>, R2dbcConnectionDetails> {

	/**
     * Constructs a new MariaDbR2dbcContainerConnectionDetailsFactory.
     * 
     * @param connectionName the name of the connection
     * @param connectionFactoryOptions the options for the connection factory
     */
    MariaDbR2dbcContainerConnectionDetailsFactory() {
		super(ANY_CONNECTION_NAME, "io.r2dbc.spi.ConnectionFactoryOptions");
	}

	/**
     * Returns the R2dbcConnectionDetails for the given ContainerConnectionSource.
     *
     * @param source the ContainerConnectionSource for the MariaDB container
     * @return the R2dbcConnectionDetails for the MariaDB container
     */
    @Override
	public R2dbcConnectionDetails getContainerConnectionDetails(ContainerConnectionSource<MariaDBContainer<?>> source) {
		return new MariaDbR2dbcDatabaseContainerConnectionDetails(source);
	}

	/**
	 * {@link R2dbcConnectionDetails} backed by a {@link ContainerConnectionSource}.
	 */
	private static final class MariaDbR2dbcDatabaseContainerConnectionDetails
			extends ContainerConnectionDetails<MariaDBContainer<?>> implements R2dbcConnectionDetails {

		/**
         * Constructs a new MariaDbR2dbcDatabaseContainerConnectionDetails object with the specified ContainerConnectionSource.
         * 
         * @param source the ContainerConnectionSource used to create the connection details
         */
        private MariaDbR2dbcDatabaseContainerConnectionDetails(ContainerConnectionSource<MariaDBContainer<?>> source) {
			super(source);
		}

		/**
         * Returns the connection factory options for the MariaDB R2DBC database container.
         * 
         * @return the connection factory options
         */
        @Override
		public ConnectionFactoryOptions getConnectionFactoryOptions() {
			return MariaDBR2DBCDatabaseContainer.getOptions(getContainer());
		}

	}

}
