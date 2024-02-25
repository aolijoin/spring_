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

package org.springframework.boot.actuate.health;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.springframework.util.Assert;

/**
 * {@link NamedContributors} backed by a map with values adapted as necessary.
 *
 * @param <V> the value type
 * @param <C> the contributor type
 * @author Phillip Webb
 * @author Guirong Hu
 * @see CompositeHealthContributorMapAdapter
 * @see CompositeReactiveHealthContributorMapAdapter
 */
abstract class NamedContributorsMapAdapter<V, C> implements NamedContributors<C> {

	private final Map<String, C> map;

	/**
     * Constructs a new NamedContributorsMapAdapter with the specified map and value adapter.
     * 
     * @param map the map to be adapted
     * @param valueAdapter the function used to adapt the values in the map
     * @throws IllegalArgumentException if the map or valueAdapter is null
     */
    NamedContributorsMapAdapter(Map<String, V> map, Function<V, ? extends C> valueAdapter) {
		Assert.notNull(map, "Map must not be null");
		Assert.notNull(valueAdapter, "ValueAdapter must not be null");
		map.keySet().forEach(this::validateKey);
		this.map = Collections.unmodifiableMap(map.entrySet()
			.stream()
			.collect(LinkedHashMap::new,
					(result, entry) -> result.put(entry.getKey(), adapt(entry.getValue(), valueAdapter)), Map::putAll));

	}

	/**
     * Validates the given key.
     * 
     * @param value the key to be validated
     * @throws IllegalArgumentException if the key is null or contains a '/'
     */
    private void validateKey(String value) {
		Assert.notNull(value, "Map must not contain null keys");
		Assert.isTrue(!value.contains("/"), "Map keys must not contain a '/'");
	}

	/**
     * Adapts a value to a contributor using the provided value adapter function.
     * 
     * @param value the value to be adapted
     * @param valueAdapter the function used to adapt the value to a contributor
     * @return the adapted contributor
     * @throws IllegalArgumentException if the value is null
     */
    private C adapt(V value, Function<V, ? extends C> valueAdapter) {
		C contributor = (value != null) ? valueAdapter.apply(value) : null;
		Assert.notNull(contributor, "Map must not contain null values");
		return contributor;
	}

	/**
     * Returns an iterator over the elements in this NamedContributorsMapAdapter.
     *
     * @return an iterator over the elements in this NamedContributorsMapAdapter
     */
    @Override
	public Iterator<NamedContributor<C>> iterator() {
		Iterator<Entry<String, C>> iterator = this.map.entrySet().iterator();
		return new Iterator<>() {

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public NamedContributor<C> next() {
				Entry<String, C> entry = iterator.next();
				return NamedContributor.of(entry.getKey(), entry.getValue());
			}

		};
	}

	/**
     * Retrieves the contributor with the specified name from the map.
     *
     * @param name the name of the contributor to retrieve
     * @return the contributor with the specified name, or null if not found
     */
    @Override
	public C getContributor(String name) {
		return this.map.get(name);
	}

}
