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

package org.springframework.boot.actuate.autoconfigure.health;

import java.util.Map;
import java.util.function.Function;

import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;

/**
 * Base class for health contributor configurations that can combine source beans into a
 * composite.
 *
 * @param <I> the health indicator type
 * @param <B> the bean type
 * @author Stephane Nicoll
 * @author Phillip Webb
 * @since 2.2.0
 */
public abstract class CompositeReactiveHealthContributorConfiguration<I extends ReactiveHealthIndicator, B>
		extends AbstractCompositeHealthContributorConfiguration<ReactiveHealthContributor, I, B> {

	/**
	 * Creates a {@code CompositeReactiveHealthContributorConfiguration} that will use the
	 * given {@code indicatorFactory} to create {@link ReactiveHealthIndicator} instances.
	 * @param indicatorFactory the function to create health indicator instances
	 * @since 3.0.0
	 */
	public CompositeReactiveHealthContributorConfiguration(Function<B, I> indicatorFactory) {
		super(indicatorFactory);
	}

	/**
     * Creates a composite ReactiveHealthContributor from a map of beans.
     * 
     * @param beans a map of beans where the key is the name of the bean and the value is the bean itself
     * @return a composite ReactiveHealthContributor created from the provided beans
     */
    @Override
	protected final ReactiveHealthContributor createComposite(Map<String, B> beans) {
		return CompositeReactiveHealthContributor.fromMap(beans, this::createIndicator);
	}

}
