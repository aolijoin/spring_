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

package org.springframework.boot.buildpack.platform.build;

import java.util.Comparator;

import org.springframework.util.Assert;

/**
 * A lifecycle version number comprised of a major, minor and patch value.
 *
 * @author Phillip Webb
 */
class LifecycleVersion implements Comparable<LifecycleVersion> {

	private static final Comparator<LifecycleVersion> COMPARATOR = Comparator.comparingInt(LifecycleVersion::getMajor)
		.thenComparingInt(LifecycleVersion::getMinor)
		.thenComparing(LifecycleVersion::getPatch);

	private final int major;

	private final int minor;

	private final int patch;

	/**
     * Constructs a new instance of the LifecycleVersion class with the specified major, minor, and patch versions.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     */
    LifecycleVersion(int major, int minor, int patch) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}

	/**
     * Compares this LifecycleVersion object with the specified object for equality.
     * 
     * @param obj the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		LifecycleVersion other = (LifecycleVersion) obj;
		boolean result = true;
		result = result && this.major == other.major;
		result = result && this.minor == other.minor;
		result = result && this.patch == other.patch;
		return result;
	}

	/**
     * Returns a hash code value for the object. This method overrides the default implementation of the hashCode() method.
     * The hash code is calculated based on the major, minor, and patch values of the LifecycleVersion object.
     *
     * @return the hash code value for the object
     */
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.major;
		result = prime * result + this.minor;
		result = prime * result + this.patch;
		return result;
	}

	/**
     * Returns a string representation of the version in the format "vX.Y.Z".
     *
     * @return the string representation of the version
     */
    @Override
	public String toString() {
		return "v" + this.major + "." + this.minor + "." + this.patch;
	}

	/**
	 * Return if this version is greater than or equal to the specified version.
	 * @param other the version to compare
	 * @return {@code true} if this version is greater than or equal to the specified
	 * version
	 */
	boolean isEqualOrGreaterThan(LifecycleVersion other) {
		return compareTo(other) >= 0;
	}

	/**
     * Compares this LifecycleVersion object with the specified object for order.
     * 
     * @param other the object to be compared
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object
     * @throws NullPointerException if the specified object is null
     */
    @Override
	public int compareTo(LifecycleVersion other) {
		return COMPARATOR.compare(this, other);
	}

	/**
	 * Return the major version number.
	 * @return the major version
	 */
	int getMajor() {
		return this.major;
	}

	/**
	 * Return the minor version number.
	 * @return the minor version
	 */
	int getMinor() {
		return this.minor;
	}

	/**
	 * Return the patch version number.
	 * @return the patch version
	 */
	int getPatch() {
		return this.patch;
	}

	/**
	 * Factory method to parse a string into a {@link LifecycleVersion} instance.
	 * @param value the value to parse.
	 * @return the corresponding {@link LifecycleVersion}
	 * @throws IllegalArgumentException if the value could not be parsed
	 */
	static LifecycleVersion parse(String value) {
		Assert.hasText(value, "Value must not be empty");
		if (value.startsWith("v") || value.startsWith("V")) {
			value = value.substring(1);
		}
		String[] components = value.split("\\.");
		Assert.isTrue(components.length <= 3, "Malformed version number '" + value + "'");
		int[] versions = new int[3];
		for (int i = 0; i < components.length; i++) {
			try {
				versions[i] = Integer.parseInt(components[i]);
			}
			catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Malformed version number '" + value + "'", ex);
			}
		}
		return new LifecycleVersion(versions[0], versions[1], versions[2]);
	}

}
