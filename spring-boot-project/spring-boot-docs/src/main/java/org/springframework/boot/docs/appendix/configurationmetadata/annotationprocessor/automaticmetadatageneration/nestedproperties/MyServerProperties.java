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

package org.springframework.boot.docs.appendix.configurationmetadata.annotationprocessor.automaticmetadatageneration.nestedproperties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MyServerProperties class.
 */
@ConfigurationProperties(prefix = "my.server")
public class MyServerProperties {

	private String name;

	private Host host;

	// @fold:on // getters/setters ...
	public String getName() {
		return this.name;
	}

	/**
     * Sets the name of the server.
     * 
     * @param name the name to set
     */
    public void setName(String name) {
		this.name = name;
	}

	/**
     * Returns the host object associated with this server properties.
     *
     * @return the host object
     */
    public Host getHost() {
		return this.host;
	}

	/**
     * Sets the host for the server properties.
     * 
     * @param host the host to be set
     */
    public void setHost(Host host) {
		this.host = host;
	}
	// @fold:off

	public static class Host {

		private String ip;

		private int port;

		// @fold:on // getters/setters ...
		public String getIp() {
			return this.ip;
		}

		/**
         * Sets the IP address of the host.
         * 
         * @param ip the IP address to be set
         */
        public void setIp(String ip) {
			this.ip = ip;
		}

		/**
         * Returns the port number of the host.
         *
         * @return the port number
         */
        public int getPort() {
			return this.port;
		}

		/**
         * Sets the port number for the host.
         * 
         * @param port the port number to be set
         */
        public void setPort(int port) {
			this.port = port;
		}
		// @fold:off // getters/setters ...

	}

}
