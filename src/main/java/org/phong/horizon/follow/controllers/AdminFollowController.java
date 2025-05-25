package org.phong.horizon.follow.controllers;

import lombok.RequiredArgsConstructor;
import org.phong.horizon.follow.dtos.FollowRespond;
import org.phong.horizon.follow.services.FollowService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/api/follows")
@RequiredArgsConstructor
public class AdminFollowController {
    private final FollowService followService;

    @GetMapping
    public ResponseEntity<RestApiResponse<List<FollowRespond>>> getAll(@ParameterObject Pageable pageable) {
        return RestApiResponse.success(followService.getAll(pageable));
    }
}
