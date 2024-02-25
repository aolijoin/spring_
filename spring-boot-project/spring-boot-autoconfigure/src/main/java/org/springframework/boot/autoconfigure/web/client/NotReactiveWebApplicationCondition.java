/*
 * Copyright 2012-2024 the original author or authors.
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

package org.springframework.boot.autoconfigure.web.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;

/**
 * {@link SpringBootCondition} that applies only when running in a non-reactive web
 * application.
 *
 * @author Phillip Webb
 */
class NotReactiveWebApplicationCondition extends NoneNestedConditions {

	/**
     * Constructs a new instance of the NotReactiveWebApplicationCondition class.
     * 
     * This constructor calls the super constructor with the ConfigurationPhase.PARSE_CONFIGURATION argument.
     */
    NotReactiveWebApplicationCondition() {
		super(ConfigurationPhase.PARSE_CONFIGURATION);
	}

	/**
     * ReactiveWebApplication class.
     */
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
	private static final class ReactiveWebApplication {

	}

}
