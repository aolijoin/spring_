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

package org.springframework.boot.loader.launch;

import java.util.List;

import org.springframework.boot.loader.jarmode.JarMode;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ClassUtils;

/**
 * Delegate class used to run the nested jar in a specific mode.
 *
 * @author Phillip Webb
 */
final class JarModeRunner {

	static final String DISABLE_SYSTEM_EXIT = JarModeRunner.class.getName() + ".DISABLE_SYSTEM_EXIT";

	/**
     * Private constructor for the JarModeRunner class.
     */
    private JarModeRunner() {
	}

	/**
     * The main method of the JarModeRunner class.
     * 
     * This method is responsible for running the application in the specified jar mode.
     * It retrieves the jar mode from the system property "jarmode" and loads the available
     * jar mode candidates using the SpringFactoriesLoader. It then iterates through the
     * candidates and checks if any of them accepts the specified jar mode. If a candidate
     * accepts the mode, it runs the candidate's run method passing the mode and command line
     * arguments. If no candidate accepts the mode, an error message is printed to the standard
     * error stream. If the system property "DISABLE_SYSTEM_EXIT" is not set to true, the
     * application exits with a status code of 1.
     * 
     * @param args the command line arguments passed to the application
     */
    static void main(String[] args) {
		String mode = System.getProperty("jarmode");
		List<JarMode> candidates = SpringFactoriesLoader.loadFactories(JarMode.class,
				ClassUtils.getDefaultClassLoader());
		for (JarMode candidate : candidates) {
			if (candidate.accepts(mode)) {
				candidate.run(mode, args);
				return;
			}
		}
		System.err.println("Unsupported jarmode '" + mode + "'");
		if (!Boolean.getBoolean(DISABLE_SYSTEM_EXIT)) {
			System.exit(1);
		}
	}

}
