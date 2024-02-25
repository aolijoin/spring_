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

package org.springframework.boot.actuate.metrics.web.jetty;

import java.util.Collections;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jetty.JettySslHandshakeMetrics;
import org.eclipse.jetty.server.Server;

/**
 * {@link AbstractJettyMetricsBinder} for {@link JettySslHandshakeMetrics}.
 *
 * @author Chris Bono
 * @since 2.6.0
 */
public class JettySslHandshakeMetricsBinder extends AbstractJettyMetricsBinder {

	private final MeterRegistry meterRegistry;

	private final Iterable<Tag> tags;

	/**
     * Constructs a new JettySslHandshakeMetricsBinder with the specified MeterRegistry.
     * 
     * @param meterRegistry the MeterRegistry to bind the metrics to
     */
    public JettySslHandshakeMetricsBinder(MeterRegistry meterRegistry) {
		this(meterRegistry, Collections.emptyList());
	}

	/**
     * Constructs a new JettySslHandshakeMetricsBinder with the specified MeterRegistry and tags.
     *
     * @param meterRegistry the MeterRegistry to bind the metrics to
     * @param tags the tags to associate with the metrics
     */
    public JettySslHandshakeMetricsBinder(MeterRegistry meterRegistry, Iterable<Tag> tags) {
		this.meterRegistry = meterRegistry;
		this.tags = tags;
	}

	/**
     * Binds the SSL handshake metrics to the given Jetty server.
     * 
     * @param server the Jetty server to bind the metrics to
     */
    @Override
	protected void bindMetrics(Server server) {
		JettySslHandshakeMetrics.addToAllConnectors(server, this.meterRegistry, this.tags);
	}

}
