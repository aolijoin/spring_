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

package org.springframework.boot.autoconfigure.data.couchbase;

import java.lang.annotation.Annotation;

import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.data.couchbase.repository.config.EnableReactiveCouchbaseRepositories;
import org.springframework.data.couchbase.repository.config.ReactiveCouchbaseRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/**
 * {@link ImportBeanDefinitionRegistrar} used to auto-configure Spring Data Couchbase
 * Reactive Repositories.
 *
 * @author Alex Derkach
 */
class CouchbaseReactiveRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {

	/**
     * Returns the annotation class that is used to enable reactive Couchbase repositories.
     *
     * @return the annotation class {@code EnableReactiveCouchbaseRepositories}
     */
    @Override
	protected Class<? extends Annotation> getAnnotation() {
		return EnableReactiveCouchbaseRepositories.class;
	}

	/**
     * Returns the configuration class for enabling reactive Couchbase repositories.
     * 
     * @return the configuration class for enabling reactive Couchbase repositories
     */
    @Override
	protected Class<?> getConfiguration() {
		return EnableReactiveCouchbaseRepositoriesConfiguration.class;
	}

	/**
     * Returns the repository configuration extension for Couchbase reactive repositories.
     *
     * @return The repository configuration extension.
     */
    @Override
	protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
		return new ReactiveCouchbaseRepositoryConfigurationExtension();
	}

	/**
     * EnableReactiveCouchbaseRepositoriesConfiguration class.
     */
    @EnableReactiveCouchbaseRepositories
	private static final class EnableReactiveCouchbaseRepositoriesConfiguration {

	}

}
