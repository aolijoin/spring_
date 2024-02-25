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

package org.test;

import org.springframework.aot.generate.GenerationContext;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * ResourceRegisteringAotProcessor class.
 */
class ResourceRegisteringAotProcessor implements BeanFactoryInitializationAotProcessor {

	/**
     * This method is used to process ahead of time the bean factory initialization for the ResourceRegisteringAotProcessor class.
     * 
     * @param beanFactory The configurable listable bean factory.
     * @return The BeanFactoryInitializationAotContribution object.
     */
    @Override
	public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
		return new BeanFactoryInitializationAotContribution() {

			@Override
			public void applyTo(GenerationContext generationContext,
					BeanFactoryInitializationCode beanFactoryInitializationCode) {
				generationContext.getGeneratedFiles().addResourceFile("generated-resource", "content");
				generationContext.getGeneratedFiles().addResourceFile("nested/generated-resource", "nested content");
			}

		};
	}

}
