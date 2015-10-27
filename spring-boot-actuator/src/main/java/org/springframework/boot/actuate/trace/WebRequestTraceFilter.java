/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.actuate.trace;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.actuate.trace.TraceProperties.Include;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.core.Ordered;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Servlet {@link Filter} that logs all requests to a {@link TraceRepository}.
 *
 * @author Dave Syer
 * @author Wallace Wadge
 */
public class WebRequestTraceFilter extends OncePerRequestFilter implements Ordered {

	private final Log logger = LogFactory.getLog(WebRequestTraceFilter.class);

	private boolean dumpRequests = false;

	// Not LOWEST_PRECEDENCE, but near the end, so it has a good chance of catching all
	// enriched headers, but users can add stuff after this if they want to
	private int order = Ordered.LOWEST_PRECEDENCE - 10;

	private final TraceRepository repository;

	private ErrorAttributes errorAttributes;

	private final TraceProperties properties;

	/**
	 * Create a new {@link WebRequestTraceFilter} instance.
	 * @param traceRepository the trace repository.
	 * @deprecated since 1.3.0 in favor of
	 * {@link #WebRequestTraceFilter(TraceRepository, TraceProperties)}
	 */
	@Deprecated
	public WebRequestTraceFilter(TraceRepository traceRepository) {
		this(traceRepository, new TraceProperties());
	}

	/**
	 * Create a new {@link WebRequestTraceFilter} instance.
	 * @param repository the trace repository
	 * @param properties the trace properties
	 */
	public WebRequestTraceFilter(TraceRepository repository, TraceProperties properties) {
		this.repository = repository;
		this.properties = properties;
	}

	/**
	 * Debugging feature. If enabled, and trace logging is enabled then web request
	 * headers will be logged.
	 * @param dumpRequests if requests should be logged
	 */
	public void setDumpRequests(boolean dumpRequests) {
		this.dumpRequests = dumpRequests;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
					throws ServletException, IOException {
		Map<String, Object> trace = getTrace(request);
		logTrace(request, trace);
		try {
			filterChain.doFilter(request, response);
		}
		finally {
			enhanceTrace(trace, response);
			this.repository.add(trace);
		}
	}

	protected Map<String, Object> getTrace(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		Throwable exception = (Throwable) request
				.getAttribute("javax.servlet.error.exception");
		Principal userPrincipal = request.getUserPrincipal();
		Map<String, Object> trace = new LinkedHashMap<String, Object>();
		Map<String, Object> headers = new LinkedHashMap<String, Object>();
		trace.put("method", request.getMethod());
		trace.put("path", request.getRequestURI());
		trace.put("headers", headers);
		if (isIncluded(Include.REQUEST_HEADERS)) {
			headers.put("request", getRequestHeaders(request));
		}
		add(trace, Include.PATH_INFO, "pathInfo", request.getPathInfo());
		add(trace, Include.PATH_TRANSLATED, "pathTranslated",
				request.getPathTranslated());
		add(trace, Include.CONTEXT_PATH, "contextPath", request.getContextPath());
		add(trace, Include.USER_PRINCIPAL, "userPrincipal",
				(userPrincipal == null ? null : userPrincipal.getName()));
		add(trace, Include.PARAMETERS, "parameters", request.getParameterMap());
		add(trace, Include.QUERY_STRING, "query", request.getQueryString());
		add(trace, Include.AUTH_TYPE, "authType", request.getAuthType());
		add(trace, Include.REMOTE_ADDRESS, "remoteAddress", request.getRemoteAddr());
		add(trace, Include.SESSION_ID, "sessionId",
				(session == null ? null : session.getId()));
		add(trace, Include.REMOTE_USER, "remoteUser", request.getRemoteUser());
		if (isIncluded(Include.ERRORS) && exception != null
				&& this.errorAttributes != null) {
			trace.put("error", this.errorAttributes
					.getErrorAttributes(new ServletRequestAttributes(request), true));
		}
		return trace;
	}

	private Map<String, Object> getRequestHeaders(HttpServletRequest request) {
		Map<String, Object> headers = new LinkedHashMap<String, Object>();
		Enumeration<String> names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			List<String> values = Collections.list(request.getHeaders(name));
			Object value = values;
			if (values.size() == 1) {
				value = values.get(0);
			}
			else if (values.isEmpty()) {
				value = "";
			}
			headers.put(name, value);
		}
		return headers;
	}

	@SuppressWarnings("unchecked")
	protected void enhanceTrace(Map<String, Object> trace, HttpServletResponse response) {
		if (isIncluded(Include.RESPONSE_HEADERS)) {
			Map<String, Object> headers = (Map<String, Object>) trace.get("headers");
			headers.put("response", getResponseHeaders(response));
		}
	}

	private Map<String, String> getResponseHeaders(HttpServletResponse response) {
		Map<String, String> headers = new LinkedHashMap<String, String>();
		for (String header : response.getHeaderNames()) {
			String value = response.getHeader(header);
			headers.put(header, value);
		}
		headers.put("status", "" + response.getStatus());
		return headers;
	}

	private void logTrace(HttpServletRequest request, Map<String, Object> trace) {
		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Processing request " + request.getMethod() + " "
					+ request.getRequestURI());
			if (this.dumpRequests) {
				this.logger.trace("Headers: " + trace.get("headers"));
			}
		}
	}

	private void add(Map<String, Object> trace, Include include, String name,
			Object value) {
		if (isIncluded(include) && value != null) {
			trace.put(name, value);
		}
	}

	private boolean isIncluded(Include include) {
		return this.properties.getInclude().contains(include);
	}

	public void setErrorAttributes(ErrorAttributes errorAttributes) {
		this.errorAttributes = errorAttributes;
	}

}
