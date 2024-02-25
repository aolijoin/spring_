/*
 * Copyright 2012-2023 the original author or authors.
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

package org.springframework.boot.context.properties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.aot.generate.GenerationContext;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.bind.BindMethod;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.BindableRuntimeHintsRegistrar;
import org.springframework.util.ClassUtils;

/**
 * {@link BeanFactoryInitializationAotProcessor} that contributes runtime hints for
 * configuration properties-annotated beans.
 *
 * @author Stephane Nicoll
 * @author Christoph Strobl
 * @author Sebastien Deleuze
 * @author Andy Wilkinson
 */
class ConfigurationPropertiesBeanFactoryInitializationAotProcessor implements BeanFactoryInitializationAotProcessor {

	/**
     * Processes the ConfigurationProperties annotations ahead of time.
     * 
     * @param beanFactory the bean factory to process
     * @return the ConfigurationPropertiesReflectionHintsContribution object containing the bindables
     */
    @Override
	public ConfigurationPropertiesReflectionHintsContribution processAheadOfTime(
			ConfigurableListableBeanFactory beanFactory) {
		String[] beanNames = beanFactory.getBeanNamesForAnnotation(ConfigurationProperties.class);
		List<Bindable<?>> bindables = new ArrayList<>();
		for (String beanName : beanNames) {
			Class<?> beanType = beanFactory.getType(beanName, false);
			if (beanType != null) {
				BindMethod bindMethod = beanFactory.containsBeanDefinition(beanName)
						? (BindMethod) beanFactory.getBeanDefinition(beanName).getAttribute(BindMethod.class.getName())
						: null;
				bindables.add(Bindable.of(ClassUtils.getUserClass(beanType))
					.withBindMethod((bindMethod != null) ? bindMethod : BindMethod.JAVA_BEAN));
			}
		}
		return (!bindables.isEmpty()) ? new ConfigurationPropertiesReflectionHintsContribution(bindables) : null;
	}

	/**
     * ConfigurationPropertiesReflectionHintsContribution class.
     */
    static final class ConfigurationPropertiesReflectionHintsContribution
			implements BeanFactoryInitializationAotContribution {

		private final List<Bindable<?>> bindables;

		/**
         * Constructs a new ConfigurationPropertiesReflectionHintsContribution object with the specified list of bindables.
         * 
         * @param bindables the list of bindables to be used by the ConfigurationPropertiesReflectionHintsContribution object
         */
        private ConfigurationPropertiesReflectionHintsContribution(List<Bindable<?>> bindables) {
			this.bindables = bindables;
		}

		/**
         * Applies the configuration properties reflection hints to the given generation context and bean factory initialization code.
         * 
         * @param generationContext The generation context to apply the hints to.
         * @param beanFactoryInitializationCode The bean factory initialization code to apply the hints to.
         */
        @Override
		public void applyTo(GenerationContext generationContext,
				BeanFactoryInitializationCode beanFactoryInitializationCode) {
			BindableRuntimeHintsRegistrar.forBindables(this.bindables)
				.registerHints(generationContext.getRuntimeHints());
		}

		/**
         * Returns an iterable of bindable objects.
         *
         * @return an iterable of bindable objects
         */
        Iterable<Bindable<?>> getBindables() {
			return this.bindables;
		}

	}

}
