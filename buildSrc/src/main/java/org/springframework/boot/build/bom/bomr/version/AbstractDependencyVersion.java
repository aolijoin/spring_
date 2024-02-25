/*
 * Copyright 2012-2024 the original author or authors.
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

package org.springframework.boot.build.bom.bomr.version;

import org.apache.maven.artifact.versioning.ComparableVersion;

/**
 * Base class for {@link DependencyVersion} implementations.
 *
 * @author Andy Wilkinson
 */
abstract class AbstractDependencyVersion implements DependencyVersion {

	private final ComparableVersion comparableVersion;

	/**
     * Constructs a new AbstractDependencyVersion object with the given ComparableVersion.
     * 
     * @param comparableVersion the ComparableVersion to be set for this AbstractDependencyVersion
     */
    protected AbstractDependencyVersion(ComparableVersion comparableVersion) {
		this.comparableVersion = comparableVersion;
	}

	/**
     * Compares this DependencyVersion object with the specified object for order.
     * 
     * @param other the object to be compared
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object
     * @throws NullPointerException if the specified object is null
     */
    @Override
	public int compareTo(DependencyVersion other) {
		ComparableVersion otherComparable = (other instanceof AbstractDependencyVersion otherVersion)
				? otherVersion.comparableVersion : new ComparableVersion(other.toString());
		return this.comparableVersion.compareTo(otherComparable);
	}

	/**
     * Determines if the given dependency version is an upgrade compared to the current version.
     * 
     * @param candidate The dependency version to be checked.
     * @param movingToSnapshots A boolean indicating whether the upgrade is moving to snapshots.
     * @return True if the given version is an upgrade, false otherwise.
     */
    @Override
	public boolean isUpgrade(DependencyVersion candidate, boolean movingToSnapshots) {
		ComparableVersion comparableCandidate = (candidate instanceof AbstractDependencyVersion)
				? ((AbstractDependencyVersion) candidate).comparableVersion
				: new ComparableVersion(candidate.toString());
		return comparableCandidate.compareTo(this.comparableVersion) > 0;
	}

	/**
     * Compares this AbstractDependencyVersion object with the specified object for equality.
     * 
     * @param obj the object to compare with
     * @return true if the specified object is equal to this AbstractDependencyVersion object, false otherwise
     */
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AbstractDependencyVersion other = (AbstractDependencyVersion) obj;
		return this.comparableVersion.equals(other.comparableVersion);
	}

	/**
     * Returns the hash code value for this AbstractDependencyVersion object.
     * 
     * @return the hash code value for this object
     */
    @Override
	public int hashCode() {
		return this.comparableVersion.hashCode();
	}

	/**
     * Returns a string representation of the AbstractDependencyVersion object.
     * 
     * @return the string representation of the AbstractDependencyVersion object
     */
    @Override
	public String toString() {
		return this.comparableVersion.toString();
	}

}
