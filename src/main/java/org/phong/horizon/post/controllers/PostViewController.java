package org.phong.horizon.post.controllers;

import lombok.RequiredArgsConstructor;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.post.services.PostViewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostViewController {

    private final PostViewService postViewService;

    @PostMapping("/{postId}/view")
    public ResponseEntity<RestApiResponse<Void>> recordView(@PathVariable UUID postId) {
        postViewService.handleView(postId);

        return RestApiResponse.created();
    }
}
