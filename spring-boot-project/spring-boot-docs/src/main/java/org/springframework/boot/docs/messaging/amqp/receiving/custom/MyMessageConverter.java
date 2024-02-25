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

package org.springframework.boot.docs.messaging.amqp.receiving.custom;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * MyMessageConverter class.
 */
class MyMessageConverter implements MessageConverter {

	/**
     * Converts the given object to a Message using the provided MessageProperties.
     * 
     * @param object the object to be converted
     * @param messageProperties the properties to be used for the converted Message
     * @return the converted Message, or null if the conversion fails
     * @throws MessageConversionException if an error occurs during the conversion process
     */
    @Override
	public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
		return null;
	}

	/**
     * Converts a Message object to an Object.
     * 
     * @param message the Message object to be converted
     * @return the converted Object, or null if conversion fails
     * @throws MessageConversionException if an error occurs during conversion
     */
    @Override
	public Object fromMessage(Message message) throws MessageConversionException {
		return null;
	}

}
