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

package org.springframework.boot.logging.java;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import org.springframework.boot.logging.LoggingSystemProperty;

/**
 * Simple 'Java Logging' {@link Formatter}.
 *
 * @author Phillip Webb
 * @since 1.0.0
 */
public class SimpleFormatter extends Formatter {

	private static final String DEFAULT_FORMAT = "[%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL] - %8$s %4$s [%7$s] --- %3$s: %5$s%6$s%n";

	private final String format = getOrUseDefault("LOG_FORMAT", DEFAULT_FORMAT);

	private final String pid = getOrUseDefault(LoggingSystemProperty.PID.getEnvironmentVariableName(), "????");

	/**
     * Formats the given log record into a string representation.
     * 
     * @param record the log record to be formatted
     * @return the formatted log record as a string
     */
    @Override
	public String format(LogRecord record) {
		Date date = new Date(record.getMillis());
		String source = record.getLoggerName();
		String message = formatMessage(record);
		String throwable = getThrowable(record);
		String thread = getThreadName();
		return String.format(this.format, date, source, record.getLoggerName(), record.getLevel().getLocalizedName(),
				message, throwable, thread, this.pid);
	}

	/**
     * Returns the stack trace of the throwable associated with the given log record.
     * 
     * @param record the log record containing the throwable
     * @return the stack trace of the throwable as a string, or an empty string if the throwable is null
     */
    private String getThrowable(LogRecord record) {
		if (record.getThrown() == null) {
			return "";
		}
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		printWriter.println();
		record.getThrown().printStackTrace(printWriter);
		printWriter.close();
		return stringWriter.toString();
	}

	/**
     * Returns the name of the current thread.
     * 
     * @return the name of the current thread, or an empty string if the name is null
     */
    private String getThreadName() {
		String name = Thread.currentThread().getName();
		return (name != null) ? name : "";
	}

	/**
     * Retrieves the value of the specified environment variable or uses the provided default value if the variable is not set.
     * 
     * @param key the name of the environment variable to retrieve
     * @param defaultValue the default value to use if the environment variable is not set
     * @return the value of the environment variable if set, otherwise the default value
     */
    private static String getOrUseDefault(String key, String defaultValue) {
		String value = null;
		try {
			value = System.getenv(key);
		}
		catch (Exception ex) {
			// ignore
		}
		if (value == null) {
			value = defaultValue;
		}
		return System.getProperty(key, value);
	}

}
