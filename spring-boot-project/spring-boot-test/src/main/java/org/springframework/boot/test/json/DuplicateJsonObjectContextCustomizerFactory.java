/*
 * Copyright 2012-2024 the original author or authors.
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

package org.springframework.boot.test.json;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.MergedContextConfiguration;

/**
 * A {@link ContextCustomizerFactory} that produces a {@link ContextCustomizer} that warns
 * the user when multiple occurrences of {@code JSONObject} are found on the class path.
 *
 * @author Andy Wilkinson
 */
class DuplicateJsonObjectContextCustomizerFactory implements ContextCustomizerFactory {

	/**
     * {@inheritDoc}
     * 
     * Creates a {@link ContextCustomizer} for customizing the test context.
     * 
     * @param testClass the test class being executed
     * @param configAttributes the configuration attributes for the test context
     * @return a {@link ContextCustomizer} instance
     */
    @Override
	public ContextCustomizer createContextCustomizer(Class<?> testClass,
			List<ContextConfigurationAttributes> configAttributes) {
		return new DuplicateJsonObjectContextCustomizer();
	}

	/**
     * DuplicateJsonObjectContextCustomizer class.
     */
    private static final class DuplicateJsonObjectContextCustomizer implements ContextCustomizer {

		private final Log logger = LogFactory.getLog(DuplicateJsonObjectContextCustomizer.class);

		/**
         * Customizes the application context by checking for duplicate JSON objects.
         * 
         * @param context the configurable application context
         * @param mergedConfig the merged context configuration
         */
        @Override
		public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
			List<URL> jsonObjects = findJsonObjects();
			if (jsonObjects.size() > 1) {
				logDuplicateJsonObjectsWarning(jsonObjects);
			}
		}

		/**
         * Finds all the JSON objects in the classpath.
         * 
         * @return a list of URLs pointing to the JSON objects found
         */
        private List<URL> findJsonObjects() {
			try {
				Enumeration<URL> resources = getClass().getClassLoader().getResources("org/json/JSONObject.class");
				return Collections.list(resources);
			}
			catch (Exception ex) {
				// Continue
			}
			return Collections.emptyList();
		}

		/**
         * Logs a warning message for duplicate occurrences of org.json.JSONObject on the class path.
         * 
         * @param jsonObjects the list of URLs representing the duplicate JSON objects
         */
        private void logDuplicateJsonObjectsWarning(List<URL> jsonObjects) {
			StringBuilder message = new StringBuilder(
					String.format("%n%nFound multiple occurrences of org.json.JSONObject on the class path:%n%n"));
			for (URL jsonObject : jsonObjects) {
				message.append(String.format("\t%s%n", jsonObject));
			}
			message.append(
					String.format("%nYou may wish to exclude one of them to ensure predictable runtime behavior%n"));
			this.logger.warn(message);
		}

		/**
         * Compares this object with the specified object for equality.
         * 
         * @param obj the object to compare with
         * @return {@code true} if the specified object is of the same class as this object, {@code false} otherwise
         */
        @Override
		public boolean equals(Object obj) {
			return (obj != null) && (getClass() == obj.getClass());
		}

		/**
         * Returns a hash code value for the object. This method overrides the default implementation of the {@code hashCode()} method.
         * 
         * @return the hash code value for the object
         */
        @Override
		public int hashCode() {
			return getClass().hashCode();
		}

	}

}
