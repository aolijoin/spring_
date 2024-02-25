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

package org.springframework.boot.docs.features.testing.springbootapplications.withrunningserver;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * MyRandomPortWebTestClientTests class.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MyRandomPortWebTestClientTests {

	/**
     * This is an example test method that tests the "/" endpoint using a WebTestClient.
     * It verifies that the response status is OK and the response body is "Hello World".
     *
     * @param webClient the WebTestClient instance used for making HTTP requests
     */
    @Test
	void exampleTest(@Autowired WebTestClient webClient) {
		// @formatter:off
		webClient
			.get().uri("/")
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class).isEqualTo("Hello World");
		// @formatter:on
	}

}
