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

package org.springframework.boot.test.autoconfigure.webservices.server;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.ws.test.server.MockWebServiceClient;

/**
 * Auto-configuration for {@link MockWebServiceClient} support.
 *
 * @author Daniil Razorenov
 * @since 2.6.0
 * @see AutoConfigureMockWebServiceClient
 */
@AutoConfiguration
@ConditionalOnClass(MockWebServiceClient.class)
public class MockWebServiceClientAutoConfiguration {

	/**
     * Creates a mock web service client using the provided application context.
     * 
     * @param applicationContext the application context to use for creating the mock web service client
     * @return the created mock web service client
     */
    @Bean
	MockWebServiceClient mockWebServiceClient(ApplicationContext applicationContext) {
		return MockWebServiceClient.createClient(applicationContext);
	}

}
