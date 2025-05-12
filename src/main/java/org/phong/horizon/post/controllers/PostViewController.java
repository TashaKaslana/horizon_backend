package org.phong.horizon.post.controllers;

import lombok.RequiredArgsConstructor;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.post.dtos.PostViewRespond;
import org.phong.horizon.post.services.PostViewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class PostViewController {

    private final PostViewService postViewService;

    @PostMapping("/{postId}/view")
    public ResponseEntity<RestApiResponse<Void>> recordView(@PathVariable UUID postId) {
        postViewService.handleView(postId);

        return RestApiResponse.created();
    }

    @GetMapping("/{postId}/views")
    public ResponseEntity<RestApiResponse<PostViewRespond>> getTotalViews(@PathVariable UUID postId) {
        PostViewRespond res = new PostViewRespond(postViewService.getTotalViews(postId));
        return RestApiResponse.success(res);
    }
}
