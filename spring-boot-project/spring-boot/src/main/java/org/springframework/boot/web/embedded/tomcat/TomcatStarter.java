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

package org.springframework.boot.web.embedded.tomcat;

import java.util.Set;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.web.servlet.ServletContextInitializer;

/**
 * {@link ServletContainerInitializer} used to trigger {@link ServletContextInitializer
 * ServletContextInitializers} and track startup errors.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
class TomcatStarter implements ServletContainerInitializer {

	private static final Log logger = LogFactory.getLog(TomcatStarter.class);

	private final ServletContextInitializer[] initializers;

	private volatile Exception startUpException;

	/**
     * Constructs a new TomcatStarter object with the specified initializers.
     *
     * @param initializers an array of ServletContextInitializer objects to be used for initializing the Tomcat server
     */
    TomcatStarter(ServletContextInitializer[] initializers) {
		this.initializers = initializers;
	}

	/**
     * This method is called during the startup of the Tomcat server.
     * It iterates over the set of initializers and calls the onStartup method for each initializer.
     * If an exception occurs during the startup process, it is caught and stored in the startUpException variable.
     * The method also logs the error message for information purposes.
     * 
     * @param classes         a set of classes
     * @param servletContext  the servlet context
     * @throws ServletException if an exception occurs during the startup process
     */
    @Override
	public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
		try {
			for (ServletContextInitializer initializer : this.initializers) {
				initializer.onStartup(servletContext);
			}
		}
		catch (Exception ex) {
			this.startUpException = ex;
			// Prevent Tomcat from logging and re-throwing when we know we can
			// deal with it in the main thread, but log for information here.
			if (logger.isErrorEnabled()) {
				logger.error("Error starting Tomcat context. Exception: " + ex.getClass().getName() + ". Message: "
						+ ex.getMessage());
			}
		}
	}

	/**
     * Returns the start-up exception that occurred during the execution of the TomcatStarter class.
     * 
     * @return the start-up exception that occurred during the execution of the TomcatStarter class
     */
    Exception getStartUpException() {
		return this.startUpException;
	}

}
