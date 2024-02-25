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

package org.springframework.boot.docs.actuator.micrometertracing.gettingstarted;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * MyApplication class.
 */
@RestController
@SpringBootApplication
public class MyApplication {

	private static final Log logger = LogFactory.getLog(MyApplication.class);

	/**
     * This method is the home endpoint of the application.
     * It returns a string "Hello World!".
     * 
     * @return The string "Hello World!".
     */
    @RequestMapping("/")
	String home() {
		logger.info("home() has been called");
		return "Hello World!";
	}

	/**
     * The main method is the entry point of the application.
     * It starts the Spring Boot application by calling the SpringApplication.run() method.
     * 
     * @param args the command line arguments passed to the application
     */
    public static void main(String[] args) {
		SpringApplication.run(MyApplication.class, args);
	}

}
