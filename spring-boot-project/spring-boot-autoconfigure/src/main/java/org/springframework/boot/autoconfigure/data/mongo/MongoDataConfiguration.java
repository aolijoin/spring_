/*
 * Copyright 2012-2022 the original author or authors.
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

package org.springframework.boot.autoconfigure.data.mongo;

import java.util.Collections;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mongodb.MongoManagedTypes;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * Base configuration class for Spring Data's mongo support.
 *
 * @author Madhura Bhave
 * @author Artsiom Yudovin
 * @author Scott Fredericks
 */
@Configuration(proxyBeanMethods = false)
class MongoDataConfiguration {

	/**
     * Returns the MongoManagedTypes object based on the ApplicationContext.
     * This method is annotated with @Bean and @ConditionalOnMissingBean to ensure that it is only created if there is no existing bean of the same type.
     * It uses the EntityScanner class to scan for classes annotated with @Document and returns a MongoManagedTypes object containing these classes.
     *
     * @param applicationContext The ApplicationContext object used for scanning classes.
     * @return The MongoManagedTypes object containing the scanned classes.
     * @throws ClassNotFoundException If any of the scanned classes cannot be found.
     */
    @Bean
	@ConditionalOnMissingBean
	static MongoManagedTypes mongoManagedTypes(ApplicationContext applicationContext) throws ClassNotFoundException {
		return MongoManagedTypes.fromIterable(new EntityScanner(applicationContext).scan(Document.class));
	}

	/**
     * Creates a MongoMappingContext bean if it is missing.
     * 
     * @param properties     the MongoProperties object containing the MongoDB configuration properties
     * @param conversions    the MongoCustomConversions object containing the custom conversions
     * @param managedTypes   the MongoManagedTypes object containing the managed types
     * @return               the MongoMappingContext bean
     */
    @Bean
	@ConditionalOnMissingBean
	MongoMappingContext mongoMappingContext(MongoProperties properties, MongoCustomConversions conversions,
			MongoManagedTypes managedTypes) {
		PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
		MongoMappingContext context = new MongoMappingContext();
		map.from(properties.isAutoIndexCreation()).to(context::setAutoIndexCreation);
		context.setManagedTypes(managedTypes);
		Class<?> strategyClass = properties.getFieldNamingStrategy();
		if (strategyClass != null) {
			context.setFieldNamingStrategy((FieldNamingStrategy) BeanUtils.instantiateClass(strategyClass));
		}
		context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
		return context;
	}

	/**
     * Returns an instance of MongoCustomConversions.
     * This method is annotated with @Bean and @ConditionalOnMissingBean, indicating that it is a bean definition and will only be created if there is no existing bean of the same type.
     * The method creates a new instance of MongoCustomConversions with an empty list of conversions.
     * 
     * @return an instance of MongoCustomConversions
     */
    @Bean
	@ConditionalOnMissingBean
	MongoCustomConversions mongoCustomConversions() {
		return new MongoCustomConversions(Collections.emptyList());
	}

}
