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

package org.springframework.boot.actuate.web.mappings.servlet;

import java.util.Collections;
import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletContext;

import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.actuate.web.mappings.MappingDescriptionProvider;
import org.springframework.boot.actuate.web.mappings.servlet.FiltersMappingDescriptionProvider.FiltersMappingDescriptionProviderRuntimeHints;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.web.context.WebApplicationContext;

/**
 * A {@link MappingDescriptionProvider} that describes that mappings of any {@link Filter
 * Filters} registered with a {@link ServletContext}.
 *
 * @author Andy Wilkinson
 * @since 2.0.0
 */
@ImportRuntimeHints(FiltersMappingDescriptionProviderRuntimeHints.class)
public class FiltersMappingDescriptionProvider implements MappingDescriptionProvider {

	/**
     * Returns a list of {@link FilterRegistrationMappingDescription} objects that describe the mappings of filters in the given application context.
     * 
     * @param context the application context to describe the filter mappings for
     * @return a list of {@link FilterRegistrationMappingDescription} objects representing the filter mappings
     */
    @Override
	public List<FilterRegistrationMappingDescription> describeMappings(ApplicationContext context) {
		if (context instanceof WebApplicationContext webApplicationContext) {
			return webApplicationContext.getServletContext()
				.getFilterRegistrations()
				.values()
				.stream()
				.map(FilterRegistrationMappingDescription::new)
				.toList();
		}
		return Collections.emptyList();
	}

	/**
     * Returns the mapping name for the servlet filters.
     * 
     * @return the mapping name for the servlet filters
     */
    @Override
	public String getMappingName() {
		return "servletFilters";
	}

	/**
     * FiltersMappingDescriptionProviderRuntimeHints class.
     */
    static class FiltersMappingDescriptionProviderRuntimeHints implements RuntimeHintsRegistrar {

		private final BindingReflectionHintsRegistrar bindingRegistrar = new BindingReflectionHintsRegistrar();

		/**
         * Registers the runtime hints for FiltersMappingDescriptionProvider.
         * 
         * @param hints the runtime hints to be registered
         * @param classLoader the class loader to be used for reflection
         */
        @Override
		public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
			this.bindingRegistrar.registerReflectionHints(hints.reflection(),
					FilterRegistrationMappingDescription.class);
		}

	}

}
