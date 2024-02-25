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

package org.springframework.boot.autoconfigure.r2dbc;

import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsInitializer.ConnectionFactoryBeanCreationException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.r2dbc.EmbeddedDatabaseConnection;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * An {@link AbstractFailureAnalyzer} for failures caused by a
 * {@link ConnectionFactoryBeanCreationException}.
 *
 * @author Mark Paluch
 */
class ConnectionFactoryBeanCreationFailureAnalyzer
		extends AbstractFailureAnalyzer<ConnectionFactoryBeanCreationException> {

	private final Environment environment;

	/**
     * Constructs a new ConnectionFactoryBeanCreationFailureAnalyzer with the specified environment.
     * 
     * @param environment the environment to be used by the ConnectionFactoryBeanCreationFailureAnalyzer
     */
    ConnectionFactoryBeanCreationFailureAnalyzer(Environment environment) {
		this.environment = environment;
	}

	/**
     * Analyzes the failure caused by a ConnectionFactoryBeanCreationException and returns a FailureAnalysis object.
     * 
     * @param rootFailure the root cause of the failure
     * @param cause the ConnectionFactoryBeanCreationException that caused the failure
     * @return a FailureAnalysis object containing information about the failure
     */
    @Override
	protected FailureAnalysis analyze(Throwable rootFailure, ConnectionFactoryBeanCreationException cause) {
		return getFailureAnalysis(cause);
	}

	/**
     * Generates a FailureAnalysis object based on the given ConnectionFactoryBeanCreationException.
     * 
     * @param cause the ConnectionFactoryBeanCreationException that occurred
     * @return a FailureAnalysis object containing the description, action, and cause of the failure
     */
    private FailureAnalysis getFailureAnalysis(ConnectionFactoryBeanCreationException cause) {
		String description = getDescription(cause);
		String action = getAction(cause);
		return new FailureAnalysis(description, action, cause);
	}

	/**
     * Returns a description of the ConnectionFactory bean creation failure.
     * 
     * @param cause the exception that caused the failure
     * @return a string description of the failure
     */
    private String getDescription(ConnectionFactoryBeanCreationException cause) {
		StringBuilder description = new StringBuilder();
		description.append("Failed to configure a ConnectionFactory: ");
		if (!StringUtils.hasText(cause.getUrl())) {
			description.append("'url' attribute is not specified and ");
		}
		description.append(String.format("no embedded database could be configured.%n"));
		description.append(String.format("%nReason: %s%n", cause.getMessage()));
		return description.toString();
	}

	/**
     * Returns the recommended action to be taken based on the given ConnectionFactoryBeanCreationException.
     * 
     * @param cause the ConnectionFactoryBeanCreationException that occurred
     * @return the recommended action as a String
     */
    private String getAction(ConnectionFactoryBeanCreationException cause) {
		StringBuilder action = new StringBuilder();
		action.append(String.format("Consider the following:%n"));
		if (EmbeddedDatabaseConnection.NONE == cause.getEmbeddedDatabaseConnection()) {
			action.append(String.format("\tIf you want an embedded database (H2), please put it on the classpath.%n"));
		}
		else {
			action.append(String.format("\tReview the configuration of %s%n.", cause.getEmbeddedDatabaseConnection()));
		}
		action
			.append("\tIf you have database settings to be loaded from a particular "
					+ "profile you may need to activate it")
			.append(getActiveProfiles());
		return action.toString();
	}

	/**
     * Returns a string representation of the active profiles.
     * 
     * @return a string representation of the active profiles
     */
    private String getActiveProfiles() {
		StringBuilder message = new StringBuilder();
		String[] profiles = this.environment.getActiveProfiles();
		if (ObjectUtils.isEmpty(profiles)) {
			message.append(" (no profiles are currently active).");
		}
		else {
			message.append(" (the profiles ");
			message.append(StringUtils.arrayToCommaDelimitedString(profiles));
			message.append(" are currently active).");
		}
		return message.toString();
	}

}
