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

package org.springframework.boot.docs.data.nosql.mongodb.template;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * MyBean class.
 */
@Component
public class MyBean {

	private final MongoTemplate mongoTemplate;

	/**
     * Constructs a new instance of MyBean with the specified MongoTemplate.
     *
     * @param mongoTemplate the MongoTemplate to be used for database operations
     */
    public MyBean(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	// @fold:on // ...
	public MongoCollection<Document> someMethod() {
		return this.mongoTemplate.getCollection("users");
	}
	// @fold:off

}
