/*
 * Copyright 2023-2023 the original author or authors.
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

package org.springframework.boot.docs.messaging.pulsar.receivingreactive;

import reactor.core.publisher.Mono;

import org.springframework.pulsar.reactive.config.annotation.ReactivePulsarListener;
import org.springframework.stereotype.Component;

/**
 * MyBean class.
 */
@Component
public class MyBean {

	/**
     * Process the message received from the "someTopic" topic.
     *
     * @param content the content of the message
     * @return a Mono representing the completion of the processing
     */
    @ReactivePulsarListener(topics = "someTopic")
	public Mono<Void> processMessage(String content) {
		// ...
		return Mono.empty();
	}

}
