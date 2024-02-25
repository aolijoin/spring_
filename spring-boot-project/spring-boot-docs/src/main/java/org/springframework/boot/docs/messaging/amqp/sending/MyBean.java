/*
 * Copyright 2012-2021 the original author or authors.
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

package org.springframework.boot.docs.messaging.amqp.sending;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

/**
 * MyBean class.
 */
@Component
public class MyBean {

	private final AmqpAdmin amqpAdmin;

	private final AmqpTemplate amqpTemplate;

	/**
     * Constructs a new instance of MyBean with the specified AmqpAdmin and AmqpTemplate.
     * 
     * @param amqpAdmin the AmqpAdmin to be used by this MyBean
     * @param amqpTemplate the AmqpTemplate to be used by this MyBean
     */
    public MyBean(AmqpAdmin amqpAdmin, AmqpTemplate amqpTemplate) {
		this.amqpAdmin = amqpAdmin;
		this.amqpTemplate = amqpTemplate;
	}

	// @fold:on // ...
	public void someMethod() {
		this.amqpAdmin.getQueueInfo("someQueue");
	}

	/**
     * Sends a message "hello" to the AMQP server.
     */
    public void someOtherMethod() {
		this.amqpTemplate.convertAndSend("hello");
	}
	// @fold:off

}
