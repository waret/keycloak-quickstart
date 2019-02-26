/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.waret.demo.alice.web.rest.v3.album;

import net.waret.demo.alice.repository.AlbumRepository;
import net.waret.demo.alice.web.rest.v3.dto.Profile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/profile")
public class ProfileService {

    private final HttpServletRequest request;

    private final AlbumRepository albumRepository;

    private static final String PROFILE_VIEW = "urn:photoz.com:scopes:profile:view";

    public ProfileService(HttpServletRequest request,
            AlbumRepository albumRepository) {
        this.request = request;
        this.albumRepository = albumRepository;
    }

    @GetMapping
    public ResponseEntity view() {
        Principal userPrincipal = request.getUserPrincipal();
        return ResponseEntity.ok(new Profile(userPrincipal.getName(), albumRepository.findByUserId(userPrincipal.getName()).size()));
    }
}
