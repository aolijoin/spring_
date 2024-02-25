/*
 * Copyright 2012-2019 the original author or authors.
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

package org.springframework.boot.test.autoconfigure.restdocs;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.mockmvc.UriConfigurer;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;

/**
 * A {@link MockMvcBuilderCustomizer} that configures Spring REST Docs.
 *
 * @author Andy Wilkinson
 * @since 1.5.22
 */
public class RestDocsMockMvcBuilderCustomizer implements InitializingBean, MockMvcBuilderCustomizer {

	private final RestDocsProperties properties;

	private final MockMvcRestDocumentationConfigurer delegate;

	private final RestDocumentationResultHandler resultHandler;

	/**
     * Constructs a new RestDocsMockMvcBuilderCustomizer with the specified properties, delegate, and result handler.
     * 
     * @param properties the RestDocsProperties to be used
     * @param delegate the MockMvcRestDocumentationConfigurer to be used
     * @param resultHandler the RestDocumentationResultHandler to be used
     */
    RestDocsMockMvcBuilderCustomizer(RestDocsProperties properties, MockMvcRestDocumentationConfigurer delegate,
			RestDocumentationResultHandler resultHandler) {
		this.properties = properties;
		this.delegate = delegate;
		this.resultHandler = resultHandler;
	}

	/**
     * {@inheritDoc}
     * 
     * Sets up the properties for the RestDocsMockMvcBuilderCustomizer.
     * 
     * This method is called after all the properties have been set, and it configures the URI scheme, host, and port based on the provided properties.
     * 
     * @throws Exception if an error occurs while setting up the properties
     */
    @Override
	public void afterPropertiesSet() throws Exception {
		PropertyMapper map = PropertyMapper.get();
		RestDocsProperties properties = this.properties;
		UriConfigurer uri = this.delegate.uris();
		map.from(properties::getUriScheme).whenHasText().to(uri::withScheme);
		map.from(properties::getUriHost).whenHasText().to(uri::withHost);
		map.from(properties::getUriPort).whenNonNull().to(uri::withPort);
	}

	/**
     * Customize the RestDocsMockMvcBuilder by applying the delegate builder and result handler.
     * 
     * @param builder the ConfigurableMockMvcBuilder to customize
     */
    @Override
	public void customize(ConfigurableMockMvcBuilder<?> builder) {
		builder.apply(this.delegate);
		if (this.resultHandler != null) {
			builder.alwaysDo(this.resultHandler);
		}
	}

}
