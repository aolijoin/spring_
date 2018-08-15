/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.test.autoconfigure.web.client.bootstrap;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTestContextBootstrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link RestClientTestContextBootstrapper} with
 * {@link ApplicationContextInitializer}.
 *
 * @author Artsiom Yudovin
 */
@RunWith(SpringRunner.class)
@BootstrapWith(RestClientTestContextBootstrapper.class)
@ContextConfiguration(initializers = RestClientTestContextBootstrapperWithInitializersTests.CustomInitializer.class)
public class RestClientTestContextBootstrapperWithInitializersTests {

	@Autowired
	private ApplicationContext context;

	@Test
	public void foundConfiguration() {
		Object bean = this.context
				.getBean(RestClientTestContextBootstrapperExampleConfig.class);
		assertThat(bean).isNotNull();
	}

	public static class CustomInitializer
			implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {
		}

	}

}
