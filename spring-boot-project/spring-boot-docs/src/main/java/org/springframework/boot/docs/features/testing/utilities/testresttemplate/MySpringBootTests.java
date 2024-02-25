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

package org.springframework.boot.docs.features.testing.utilities.testresttemplate;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MySpringBootTests class.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MySpringBootTests {

	@Autowired
	private TestRestTemplate template;

	/**
     * Test case to verify the behavior of the request method.
     * 
     * <p>
     * This test method sends a GET request to the "/example" endpoint and retrieves the response headers. It then asserts that the location header has the expected host value of "other.example.com".
     * </p>
     * 
     * @throws Exception if an error occurs during the test
     */
    @Test
	void testRequest() {
		HttpHeaders headers = this.template.getForEntity("/example", String.class).getHeaders();
		assertThat(headers.getLocation()).hasHost("other.example.com");
	}

	/**
     * RestTemplateBuilderConfiguration class.
     */
    @TestConfiguration(proxyBeanMethods = false)
	static class RestTemplateBuilderConfiguration {

		/**
         * Creates a RestTemplateBuilder with a specified connection timeout and read timeout.
         * 
         * @return the RestTemplateBuilder with the specified timeouts
         */
        @Bean
		RestTemplateBuilder restTemplateBuilder() {
			return new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(1))
				.setReadTimeout(Duration.ofSeconds(1));
		}

	}

}
