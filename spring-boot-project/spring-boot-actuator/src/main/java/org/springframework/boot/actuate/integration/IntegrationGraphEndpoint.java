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

package org.springframework.boot.actuate.integration;

import java.util.Collection;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.OperationResponseBody;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.integration.graph.Graph;
import org.springframework.integration.graph.IntegrationGraphServer;
import org.springframework.integration.graph.IntegrationNode;
import org.springframework.integration.graph.LinkNode;

/**
 * {@link Endpoint @Endpoint} to expose the Spring Integration graph.
 *
 * @author Tim Ysewyn
 * @since 2.1.0
 */
@Endpoint(id = "integrationgraph")
public class IntegrationGraphEndpoint {

	private final IntegrationGraphServer graphServer;

	/**
	 * Create a new {@code IntegrationGraphEndpoint} instance that exposes a graph
	 * containing all the Spring Integration components in the given
	 * {@link IntegrationGraphServer}.
	 * @param graphServer the integration graph server
	 */
	public IntegrationGraphEndpoint(IntegrationGraphServer graphServer) {
		this.graphServer = graphServer;
	}

	/**
     * Retrieves the graph descriptor from the graph server.
     * 
     * @return the graph descriptor
     */
    @ReadOperation
	public GraphDescriptor graph() {
		return new GraphDescriptor(this.graphServer.getGraph());
	}

	/**
     * Rebuilds the integration graph by calling the rebuild method of the graph server.
     */
    @WriteOperation
	public void rebuild() {
		this.graphServer.rebuild();
	}

	/**
	 * Description of a {@link Graph}.
	 */
	public static class GraphDescriptor implements OperationResponseBody {

		private final Map<String, Object> contentDescriptor;

		private final Collection<IntegrationNode> nodes;

		private final Collection<LinkNode> links;

		/**
         * Constructs a new GraphDescriptor object with the given Graph.
         * 
         * @param graph the Graph object to be used for constructing the GraphDescriptor
         */
        GraphDescriptor(Graph graph) {
			this.contentDescriptor = graph.getContentDescriptor();
			this.nodes = graph.getNodes();
			this.links = graph.getLinks();
		}

		/**
         * Returns the content descriptor of the GraphDescriptor.
         * 
         * @return the content descriptor as a Map<String, Object>
         */
        public Map<String, Object> getContentDescriptor() {
			return this.contentDescriptor;
		}

		/**
         * Returns the collection of integration nodes in the graph.
         *
         * @return the collection of integration nodes
         */
        public Collection<IntegrationNode> getNodes() {
			return this.nodes;
		}

		/**
         * Returns the collection of LinkNodes in the GraphDescriptor.
         *
         * @return the collection of LinkNodes in the GraphDescriptor
         */
        public Collection<LinkNode> getLinks() {
			return this.links;
		}

	}

}
