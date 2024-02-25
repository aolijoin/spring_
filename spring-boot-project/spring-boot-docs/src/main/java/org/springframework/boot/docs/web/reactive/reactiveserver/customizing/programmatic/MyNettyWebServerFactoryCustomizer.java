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

package org.springframework.boot.docs.web.reactive.reactiveserver.customizing.programmatic;

import java.time.Duration;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

/**
 * MyNettyWebServerFactoryCustomizer class.
 */
@Component
public class MyNettyWebServerFactoryCustomizer implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {

	/**
     * Customize the NettyReactiveWebServerFactory by adding a server customizer to set the idle timeout.
     * 
     * @param factory the NettyReactiveWebServerFactory to customize
     */
    @Override
	public void customize(NettyReactiveWebServerFactory factory) {
		factory.addServerCustomizers((server) -> server.idleTimeout(Duration.ofSeconds(20)));
	}

}
