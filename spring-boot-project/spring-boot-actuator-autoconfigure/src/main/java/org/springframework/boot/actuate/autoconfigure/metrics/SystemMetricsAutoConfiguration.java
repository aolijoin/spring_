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

package org.springframework.boot.actuate.autoconfigure.metrics;

import java.io.File;
import java.util.List;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;

import org.springframework.boot.actuate.metrics.system.DiskSpaceMetricsBinder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for system metrics.
 *
 * @author Stephane Nicoll
 * @author Chris Bono
 * @since 2.1.0
 */
@AutoConfiguration(after = { MetricsAutoConfiguration.class, CompositeMeterRegistryAutoConfiguration.class })
@ConditionalOnClass(MeterRegistry.class)
@ConditionalOnBean(MeterRegistry.class)
@EnableConfigurationProperties(MetricsProperties.class)
public class SystemMetricsAutoConfiguration {

	/**
     * Creates a new instance of UptimeMetrics if no other bean of type UptimeMetrics is present.
     * 
     * @return the UptimeMetrics instance
     */
    @Bean
	@ConditionalOnMissingBean
	public UptimeMetrics uptimeMetrics() {
		return new UptimeMetrics();
	}

	/**
     * Creates a new instance of ProcessorMetrics if no other bean of the same type is present in the application context.
     * 
     * @return the created ProcessorMetrics instance
     */
    @Bean
	@ConditionalOnMissingBean
	public ProcessorMetrics processorMetrics() {
		return new ProcessorMetrics();
	}

	/**
     * Creates a new instance of FileDescriptorMetrics if no other bean of the same type is present.
     * 
     * @return the created FileDescriptorMetrics instance
     */
    @Bean
	@ConditionalOnMissingBean
	public FileDescriptorMetrics fileDescriptorMetrics() {
		return new FileDescriptorMetrics();
	}

	/**
     * Creates a DiskSpaceMetricsBinder bean if no other bean of the same type is present.
     * 
     * @param properties the MetricsProperties object containing the configuration properties
     * @return a DiskSpaceMetricsBinder object configured with the specified paths and empty tags
     */
    @Bean
	@ConditionalOnMissingBean
	public DiskSpaceMetricsBinder diskSpaceMetrics(MetricsProperties properties) {
		List<File> paths = properties.getSystem().getDiskspace().getPaths();
		return new DiskSpaceMetricsBinder(paths, Tags.empty());
	}

}
