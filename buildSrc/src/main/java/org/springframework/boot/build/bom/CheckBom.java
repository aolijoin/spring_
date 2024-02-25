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

package org.springframework.boot.build.bom;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.Restriction;
import org.apache.maven.artifact.versioning.VersionRange;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

import org.springframework.boot.build.bom.Library.Group;
import org.springframework.boot.build.bom.Library.Module;
import org.springframework.boot.build.bom.Library.ProhibitedVersion;
import org.springframework.boot.build.bom.Library.VersionAlignment;
import org.springframework.boot.build.bom.bomr.version.DependencyVersion;

/**
 * Checks the validity of a bom.
 *
 * @author Andy Wilkinson
 */
public class CheckBom extends DefaultTask {

	private final BomExtension bom;

	/**
     * Constructs a new CheckBom object with the specified BomExtension.
     * 
     * @param bom the BomExtension object to be used for checking BOM
     */
    @Inject
	public CheckBom(BomExtension bom) {
		this.bom = bom;
	}

	/**
     * Checks the Bill of Materials (BOM) for any errors.
     * 
     * @throws GradleException if any errors are found in the BOM
     */
    @TaskAction
	void checkBom() {
		List<String> errors = new ArrayList<>();
		for (Library library : this.bom.getLibraries()) {
			checkLibrary(library, errors);
		}
		if (!errors.isEmpty()) {
			System.out.println();
			errors.forEach(System.out::println);
			System.out.println();
			throw new GradleException("Bom check failed. See previous output for details.");
		}
	}

	/**
     * Checks the given library for any errors and adds them to the provided list of errors.
     * 
     * @param library The library to be checked.
     * @param errors  The list of errors to which any found errors will be added.
     */
    private void checkLibrary(Library library, List<String> errors) {
		List<String> libraryErrors = new ArrayList<>();
		checkExclusions(library, libraryErrors);
		checkProhibitedVersions(library, libraryErrors);
		checkVersionAlignment(library, libraryErrors);
		if (!libraryErrors.isEmpty()) {
			errors.add(library.getName());
			for (String libraryError : libraryErrors) {
				errors.add("    - " + libraryError);
			}
		}
	}

	/**
     * Checks for exclusions in the given library and adds any errors to the provided list.
     * 
     * @param library The library to check for exclusions.
     * @param errors The list to add any errors found during the check.
     */
    private void checkExclusions(Library library, List<String> errors) {
		for (Group group : library.getGroups()) {
			for (Module module : group.getModules()) {
				if (!module.getExclusions().isEmpty()) {
					checkExclusions(group.getId(), module, library.getVersion().getVersion(), errors);
				}
			}
		}
	}

	/**
     * Checks the exclusions for a given module in a specified group and version.
     * 
     * @param groupId the group ID of the module
     * @param module the module to check exclusions for
     * @param version the version of the module
     * @param errors a list to store any errors encountered during the check
     */
    private void checkExclusions(String groupId, Module module, DependencyVersion version, List<String> errors) {
		Set<String> resolved = getProject().getConfigurations()
			.detachedConfiguration(
					getProject().getDependencies().create(groupId + ":" + module.getName() + ":" + version))
			.getResolvedConfiguration()
			.getResolvedArtifacts()
			.stream()
			.map((artifact) -> artifact.getModuleVersion().getId())
			.map((id) -> id.getGroup() + ":" + id.getModule().getName())
			.collect(Collectors.toSet());
		Set<String> exclusions = module.getExclusions()
			.stream()
			.map((exclusion) -> exclusion.getGroupId() + ":" + exclusion.getArtifactId())
			.collect(Collectors.toSet());
		Set<String> unused = new TreeSet<>();
		for (String exclusion : exclusions) {
			if (!resolved.contains(exclusion)) {
				if (exclusion.endsWith(":*")) {
					String group = exclusion.substring(0, exclusion.indexOf(':') + 1);
					if (resolved.stream().noneMatch((candidate) -> candidate.startsWith(group))) {
						unused.add(exclusion);
					}
				}
				else {
					unused.add(exclusion);
				}
			}
		}
		exclusions.removeAll(resolved);
		if (!unused.isEmpty()) {
			errors.add("Unnecessary exclusions on " + groupId + ":" + module.getName() + ": " + exclusions);
		}
	}

	/**
     * Checks if the current version of a library is prohibited or falls within an ineffective version range.
     * 
     * @param library The library to check.
     * @param errors A list to store any errors encountered during the check.
     */
    private void checkProhibitedVersions(Library library, List<String> errors) {
		ArtifactVersion currentVersion = new DefaultArtifactVersion(library.getVersion().getVersion().toString());
		for (ProhibitedVersion prohibited : library.getProhibitedVersions()) {
			if (prohibited.isProhibited(library.getVersion().getVersion().toString())) {
				errors.add("Current version " + currentVersion + " is prohibited");
			}
			else {
				VersionRange versionRange = prohibited.getRange();
				if (versionRange != null) {
					for (Restriction restriction : versionRange.getRestrictions()) {
						ArtifactVersion upperBound = restriction.getUpperBound();
						if (upperBound == null) {
							return;
						}
						int comparison = currentVersion.compareTo(upperBound);
						if ((restriction.isUpperBoundInclusive() && comparison <= 0)
								|| ((!restriction.isUpperBoundInclusive()) && comparison < 0)) {
							return;
						}
					}
					errors.add("Version range " + versionRange + " is ineffective as the current version, "
							+ currentVersion + ", is greater than its upper bound");
				}
			}
		}
	}

	/**
     * Checks if the version of a library is aligned with the specified version alignment.
     * 
     * @param library The library to check.
     * @param errors A list to store any errors encountered during the check.
     */
    private void checkVersionAlignment(Library library, List<String> errors) {
		VersionAlignment versionAlignment = library.getVersionAlignment();
		if (versionAlignment == null) {
			return;
		}
		Set<String> alignedVersions = versionAlignment.resolve();
		if (alignedVersions.size() == 1) {
			String alignedVersion = alignedVersions.iterator().next();
			if (!alignedVersion.equals(library.getVersion().getVersion().toString())) {
				errors.add("Version " + library.getVersion().getVersion() + " is misaligned. It should be "
						+ alignedVersion + ".");
			}
		}
		else {
			if (alignedVersions.isEmpty()) {
				errors.add("Version alignment requires a single version but none were found.");
			}
			else {
				errors.add("Version alignment requires a single version but " + alignedVersions.size() + " were found: "
						+ alignedVersions + ".");
			}
		}
	}

}
