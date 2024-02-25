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

package org.springframework.boot.docs.actuator.micrometertracing.creatingspans;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import org.springframework.stereotype.Component;

/**
 * CustomObservation class.
 */
@Component
class CustomObservation {

	private final ObservationRegistry observationRegistry;

	/**
     * Constructs a new CustomObservation object with the specified ObservationRegistry.
     * 
     * @param observationRegistry the ObservationRegistry to be associated with this CustomObservation
     */
    CustomObservation(ObservationRegistry observationRegistry) {
		this.observationRegistry = observationRegistry;
	}

	/**
     * Performs some operation and records an observation.
     * 
     * @throws IllegalStateException if the observation registry is not set
     */
    void someOperation() {
		Observation observation = Observation.createNotStarted("some-operation", this.observationRegistry);
		observation.lowCardinalityKeyValue("some-tag", "some-value");
		observation.observe(() -> {
			// Business logic ...
		});
	}

}
