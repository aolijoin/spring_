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

package org.springframework.boot.docs.howto.dataaccess.configurehibernatenamingstrategy.standard;

import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyHibernateConfiguration class.
 */
@Configuration(proxyBeanMethods = false)
class MyHibernateConfiguration {

	/**
     * Returns an instance of PhysicalNamingStrategyStandardImpl that is used as a case-sensitive physical naming strategy.
     * 
     * @return an instance of PhysicalNamingStrategyStandardImpl
     */
    @Bean
	PhysicalNamingStrategyStandardImpl caseSensitivePhysicalNamingStrategy() {
		return new PhysicalNamingStrategyStandardImpl();
	}

}
