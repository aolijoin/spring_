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

package org.springframework.boot.autoconfigure.hazelcast;

import java.io.IOException;
import java.net.URL;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.client.config.YamlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

/**
 * Configuration for Hazelcast client.
 *
 * @author Vedran Pavic
 * @author Stephane Nicoll
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(HazelcastClient.class)
@ConditionalOnMissingBean(HazelcastInstance.class)
class HazelcastClientConfiguration {

	static final String CONFIG_SYSTEM_PROPERTY = "hazelcast.client.config";

	/**
     * Returns a HazelcastInstance based on the provided ClientConfig.
     * If the config has an instance name, it will try to get or create a HazelcastClient with that instance name.
     * Otherwise, it will create a new HazelcastClient with the provided config.
     *
     * @param config the ClientConfig to use for creating the HazelcastInstance
     * @return the HazelcastInstance based on the provided config
     */
    private static HazelcastInstance getHazelcastInstance(ClientConfig config) {
		if (StringUtils.hasText(config.getInstanceName())) {
			return HazelcastClient.getOrCreateHazelcastClient(config);
		}
		return HazelcastClient.newHazelcastClient(config);
	}

	/**
     * HazelcastClientConfigFileConfiguration class.
     */
    @Configuration(proxyBeanMethods = false)
	@ConditionalOnMissingBean(ClientConfig.class)
	@Conditional(HazelcastClientConfigAvailableCondition.class)
	static class HazelcastClientConfigFileConfiguration {

		/**
         * Creates and returns a HazelcastInstance using the provided HazelcastProperties and ResourceLoader.
         * 
         * @param properties the HazelcastProperties used to resolve the configuration location
         * @param resourceLoader the ResourceLoader used to load the configuration file
         * @return the created HazelcastInstance
         * @throws IOException if an I/O error occurs while loading the configuration file
         */
        @Bean
		HazelcastInstance hazelcastInstance(HazelcastProperties properties, ResourceLoader resourceLoader)
				throws IOException {
			Resource configLocation = properties.resolveConfigLocation();
			ClientConfig config = (configLocation != null) ? loadClientConfig(configLocation) : ClientConfig.load();
			config.setClassLoader(resourceLoader.getClassLoader());
			return getHazelcastInstance(config);
		}

		/**
         * Loads the client configuration from the specified resource location.
         * 
         * @param configLocation the resource location of the client configuration file
         * @return the loaded client configuration
         * @throws IOException if an I/O error occurs while reading the configuration file
         */
        private ClientConfig loadClientConfig(Resource configLocation) throws IOException {
			URL configUrl = configLocation.getURL();
			String configFileName = configUrl.getPath();
			if (configFileName.endsWith(".yaml") || configFileName.endsWith(".yml")) {
				return new YamlClientConfigBuilder(configUrl).build();
			}
			return new XmlClientConfigBuilder(configUrl).build();
		}

	}

	/**
     * HazelcastClientConfigConfiguration class.
     */
    @Configuration(proxyBeanMethods = false)
	@ConditionalOnSingleCandidate(ClientConfig.class)
	static class HazelcastClientConfigConfiguration {

		/**
         * Creates and returns a HazelcastInstance using the provided ClientConfig.
         *
         * @param config the ClientConfig object used to configure the HazelcastInstance
         * @return the created HazelcastInstance
         */
        @Bean
		HazelcastInstance hazelcastInstance(ClientConfig config) {
			return getHazelcastInstance(config);
		}

	}

}
