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

package org.springframework.boot.docs.features.externalconfig.typesafeconfigurationproperties.conversion.datasizes.javabeanbinding;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

/**
 * MyProperties class.
 */
@ConfigurationProperties("my")
public class MyProperties {

	@DataSizeUnit(DataUnit.MEGABYTES)
	private DataSize bufferSize = DataSize.ofMegabytes(2);

	private DataSize sizeThreshold = DataSize.ofBytes(512);

	// @fold:on // getters/setters...
	public DataSize getBufferSize() {
		return this.bufferSize;
	}

	/**
     * Sets the buffer size for the MyProperties class.
     * 
     * @param bufferSize the buffer size to be set
     */
    public void setBufferSize(DataSize bufferSize) {
		this.bufferSize = bufferSize;
	}

	/**
     * Returns the size threshold for the data.
     *
     * @return the size threshold for the data
     */
    public DataSize getSizeThreshold() {
		return this.sizeThreshold;
	}

	/**
     * Sets the size threshold for the MyProperties object.
     * 
     * @param sizeThreshold the size threshold to be set
     */
    public void setSizeThreshold(DataSize sizeThreshold) {
		this.sizeThreshold = sizeThreshold;
	}
	// @fold:off

}
