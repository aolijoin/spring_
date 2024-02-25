/*
 * Copyright 2012-2021 the original author or authors.
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

package org.springframework.boot.docs.web.servlet.springmvc.errorhandling.errorpages;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * MyErrorViewResolver class.
 */
public class MyErrorViewResolver implements ErrorViewResolver {

	/**
     * Resolves the error view for the given request, status, and model.
     * 
     * @param request The HttpServletRequest object representing the current request.
     * @param status The HttpStatus object representing the status of the error.
     * @param model A Map containing the model values for the error view.
     * @return A ModelAndView object representing the resolved error view, or null if no error view is found.
     */
    @Override
	public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
		// Use the request or status to optionally return a ModelAndView
		if (status == HttpStatus.INSUFFICIENT_STORAGE) {
			// We could add custom model values here
			new ModelAndView("myview");
		}
		return null;
	}

}
