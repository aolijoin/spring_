/*
 * Copyright 2012-2019 the original author or authors.
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

package org.springframework.boot.actuate.info;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple {@link InfoContributor} that exposes a map.
 *
 * @author Dave Syer
 * @since 1.4.0
 */
public class MapInfoContributor implements InfoContributor {

	private final Map<String, Object> info;

	/**
     * Constructs a new MapInfoContributor with the specified information.
     * 
     * @param info the information to be stored in the MapInfoContributor
     */
    public MapInfoContributor(Map<String, Object> info) {
		this.info = new LinkedHashMap<>(info);
	}

	/**
     * Contributes the information to the given Info.Builder object.
     * 
     * @param builder the Info.Builder object to contribute the information to
     */
    @Override
	public void contribute(Info.Builder builder) {
		builder.withDetails(this.info);
	}

}
