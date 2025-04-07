package org.phong.horizon.follow.controllers;

import lombok.RequiredArgsConstructor;
import org.phong.horizon.follow.dtos.FollowRespond;
import org.phong.horizon.follow.services.FollowService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/follows")
@RequiredArgsConstructor
public class AdminFollowController {
    private final FollowService followService;

    @GetMapping
    public ResponseEntity<Page<FollowRespond>> getAll(Pageable pageable) {
        return ResponseEntity.ok(followService.getAll(pageable));
    }
}
