/*
 * Copyright 2012-2017 the original author or authors.
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

package org.springframework.boot.autoconfigure.thymeleaf;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;

import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.template.TemplateLocation;
import org.springframework.boot.autoconfigure.web.ConditionalOnEnabledResourceChain;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Thymeleaf.
 *
 * @author Dave Syer
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Brian Clozel
 * @author Eddú Meléndez
 * @author Bruce Brouwer
 *
 */
@Configuration
@EnableConfigurationProperties(ThymeleafProperties.class)
@ConditionalOnClass(TemplateMode.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class ThymeleafAutoConfiguration {

	@Configuration
	@ConditionalOnMissingBean(name = "defaultTemplateResolver")
	static class DefaultTemplateResolverConfiguration {

		private static final Log logger = LogFactory
				.getLog(DefaultTemplateResolverConfiguration.class);

		private final ThymeleafProperties properties;

		private final ApplicationContext applicationContext;

		DefaultTemplateResolverConfiguration(ThymeleafProperties properties,
				ApplicationContext applicationContext) {
			this.properties = properties;
			this.applicationContext = applicationContext;
		}

		@PostConstruct
		public void checkTemplateLocationExists() {
			boolean checkTemplateLocation = this.properties.isCheckTemplateLocation();
			if (checkTemplateLocation) {
				TemplateLocation location = new TemplateLocation(
						this.properties.getPrefix());
				if (!location.exists(this.applicationContext)) {
					logger.warn("Cannot find template location: " + location
							+ " (please add some templates or check "
							+ "your Thymeleaf configuration)");
				}
			}
		}

		@Bean
		public SpringResourceTemplateResolver defaultTemplateResolver() {
			SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
			resolver.setApplicationContext(this.applicationContext);
			resolver.setPrefix(this.properties.getPrefix());
			resolver.setSuffix(this.properties.getSuffix());
			resolver.setTemplateMode(this.properties.getMode());
			if (this.properties.getEncoding() != null) {
				resolver.setCharacterEncoding(this.properties.getEncoding().name());
			}
			resolver.setCacheable(this.properties.isCache());
			Integer order = this.properties.getTemplateResolverOrder();
			if (order != null) {
				resolver.setOrder(order);
			}
			resolver.setCheckExistence(this.properties.isCheckTemplate());
			return resolver;
		}

	}

	@Configuration
	@ConditionalOnClass({ Servlet.class })
	@ConditionalOnWebApplication(type = Type.SERVLET)
	static class ThymeleafViewResolverConfiguration {

		private final ThymeleafProperties properties;

		private final SpringTemplateEngine templateEngine;

		ThymeleafViewResolverConfiguration(ThymeleafProperties properties,
				SpringTemplateEngine templateEngine) {
			this.properties = properties;
			this.templateEngine = templateEngine;
		}

		@Bean
		@ConditionalOnMissingBean(name = "thymeleafViewResolver")
		@ConditionalOnProperty(name = "spring.thymeleaf.enabled", matchIfMissing = true)
		public ThymeleafViewResolver thymeleafViewResolver() {
			ThymeleafViewResolver resolver = new ThymeleafViewResolver();
			resolver.setTemplateEngine(this.templateEngine);
			resolver.setCharacterEncoding(this.properties.getEncoding().name());
			resolver.setContentType(appendCharset(this.properties.getContentType(),
					resolver.getCharacterEncoding()));
			resolver.setExcludedViewNames(this.properties.getExcludedViewNames());
			resolver.setViewNames(this.properties.getViewNames());
			// This resolver acts as a fallback resolver (e.g. like a
			// InternalResourceViewResolver) so it needs to have low precedence
			resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 5);
			resolver.setCache(this.properties.isCache());
			return resolver;
		}

		private String appendCharset(MimeType type, String charset) {
			if (type.getCharset() != null) {
				return type.toString();
			}
			LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
			parameters.put("charset", charset);
			parameters.putAll(type.getParameters());
			return new MimeType(type, parameters).toString();
		}

	}

	@Configuration
	@ConditionalOnMissingBean(SpringTemplateEngine.class)
	protected static class ThymeleafDefaultConfiguration {

		private final Collection<ITemplateResolver> templateResolvers;

		private final Collection<IDialect> dialects;

		public ThymeleafDefaultConfiguration(
				Collection<ITemplateResolver> templateResolvers,
				ObjectProvider<Collection<IDialect>> dialectsProvider) {
			this.templateResolvers = templateResolvers;
			this.dialects = dialectsProvider.getIfAvailable();
		}

		@Bean
		public SpringTemplateEngine templateEngine() {
			SpringTemplateEngine engine = new SpringTemplateEngine();
			for (ITemplateResolver templateResolver : this.templateResolvers) {
				engine.addTemplateResolver(templateResolver);
			}
			if (!CollectionUtils.isEmpty(this.dialects)) {
				for (IDialect dialect : this.dialects) {
					engine.addDialect(dialect);
				}
			}
			return engine;
		}

	}

	@Configuration
	@ConditionalOnClass(name = "nz.net.ultraq.thymeleaf.LayoutDialect")
	protected static class ThymeleafWebLayoutConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public LayoutDialect layoutDialect() {
			return new LayoutDialect();
		}

	}

	@Configuration
	@ConditionalOnClass(DataAttributeDialect.class)
	protected static class DataAttributeDialectConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public DataAttributeDialect dialect() {
			return new DataAttributeDialect();
		}

	}

	@Configuration
	@ConditionalOnClass({ SpringSecurityDialect.class })
	protected static class ThymeleafSecurityDialectConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public SpringSecurityDialect securityDialect() {
			return new SpringSecurityDialect();
		}

	}

	@Configuration
	@ConditionalOnClass(Java8TimeDialect.class)
	protected static class ThymeleafJava8TimeDialect {

		@Bean
		@ConditionalOnMissingBean
		public Java8TimeDialect java8TimeDialect() {
			return new Java8TimeDialect();
		}

	}

	@Configuration
	@ConditionalOnWebApplication(type = Type.SERVLET)
	protected static class ThymeleafResourceHandlingConfig {

		@Bean
		@ConditionalOnMissingBean
		@ConditionalOnEnabledResourceChain
		public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
			return new ResourceUrlEncodingFilter();
		}

	}

	@Configuration
	@ConditionalOnWebApplication(type = Type.SERVLET)
	protected static class ThymeleafWelcomeTemplateConfiguration {

		@Bean
		public WelcomeTemplateHandlerMapping welcomePageHandlerMapping(
				final ThymeleafProperties properties,
				final ApplicationContext applicationContext) {
			final String path = properties.getPrefix() + "index" + properties.getSuffix();
			return new WelcomeTemplateHandlerMapping(path, applicationContext);
		}
	}

	static final class WelcomeTemplateHandlerMapping extends AbstractUrlHandlerMapping {
		private static final Log logger = LogFactory
				.getLog(WelcomeTemplateHandlerMapping.class);

		private WelcomeTemplateHandlerMapping(final String path,
				final ResourcePatternResolver resolver) {
			final TemplateLocation welcome = new TemplateLocation(path);
			if (welcome.exists(resolver)) {
				logger.info("Adding Thymeleaf welcome template: " + path);
				ParameterizableViewController controller = new ParameterizableViewController();
				controller.setViewName("index");
				setRootHandler(controller);
				setOrder(-10);
			}
		}

		@Override
		public Object getHandlerInternal(HttpServletRequest request) throws Exception {
			for (MediaType mediaType : getAcceptedMediaTypes(request)) {
				if (mediaType.includes(MediaType.TEXT_HTML)) {
					return super.getHandlerInternal(request);
				}
			}
			return null;
		}

		private List<MediaType> getAcceptedMediaTypes(HttpServletRequest request) {
			String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
			return MediaType.parseMediaTypes(
					StringUtils.hasText(acceptHeader) ? acceptHeader : "*/*");
		}

	}

}
