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

package org.springframework.boot.docs.messaging.pulsar.sendingreactive;

import org.springframework.pulsar.reactive.core.ReactivePulsarTemplate;
import org.springframework.stereotype.Component;

/**
 * MyBean class.
 */
@Component
public class MyBean {

	private final ReactivePulsarTemplate<String> pulsarTemplate;

	/**
     * Constructs a new instance of MyBean with the specified ReactivePulsarTemplate.
     *
     * @param pulsarTemplate the ReactivePulsarTemplate to be used by this MyBean instance
     */
    public MyBean(ReactivePulsarTemplate<String> pulsarTemplate) {
		this.pulsarTemplate = pulsarTemplate;
	}

	/**
     * Sends a message to the "someTopic" topic using the PulsarTemplate and subscribes to the result.
     * 
     * @return void
     */
    public void someMethod() {
		this.pulsarTemplate.send("someTopic", "Hello").subscribe();
	}

}
