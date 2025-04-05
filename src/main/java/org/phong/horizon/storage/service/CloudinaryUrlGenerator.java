package org.phong.horizon.storage.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.phong.horizon.storage.infrastructure.persistence.entities.Asset;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CloudinaryUrlGenerator {

    private final Cloudinary cloudinary;

    @Named("videoPlaybackUrl")
    public String generateVideoPlaybackUrl(Asset videoAsset) {
        if (videoAsset == null) {
            log.warn("Video asset is null, cannot generate playback URL.");
            return null;
        }

        return cloudinary.url()
                .resourceType("video")
                .transformation(new Transformation<>()
                        .quality("auto")
                )// Ensure correct resource type
                .format("auto")// Let Cloudinary choose the best video format (mp4, webm etc.)
                // Let Cloudinary optimize quality/size
                .secure(true)
                .generate(videoAsset.getPublicId()); // Use public_id from the asset entity
    }

    @Named("videoThumbnailUrl")
    public String generateVideoThumbnailUrl(Asset videoAsset) {
        if (videoAsset == null) {
            log.warn("Video asset is null, cannot generate thumbnail URL.");
            return null;
        }

        return cloudinary.url()
                .resourceType("video") // Still video resource type...
                // ...but generating an image! Cloudinary handles this.
                .transformation(new Transformation<>()
                        .width(300)        // Desired thumbnail width
                        .crop("limit")     // Limit width, auto height (or use 'fill', 'thumb')
                        // .startOffset("auto") // Or "2" for second 2, "50p" for 50%
                        .quality("auto")
                )
                .format("auto") // Generate best IMAGE format (jpg, png, webp)
                .secure(true)
                .generate(videoAsset.getPublicId()); // Base on the video's public_id
    }
}