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

package org.springframework.boot.actuate.endpoint.web.reactive;

import java.util.Collections;
import java.util.Set;

import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebOperation;
import org.springframework.boot.actuate.endpoint.web.WebOperationRequestPredicate;
import org.springframework.boot.actuate.health.AdditionalHealthEndpointPath;
import org.springframework.boot.actuate.health.HealthEndpointGroup;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.result.method.RequestMappingInfo;

/**
 * A custom {@link HandlerMapping} that allows health groups to be mapped to an additional
 * path.
 *
 * @author Madhura Bhave
 * @since 2.6.0
 */
public class AdditionalHealthEndpointPathsWebFluxHandlerMapping extends AbstractWebFluxEndpointHandlerMapping {

	private final EndpointMapping endpointMapping;

	private final ExposableWebEndpoint endpoint;

	private final Set<HealthEndpointGroup> groups;

	/**
     * Constructs a new AdditionalHealthEndpointPathsWebFluxHandlerMapping with the specified endpoint mapping,
     * endpoint, and groups.
     *
     * @param endpointMapping the endpoint mapping to be used
     * @param endpoint the exposable web endpoint to be used
     * @param groups the set of health endpoint groups to be used
     */
    public AdditionalHealthEndpointPathsWebFluxHandlerMapping(EndpointMapping endpointMapping,
			ExposableWebEndpoint endpoint, Set<HealthEndpointGroup> groups) {
		super(endpointMapping, Collections.singletonList(endpoint), null, null, false);
		this.endpointMapping = endpointMapping;
		this.groups = groups;
		this.endpoint = endpoint;
	}

	/**
     * Initializes the handler methods for AdditionalHealthEndpointPathsWebFluxHandlerMapping.
     * Iterates through the WebOperations of the endpoint and registers the appropriate mappings
     * based on the request predicates and additional paths of the HealthEndpointGroups.
     * 
     * @since 1.0
     */
    @Override
	protected void initHandlerMethods() {
		for (WebOperation operation : this.endpoint.getOperations()) {
			WebOperationRequestPredicate predicate = operation.getRequestPredicate();
			String matchAllRemainingPathSegmentsVariable = predicate.getMatchAllRemainingPathSegmentsVariable();
			if (matchAllRemainingPathSegmentsVariable != null) {
				for (HealthEndpointGroup group : this.groups) {
					AdditionalHealthEndpointPath additionalPath = group.getAdditionalPath();
					if (additionalPath != null) {
						RequestMappingInfo requestMappingInfo = getRequestMappingInfo(operation,
								additionalPath.getValue());
						registerReadMapping(requestMappingInfo, this.endpoint, operation);
					}
				}
			}
		}
	}

	/**
     * Returns the RequestMappingInfo for the given WebOperation and additional path.
     * 
     * @param operation the WebOperation to create the RequestMappingInfo for
     * @param additionalPath the additional path to append to the endpoint mapping
     * @return the RequestMappingInfo for the given WebOperation and additional path
     */
    private RequestMappingInfo getRequestMappingInfo(WebOperation operation, String additionalPath) {
		WebOperationRequestPredicate predicate = operation.getRequestPredicate();
		String path = this.endpointMapping.createSubPath(additionalPath);
		RequestMethod method = RequestMethod.valueOf(predicate.getHttpMethod().name());
		String[] consumes = StringUtils.toStringArray(predicate.getConsumes());
		String[] produces = StringUtils.toStringArray(predicate.getProduces());
		return RequestMappingInfo.paths(path).methods(method).consumes(consumes).produces(produces).build();
	}

	/**
     * Returns the LinksHandler for handling links in the AdditionalHealthEndpointPathsWebFluxHandlerMapping.
     *
     * @return the LinksHandler for handling links, or null if not available.
     */
    @Override
	protected LinksHandler getLinksHandler() {
		return null;
	}

}
