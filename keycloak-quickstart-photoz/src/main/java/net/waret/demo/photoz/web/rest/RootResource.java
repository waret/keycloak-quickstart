/*
 * Copyright 2017 the original author or authors.
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
package net.waret.demo.photoz.web.rest;

import net.waret.demo.photoz.web.rest.employee.EmployeeResource;
import net.waret.demo.photoz.web.rest.employee.ManagerResource;
import net.waret.demo.photoz.web.rest.employee.OrderResource;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author Greg Turnquist
 */
@RestController
public class RootResource {

	@GetMapping("/api/v1")
    public ResponseEntity<ResourceSupport> root() {

		ResourceSupport resourceSupport = new ResourceSupport();

		resourceSupport.add(linkTo(methodOn(RootResource.class).root()).withSelfRel());
		resourceSupport.add(linkTo(methodOn(EmployeeResource.class).all()).withRel("employees"));
		resourceSupport.add(linkTo(methodOn(EmployeeResource.class).findAllDetailedEmployees()).withRel("detailedEmployees"));
		resourceSupport.add(linkTo(methodOn(ManagerResource.class).findAll()).withRel("managers"));
		resourceSupport.add(linkTo(methodOn(OrderResource.class).all()).withRel("orders"));

		return ResponseEntity.ok(resourceSupport);
	}

}
