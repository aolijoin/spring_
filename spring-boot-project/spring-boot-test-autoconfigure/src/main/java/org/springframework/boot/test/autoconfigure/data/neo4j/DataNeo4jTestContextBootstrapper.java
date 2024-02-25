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

package org.springframework.boot.test.autoconfigure.data.neo4j;

import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.test.context.TestContextAnnotationUtils;
import org.springframework.test.context.TestContextBootstrapper;

/**
 * {@link TestContextBootstrapper} for {@link DataNeo4jTest @DataNeo4jTest} support.
 *
 * @author Artsiom Yudovin
 */
class DataNeo4jTestContextBootstrapper extends SpringBootTestContextBootstrapper {

	/**
     * Retrieves the properties specified in the {@link DataNeo4jTest} annotation for the given test class.
     * 
     * @param testClass the test class for which to retrieve the properties
     * @return an array of properties specified in the {@link DataNeo4jTest} annotation, or null if the annotation is not present
     */
    @Override
	protected String[] getProperties(Class<?> testClass) {
		DataNeo4jTest dataNeo4jTest = TestContextAnnotationUtils.findMergedAnnotation(testClass, DataNeo4jTest.class);
		return (dataNeo4jTest != null) ? dataNeo4jTest.properties() : null;
	}

}
