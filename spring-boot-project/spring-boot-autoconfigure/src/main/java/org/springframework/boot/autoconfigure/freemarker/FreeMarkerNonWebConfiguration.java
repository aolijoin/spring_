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

package org.springframework.boot.autoconfigure.freemarker;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

/**
 * Configuration for FreeMarker when used in a non-web context.
 *
 * @author Brian Clozel
 * @author Andy Wilkinson
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnNotWebApplication
class FreeMarkerNonWebConfiguration extends AbstractFreeMarkerConfiguration {

	/**
     * Constructs a new FreeMarkerNonWebConfiguration object with the specified properties.
     * 
     * @param properties the properties to be used for configuring the FreeMarkerNonWebConfiguration
     */
    FreeMarkerNonWebConfiguration(FreeMarkerProperties properties) {
		super(properties);
	}

	/**
     * Creates a FreeMarkerConfigurationFactoryBean if no other bean of the same type is present in the application context.
     * 
     * @return the FreeMarkerConfigurationFactoryBean instance
     */
    @Bean
	@ConditionalOnMissingBean
	FreeMarkerConfigurationFactoryBean freeMarkerConfiguration() {
		FreeMarkerConfigurationFactoryBean freeMarkerFactoryBean = new FreeMarkerConfigurationFactoryBean();
		applyProperties(freeMarkerFactoryBean);
		return freeMarkerFactoryBean;
	}

}
