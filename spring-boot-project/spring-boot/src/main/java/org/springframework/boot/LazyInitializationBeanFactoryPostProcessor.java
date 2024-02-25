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

package org.springframework.boot;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.Ordered;

/**
 * {@link BeanFactoryPostProcessor} to set lazy-init on bean definitions that are not
 * {@link LazyInitializationExcludeFilter excluded} and have not already had a value
 * explicitly set.
 * <p>
 * Note that {@link SmartInitializingSingleton SmartInitializingSingletons} are
 * automatically excluded from lazy initialization to ensure that their
 * {@link SmartInitializingSingleton#afterSingletonsInstantiated() callback method} is
 * invoked.
 *
 * @author Andy Wilkinson
 * @author Madhura Bhave
 * @author Tyler Van Gorder
 * @author Phillip Webb
 * @since 2.2.0
 * @see LazyInitializationExcludeFilter
 */
public final class LazyInitializationBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

	/**
     * Post-processes the bean factory by applying lazy initialization exclusion filters to the bean definitions.
     * 
     * @param beanFactory the configurable listable bean factory to be processed
     * @throws BeansException if an error occurs during the bean factory post-processing
     */
    @Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		Collection<LazyInitializationExcludeFilter> filters = getFilters(beanFactory);
		for (String beanName : beanFactory.getBeanDefinitionNames()) {
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
			if (beanDefinition instanceof AbstractBeanDefinition abstractBeanDefinition) {
				postProcess(beanFactory, filters, beanName, abstractBeanDefinition);
			}
		}
	}

	/**
     * Retrieves the collection of lazy initialization exclude filters from the given bean factory.
     * 
     * @param beanFactory the configurable listable bean factory to retrieve the filters from
     * @return the collection of lazy initialization exclude filters
     */
    private Collection<LazyInitializationExcludeFilter> getFilters(ConfigurableListableBeanFactory beanFactory) {
		// Take care not to force the eager init of factory beans when getting filters
		ArrayList<LazyInitializationExcludeFilter> filters = new ArrayList<>(
				beanFactory.getBeansOfType(LazyInitializationExcludeFilter.class, false, false).values());
		filters.add(LazyInitializationExcludeFilter.forBeanTypes(SmartInitializingSingleton.class));
		return filters;
	}

	/**
     * Post-processes a bean definition to set the lazy initialization flag if it is not already set.
     * 
     * @param beanFactory the bean factory to post-process the bean definition in
     * @param filters the collection of lazy initialization exclude filters
     * @param beanName the name of the bean being processed
     * @param beanDefinition the bean definition being processed
     */
    private void postProcess(ConfigurableListableBeanFactory beanFactory,
			Collection<LazyInitializationExcludeFilter> filters, String beanName,
			AbstractBeanDefinition beanDefinition) {
		Boolean lazyInit = beanDefinition.getLazyInit();
		if (lazyInit != null) {
			return;
		}
		Class<?> beanType = getBeanType(beanFactory, beanName);
		if (!isExcluded(filters, beanName, beanDefinition, beanType)) {
			beanDefinition.setLazyInit(true);
		}
	}

	/**
     * Retrieves the type of a bean from the given bean factory using the specified bean name.
     * 
     * @param beanFactory the configurable listable bean factory
     * @param beanName the name of the bean
     * @return the type of the bean, or null if the bean does not exist
     */
    private Class<?> getBeanType(ConfigurableListableBeanFactory beanFactory, String beanName) {
		try {
			return beanFactory.getType(beanName, false);
		}
		catch (NoSuchBeanDefinitionException ex) {
			return null;
		}
	}

	/**
     * Checks if a bean is excluded from lazy initialization based on the provided filters.
     * 
     * @param filters the collection of lazy initialization exclude filters
     * @param beanName the name of the bean
     * @param beanDefinition the bean definition
     * @param beanType the type of the bean
     * @return true if the bean is excluded from lazy initialization, false otherwise
     */
    private boolean isExcluded(Collection<LazyInitializationExcludeFilter> filters, String beanName,
			AbstractBeanDefinition beanDefinition, Class<?> beanType) {
		if (beanType != null) {
			for (LazyInitializationExcludeFilter filter : filters) {
				if (filter.isExcluded(beanName, beanDefinition, beanType)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
     * Returns the order of this bean factory post-processor.
     * The order is set to the highest precedence.
     *
     * @return the order of this bean factory post-processor
     */
    @Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

}
