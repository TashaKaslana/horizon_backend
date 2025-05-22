package org.phong.horizon.post.subdomain.tag.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TagPropertyConflictException extends RuntimeException {
    public TagPropertyConflictException(String message) {
        super(message);
    }
}

