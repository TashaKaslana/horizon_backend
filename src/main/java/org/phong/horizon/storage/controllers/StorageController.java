package org.phong.horizon.storage.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.storage.dtos.AssetRespond;
import org.phong.horizon.storage.dtos.UploadCompleteRequest;
import org.phong.horizon.storage.service.StorageService;
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
            targetIdExpression = "#result.body.id"
    )
    public ResponseEntity<AssetRespond> createAsset(@RequestBody UploadCompleteRequest uploadData) {
        AssetRespond asset = storageService.createAsset(uploadData);
        return ResponseEntity.ok(asset);
    }

    @GetMapping("/asset/{id}")
    public ResponseEntity<AssetRespond> getAssetById(@PathVariable UUID id) {
        AssetRespond asset = storageService.getAssetById(id);
        return ResponseEntity.ok(asset);
    }

    @GetMapping("/asset/public/{publicId}")
    public ResponseEntity<AssetRespond> getAssetByPublicId(@PathVariable String publicId) {
        AssetRespond asset = storageService.getAssetByPublicId(publicId);
        return ResponseEntity.ok(asset);
    }

    @GetMapping("/playback-url/{publicId}")
    public ResponseEntity<String> getVideoPlaybackUrl(@PathVariable String publicId) {
        String playbackUrl = storageService.generateVideoPlaybackUrl(publicId);
        return ResponseEntity.ok(playbackUrl);
    }

    @GetMapping("/thumbnail-url/{publicId}")
    public ResponseEntity<String> getVideoThumbnailUrl(@PathVariable String publicId) {
        String thumbnailUrl = storageService.generateVideoThumbnailUrl(publicId);
        return ResponseEntity.ok(thumbnailUrl);
    }

    @DeleteMapping("/delete/{id}")
    @LogActivity(
            activityCode = ActivityTypeCode.ASSET_DELETE,
            description = "Delete an asset",
            targetType = SystemCategory.ASSET,
            targetIdExpression = "#id"
    )
    public ResponseEntity<Void> deleteAsset(@PathVariable UUID id) {
        storageService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/signature")
    @LogActivity(
            activityCode = ActivityTypeCode.ASSET_SIGNATURE,
            description = "Generate upload signature",
            targetType = SystemCategory.ASSET
//            targetIdExpression = "#result.body.id"
    )
    public ResponseEntity<Map<String, Object>> generateUploadSignature(@RequestBody Map<String, Object> paramsFromClient) {
        Map<String, Object> signature = storageService.generateUploadSignature(paramsFromClient);
        return ResponseEntity.ok(signature);
    }
}