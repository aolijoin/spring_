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

package org.springframework.boot.actuate.autoconfigure.session;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.session.ReactiveSessionsEndpoint;
import org.springframework.boot.actuate.session.SessionsEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.ReactiveFindByIndexNameSessionRepository;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link SessionsEndpoint}.
 *
 * @author Vedran Pavic
 * @since 2.0.0
 */
@AutoConfiguration(after = SessionAutoConfiguration.class)
@ConditionalOnClass(Session.class)
@ConditionalOnAvailableEndpoint(endpoint = SessionsEndpoint.class)
public class SessionsEndpointAutoConfiguration {

	/**
     * ServletSessionEndpointConfiguration class.
     */
    @Configuration(proxyBeanMethods = false)
	@ConditionalOnWebApplication(type = Type.SERVLET)
	@ConditionalOnBean(SessionRepository.class)
	static class ServletSessionEndpointConfiguration {

		/**
         * Creates a new SessionsEndpoint bean if no other bean of the same type is present.
         * 
         * @param sessionRepository The session repository to be used by the endpoint.
         * @param indexedSessionRepository An optional indexed session repository to be used by the endpoint.
         * @return The created SessionsEndpoint bean.
         */
        @Bean
		@ConditionalOnMissingBean
		SessionsEndpoint sessionEndpoint(SessionRepository<? extends Session> sessionRepository,
				ObjectProvider<FindByIndexNameSessionRepository<? extends Session>> indexedSessionRepository) {
			return new SessionsEndpoint(sessionRepository, indexedSessionRepository.getIfAvailable());
		}

	}

	/**
     * ReactiveSessionEndpointConfiguration class.
     */
    @Configuration(proxyBeanMethods = false)
	@ConditionalOnWebApplication(type = Type.REACTIVE)
	@ConditionalOnBean(ReactiveSessionRepository.class)
	static class ReactiveSessionEndpointConfiguration {

		/**
         * Creates a new {@link ReactiveSessionsEndpoint} bean if no other bean of the same type is present.
         * 
         * @param sessionRepository The {@link ReactiveSessionRepository} used to manage sessions.
         * @param indexedSessionRepository The {@link ReactiveFindByIndexNameSessionRepository} used to manage indexed sessions.
         * @return The {@link ReactiveSessionsEndpoint} bean.
         */
        @Bean
		@ConditionalOnMissingBean
		ReactiveSessionsEndpoint sessionsEndpoint(ReactiveSessionRepository<? extends Session> sessionRepository,
				ObjectProvider<ReactiveFindByIndexNameSessionRepository<? extends Session>> indexedSessionRepository) {
			return new ReactiveSessionsEndpoint(sessionRepository, indexedSessionRepository.getIfAvailable());
		}

	}

}
