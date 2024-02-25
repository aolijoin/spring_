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

package org.springframework.boot.actuate.autoconfigure.tracing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.ClassUtils;

/**
 * {@link EnvironmentPostProcessor} to add a {@link PropertySource} to support log
 * correlation IDs when Micrometer Tracing is present. Adds support for the
 * {@value LoggingSystem#EXPECT_CORRELATION_ID_PROPERTY} property by delegating to
 * {@code management.tracing.enabled}.
 *
 * @author Jonatan Ivanov
 * @author Phillip Webb
 */
class LogCorrelationEnvironmentPostProcessor implements EnvironmentPostProcessor {

	/**
     * Post-processes the environment by adding a LogCorrelationPropertySource if the Tracer class from Micrometer is present in the application's class loader.
     * 
     * @param environment the configurable environment
     * @param application the Spring application
     */
    @Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		if (ClassUtils.isPresent("io.micrometer.tracing.Tracer", application.getClassLoader())) {
			environment.getPropertySources().addLast(new LogCorrelationPropertySource(this, environment));
		}
	}

	/**
	 * Log correlation {@link PropertySource}.
	 */
	private static class LogCorrelationPropertySource extends EnumerablePropertySource<Object> {

		private static final String NAME = "logCorrelation";

		private final Environment environment;

		/**
         * Constructs a new LogCorrelationPropertySource with the specified source and environment.
         * 
         * @param source the source object for the property source
         * @param environment the environment object for the property source
         */
        LogCorrelationPropertySource(Object source, Environment environment) {
			super(NAME, source);
			this.environment = environment;
		}

		/**
         * Returns an array of property names.
         *
         * @return the array of property names
         */
        @Override
		public String[] getPropertyNames() {
			return new String[] { LoggingSystem.EXPECT_CORRELATION_ID_PROPERTY };
		}

		/**
         * Retrieves the value of a property with the given name.
         * 
         * @param name the name of the property to retrieve
         * @return the value of the property, or null if the property does not exist
         */
        @Override
		public Object getProperty(String name) {
			if (name.equals(LoggingSystem.EXPECT_CORRELATION_ID_PROPERTY)) {
				return this.environment.getProperty("management.tracing.enabled", Boolean.class, Boolean.TRUE);
			}
			return null;
		}

	}

}
