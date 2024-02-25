/*
 * Copyright 2012-2020 the original author or authors.
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

package org.springframework.boot.actuate.autoconfigure.jdbc;

import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * External configuration properties for {@link DataSourceHealthIndicator}.
 *
 * @author Julio Gomez
 * @since 2.4.0
 */
@ConfigurationProperties(prefix = "management.health.db")
public class DataSourceHealthIndicatorProperties {

	/**
	 * Whether to ignore AbstractRoutingDataSources when creating database health
	 * indicators.
	 */
	private boolean ignoreRoutingDataSources = false;

	/**
     * Returns a boolean value indicating whether to ignore routing data sources.
     * 
     * @return {@code true} if routing data sources should be ignored, {@code false} otherwise
     */
    public boolean isIgnoreRoutingDataSources() {
		return this.ignoreRoutingDataSources;
	}

	/**
     * Sets the flag to ignore routing data sources.
     * 
     * @param ignoreRoutingDataSources the flag indicating whether to ignore routing data sources
     */
    public void setIgnoreRoutingDataSources(boolean ignoreRoutingDataSources) {
		this.ignoreRoutingDataSources = ignoreRoutingDataSources;
	}

}
