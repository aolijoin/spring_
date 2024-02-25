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

package org.springframework.boot.docs.io.restclient.restclient.ssl;

import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * MyService class.
 */
@Service
public class MyService {

	private final RestClient restClient;

	/**
     * Constructs a new instance of MyService with the provided RestClient.Builder and RestClientSsl.
     * 
     * @param restClientBuilder the RestClient.Builder used to build the RestClient instance
     * @param ssl the RestClientSsl used to configure SSL settings for the RestClient instance
     */
    public MyService(RestClient.Builder restClientBuilder, RestClientSsl ssl) {
		this.restClient = restClientBuilder.baseUrl("https://example.org").apply(ssl.fromBundle("mybundle")).build();
	}

	/**
     * Makes a REST call to retrieve the details for a given name.
     * 
     * @param name the name for which details are to be retrieved
     * @return the details of the given name
     */
    public Details someRestCall(String name) {
		return this.restClient.get().uri("/{name}/details", name).retrieve().body(Details.class);
	}

}
