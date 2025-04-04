package org.phong.horizon.storage.infrastructure.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.storage.enums.StorageErrorEnums;
import org.phong.horizon.storage.exceptions.CloudinaryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
@Slf4j
public class CloudinaryClientService {
    private static final Set<String> ALLOWED_CLIENT_PARAMS_FOR_SIGNING = new HashSet<>(Arrays.asList(
            "tags",           // Allow client to suggest tags (e.g., "user_tag,important")
            "context",        // Allow client to provide custom metadata (e.g., "alt=Description|caption=My Photo")
            "public_id",      // Allow client to suggest a public_id (server might prefix it)
            "eager",          // Allow client to request eager transformations
            "allowed_formats" // Allow client to specify allowed formats
            // Add other safe parameters as needed. Be cautious with parameters like 'folder', 'type', 'resource_type'.
    ));
    private final Cloudinary cloudinary;
    @Value("${cloudinary.api_key}")
    private String apiKey;

    public CloudinaryClientService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map<String, Object> generateUploadSignature(Map<String, Object> paramsFromClient) {
        long timestamp = System.currentTimeMillis() / 1000L;
        Map<String, Object> paramsToSign = new TreeMap<>(); // TreeMap for sorted keys

        paramsToSign.put("folder", "secure_uploads"); // Example: Enforce a folder
        paramsToSign.put("timestamp", timestamp);

        processClientParams(paramsFromClient, paramsToSign);

        try {
            String signature = cloudinary.apiSignRequest(paramsToSign, cloudinary.config.apiSecret);
            log.info("Generated Cloudinary signature. Params: {}", paramsToSign.keySet());

            Map<String, Object> response = new HashMap<>();
            response.put("signature", signature);
            response.put("timestamp", timestamp);
            response.put("api_key", this.apiKey);
            response.put("folder", paramsToSign.get("folder"));

            return response;
        } catch (Exception e) {
            log.error("Failed to generate Cloudinary signature", e);
            throw new CloudinaryException(StorageErrorEnums.CLOUDINARY_SIGNATURE_FAILED.getMessage(), e);
        }
    }

    public void deleteAssetOnCloudinary(String publicId, String resourceType) throws IOException {
        if (publicId == null || publicId.isBlank()) {
            throw new IllegalArgumentException(StorageErrorEnums.INVALID_PUBLIC_ID.getMessage());
        }
        String effectiveResourceType = (resourceType != null && !resourceType.isBlank()) ? resourceType : "image";
        if (resourceType == null || resourceType.isBlank()) {
            log.warn("Resource type not specified for Cloudinary deletion of public ID '{}', defaulting to 'image'.", publicId);
        }

        @SuppressWarnings("unchecked")
        Map<String, String> options = ObjectUtils.asMap("resource_type", effectiveResourceType);
        log.info("Calling Cloudinary API to delete asset. Public ID: '{}', Type: '{}'", publicId, effectiveResourceType);

        try {
            Map<?, ?> deletionResult = this.cloudinary.uploader().destroy(publicId, options);
            log.info("Cloudinary deletion API result for public ID '{}': {}", publicId, deletionResult.get("result"));
        } catch (IOException e) {
            log.error("Cloudinary API deletion failed for public ID '{}': {}", publicId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error calling Cloudinary deletion API for public ID '{}': {}", publicId, e.getMessage(), e);
            throw new CloudinaryException(StorageErrorEnums.UNEXPECTED_CLOUDINARY_ERROR.getMessage(), e);
        }
    }

    public String generateVideoPlaybackUrl(String publicId) {
        // Basic playback URL with auto format/quality optimization
        return cloudinary.url()
                .resourceType("video")
                .transformation(new Transformation<>()
                        .quality("auto")
                )// Ensure correct resource type
                .format("auto")// Let Cloudinary choose the best video format (mp4, webm etc.)
                // Let Cloudinary optimize quality/size
                .secure(true)
                .generate(publicId); // Use public_id from the asset entity
    }

    public String generateVideoThumbnailUrl(String publicId) {
        // Generate a specific thumbnail (e.g., 300px wide, cropped, as auto-format image)
        // Extracting a frame often defaults to the middle or first few seconds.
        // Use 'startOffset' ('so_') in transformation for specific frame.
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
                .generate(publicId); // Base on the video's public_id
    }

    private void processClientParams(Map<String, Object> paramsFromClient, Map<String, Object> paramsToSign) {
        if (paramsFromClient != null) {
            for (Map.Entry<String, Object> entry : paramsFromClient.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (ALLOWED_CLIENT_PARAMS_FOR_SIGNING.contains(key)) {
                    if (value != null && !value.toString().isEmpty()) { // Basic validation: ensure value is present

                        // --- Specific parameter handling/validation (Example) ---
                        if ("public_id".equals(key)) {
                            // Optional: Sanitize or prefix client-suggested public_id for security/organization
                            String sanitizedPublicId = sanitizePublicId(value.toString());
                            // Example Prefixing: paramsToSign.put(key, "user_uploads/" + sanitizedPublicId);
                            paramsToSign.put(key, sanitizedPublicId); // Use sanitized version
                        } else if ("context".equals(key) && value instanceof Map) {
                            @SuppressWarnings("unchecked")
                            var mapValue = (Map<String, String>) value;

                            paramsToSign.put(key, formatContext(mapValue));
                        } else {
                            paramsToSign.put(key, value);
                        }
                        log.debug("Including allowed client parameter in signature: {}={}", key, paramsToSign.get(key));
                    } else {
                        log.warn("Ignoring client parameter '{}' due to null or empty value.", key);
                    }
                } else {
                    log.warn("Ignoring disallowed client parameter: {}", key);
                }
            }
        }
    }

    private String sanitizePublicId(String publicId) {
        return publicId.replaceAll("[^a-zA-Z0-9-_]", "_");
    }

    private String formatContext(Map<String, String> context) {
        StringBuilder formattedContext = new StringBuilder();
        for (Map.Entry<String, String> entry : context.entrySet()) {
            if (!formattedContext.isEmpty()) {
                formattedContext.append("|");
            }
            formattedContext.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return formattedContext.toString();
    }
}