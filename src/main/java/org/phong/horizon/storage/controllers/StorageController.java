package org.phong.horizon.storage.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.storage.dtos.AssetRespond;
import org.phong.horizon.storage.dtos.UploadCompleteRequest;
import org.phong.horizon.storage.service.StorageService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/storage")
@AllArgsConstructor
public class StorageController {
    private final StorageService storageService;

    @PostMapping("/create")
    @LogActivity(
            activityCode = ActivityTypeCode.ASSET_CREATE,
            description = "Create a new asset",
            targetType = SystemCategory.ASSET,
            targetIdExpression = "#result.body.data.id"
    )
    public ResponseEntity<RestApiResponse<AssetRespond>> createAsset(@Valid @RequestBody UploadCompleteRequest uploadData) {
        AssetRespond asset = storageService.createAsset(uploadData);
        return RestApiResponse.created(asset);
    }

    @GetMapping("/asset/{id}")
    public ResponseEntity<RestApiResponse<AssetRespond>> getAssetById(@PathVariable UUID id) {
        AssetRespond asset = storageService.getAssetById(id);
        return RestApiResponse.success(asset);
    }

    @GetMapping("/asset/public/{publicId}")
    public ResponseEntity<RestApiResponse<AssetRespond>> getAssetByPublicId(@PathVariable String publicId) {
        AssetRespond asset = storageService.getAssetByPublicId(publicId);
        return RestApiResponse.success(asset);
    }

    @GetMapping("/playback-url/{publicId}")
    public ResponseEntity<RestApiResponse<String>> getVideoPlaybackUrl(@PathVariable String publicId) {
        String playbackUrl = storageService.generateVideoPlaybackUrl(publicId);
        return RestApiResponse.success(playbackUrl);
    }

    @GetMapping("/thumbnail-url/{publicId}")
    public ResponseEntity<RestApiResponse<String>> getVideoThumbnailUrl(@PathVariable String publicId) {
        String thumbnailUrl = storageService.generateVideoThumbnailUrl(publicId);
        return RestApiResponse.success(thumbnailUrl);
    }

    @DeleteMapping("/delete/{id}")
    @LogActivity(
            activityCode = ActivityTypeCode.ASSET_DELETE,
            description = "Delete an asset",
            targetType = SystemCategory.ASSET,
            targetIdExpression = "#id"
    )
    public ResponseEntity<RestApiResponse<Void>> deleteAsset(@PathVariable UUID id) {
        storageService.deleteAsset(id);
        return RestApiResponse.noContent();
    }

    @PostMapping("/signature")
    public ResponseEntity<RestApiResponse<Map<String, Object>>> generateUploadSignature(@RequestBody Map<String, Object> paramsFromClient) {
        Map<String, Object> signature = storageService.generateUploadSignature(paramsFromClient);
        return RestApiResponse.success(signature);
    }
}
