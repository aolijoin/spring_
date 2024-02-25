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

package org.springframework.boot.docker.compose.lifecycle;

import java.time.Duration;
import java.util.function.BiConsumer;

import org.springframework.boot.docker.compose.core.DockerCompose;

/**
 * Command used to stop Docker Compose.
 *
 * @author Moritz Halbritter
 * @author Andy Wilkinson
 * @author Phillip Webb
 * @since 3.1.0
 */
public enum StopCommand {

	/**
	 * Stop using {@code docker compose down}.
	 */
	DOWN(DockerCompose::down),

	/**
	 * Stop using {@code docker compose stop}.
	 */
	STOP(DockerCompose::stop);

	private final BiConsumer<DockerCompose, Duration> action;

	/**
     * Sets the action to be performed when the stop command is executed.
     * 
     * @param action the action to be performed, which takes a DockerCompose object and a Duration object as parameters
     */
    StopCommand(BiConsumer<DockerCompose, Duration> action) {
		this.action = action;
	}

	/**
     * Applies the specified action to the given DockerCompose instance with the specified timeout.
     * 
     * @param dockerCompose the DockerCompose instance to apply the action to
     * @param timeout the duration to wait for the action to complete
     */
    void applyTo(DockerCompose dockerCompose, Duration timeout) {
		this.action.accept(dockerCompose, timeout);
	}

}
