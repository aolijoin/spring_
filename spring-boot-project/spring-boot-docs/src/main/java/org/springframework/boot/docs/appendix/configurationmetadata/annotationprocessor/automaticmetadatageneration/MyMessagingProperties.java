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

package org.springframework.boot.docs.appendix.configurationmetadata.annotationprocessor.automaticmetadatageneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MyMessagingProperties class.
 */
@ConfigurationProperties(prefix = "my.messaging")
public class MyMessagingProperties {

	private List<String> addresses = new ArrayList<>(Arrays.asList("a", "b"));

	private ContainerType containerType = ContainerType.SIMPLE;

	// @fold:on // getters/setters ...
	public List<String> getAddresses() {
		return this.addresses;
	}

	/**
     * Sets the list of addresses for messaging.
     * 
     * @param addresses the list of addresses to be set
     */
    public void setAddresses(List<String> addresses) {
		this.addresses = addresses;
	}

	/**
     * Returns the container type of the messaging properties.
     * 
     * @return the container type of the messaging properties
     */
    public ContainerType getContainerType() {
		return this.containerType;
	}

	/**
     * Sets the container type for the messaging properties.
     * 
     * @param containerType the container type to be set
     */
    public void setContainerType(ContainerType containerType) {
		this.containerType = containerType;
	}
	// @fold:off

	public enum ContainerType {

		SIMPLE, DIRECT

	}

}
