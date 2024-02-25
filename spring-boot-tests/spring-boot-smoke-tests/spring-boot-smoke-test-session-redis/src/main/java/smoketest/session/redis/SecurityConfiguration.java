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

package smoketest.session.redis;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Security configuration.
 *
 * @author Madhura Bhave
 */
@Configuration(proxyBeanMethods = false)
class SecurityConfiguration {

	/**
     * Configures the security filter chain for managing security in the application.
     * 
     * @param http the HttpSecurity object used for configuring security
     * @return the SecurityFilterChain object representing the configured security filter chain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
	SecurityFilterChain managementSecurityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((requests) -> {
			requests.requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll();
			requests.anyRequest().authenticated();
		});
		http.formLogin(withDefaults());
		http.httpBasic(withDefaults());
		http.csrf((csrf) -> csrf.disable());
		return http.build();
	}

}
