/*
 * Copyright 2012-2022 the original author or authors.
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

package org.springframework.boot.actuate.autoconfigure.metrics.export.jmx;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {@link ConfigurationProperties @ConfigurationProperties} for configuring JMX metrics
 * export.
 *
 * @author Jon Schneider
 * @author Stephane Nicoll
 * @since 2.0.0
 */
@ConfigurationProperties(prefix = "management.jmx.metrics.export")
public class JmxProperties {

	/**
	 * Whether exporting of metrics to this backend is enabled.
	 */
	private boolean enabled = true;

	/**
	 * Metrics JMX domain name.
	 */
	private String domain = "metrics";

	/**
	 * Step size (i.e. reporting frequency) to use.
	 */
	private Duration step = Duration.ofMinutes(1);

	/**
     * Returns the domain of the JmxProperties.
     *
     * @return the domain of the JmxProperties
     */
    public String getDomain() {
		return this.domain;
	}

	/**
     * Sets the domain for the JmxProperties.
     * 
     * @param domain the domain to set
     */
    public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
     * Returns the step duration.
     *
     * @return the step duration
     */
    public Duration getStep() {
		return this.step;
	}

	/**
     * Sets the step duration for the JmxProperties.
     * 
     * @param step the step duration to be set
     */
    public void setStep(Duration step) {
		this.step = step;
	}

	/**
     * Returns the current status of the enabled flag.
     *
     * @return true if the enabled flag is set to true, false otherwise.
     */
    public boolean isEnabled() {
		return this.enabled;
	}

	/**
     * Sets the enabled status of the JmxProperties.
     * 
     * @param enabled the enabled status to be set
     */
    public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
