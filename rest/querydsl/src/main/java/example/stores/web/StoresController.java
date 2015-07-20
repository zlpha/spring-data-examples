/*
 * Copyright 2014-2015 the original author or authors.
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
package example.stores.web;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.data.web.querydsl.QuerydslPredicate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mysema.query.types.Predicate;

import example.stores.Store;
import example.stores.StoreRepository;

/**
 * A Spring MVC controller to produce an HTML frontend.
 * 
 * @author Oliver Gierke
 */
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class StoresController {

	private static final Point PIVOTAL_SF = new Point(-122.4041764, 37.7819286);

	private final StoreRepository repository;

	/**
	 * Looks up the stores in the given distance around the given location.
	 * 
	 * @param model the {@link Model} to populate.
	 * @param location the optional location, if none is given, no search results will be returned.
	 * @param distance the distance to use, if none is given the {@link #DEFAULT_DISTANCE} is used.
	 * @param pageable the pagination information
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	String filter(Model model, @QuerydslPredicate(root = Store.class) Predicate predicate, Pageable pageable) {

		Page<Store> stores = repository.findAll(predicate, pageable);
		Point point = PIVOTAL_SF;

		if (stores.hasContent()) {

			Store first = stores.iterator().next();
			point = new Point(first.getAddress().getLocation()[0], first.getAddress().getLocation()[1]);
		}

		model.addAttribute("stores", stores);
		model.addAttribute("location", point);

		return "index";
	}
}
