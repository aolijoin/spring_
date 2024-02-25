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

package smoketest.jpa.repository;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import smoketest.jpa.domain.Tag;

import org.springframework.stereotype.Repository;

/**
 * JpaTagRepository class.
 */
@Repository
class JpaTagRepository implements TagRepository {

	@PersistenceContext
	private EntityManager entityManager;

	/**
     * Retrieves all tags from the database.
     *
     * @return a list of Tag objects representing all the tags in the database.
     */
    @Override
	public List<Tag> findAll() {
		return this.entityManager.createQuery("SELECT t FROM Tag t", Tag.class).getResultList();
	}

}
