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

package org.springframework.boot.loader.tools;

import ch.qos.logback.classic.Level;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.ClassUtils;

/**
 * Utility to initialize logback (when present) to use INFO level logging.
 *
 * @author Dave Syer
 * @since 1.1.0
 */
public abstract class LogbackInitializer {

	/**
     * Initializes the Logback configuration.
     * Checks if the necessary classes for Logback are present and sets the root log level.
     * 
     * @see org.slf4j.LoggerFactory
     * @see ch.qos.logback.classic.Logger
     */
    public static void initialize() {
		if (ClassUtils.isPresent("org.slf4j.LoggerFactory", null)
				&& ClassUtils.isPresent("ch.qos.logback.classic.Logger", null)) {
			new Initializer().setRootLogLevel();
		}
	}

	/**
     * Initializer class.
     */
    private static final class Initializer {

		/**
         * Sets the root log level to INFO.
         * This method retrieves the root logger from the logger factory and sets its log level to INFO.
         * This will affect all loggers in the application.
         */
        void setRootLogLevel() {
			ILoggerFactory factory = LoggerFactory.getILoggerFactory();
			Logger logger = factory.getLogger(Logger.ROOT_LOGGER_NAME);
			((ch.qos.logback.classic.Logger) logger).setLevel(Level.INFO);
		}

	}

}
