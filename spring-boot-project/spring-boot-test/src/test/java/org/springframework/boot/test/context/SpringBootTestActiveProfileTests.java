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

package org.springframework.boot.test.context;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SpringBootTest} with active profiles. See gh-1469.
 *
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest("spring.config.name=enableother")
@ActiveProfiles("override")
public class SpringBootTestActiveProfileTests {

	@Autowired
	private ApplicationContext context;

	@Test
	public void profiles() throws Exception {
		assertThat(this.context.getEnvironment().getActiveProfiles())
				.containsExactly("override");
	}

	@Configuration
	protected static class Config {

	}

}
