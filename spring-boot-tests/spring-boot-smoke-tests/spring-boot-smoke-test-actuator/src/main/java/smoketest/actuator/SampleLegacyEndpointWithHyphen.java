/*
 * Copyright 2012-2020 the original author or authors.
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

package smoketest.actuator;

import java.util.Collections;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

/**
 * SampleLegacyEndpointWithHyphen class.
 */
@Component
@Endpoint(id = "another-legacy")
public class SampleLegacyEndpointWithHyphen {

	/**
     * This method returns a map containing a single key-value pair.
     * The key is "legacy" and the value is also "legacy".
     * 
     * @return a map with a single key-value pair
     */
    @ReadOperation
	public Map<String, String> example() {
		return Collections.singletonMap("legacy", "legacy");
	}

}
