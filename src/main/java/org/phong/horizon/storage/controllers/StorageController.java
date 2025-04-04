package org.phong.horizon.storage.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.storage.dtos.AssetRespond;
import org.phong.horizon.storage.dtos.UploadCompleteRequest;
import org.phong.horizon.storage.infrastructure.persistence.entities.Asset;
import org.phong.horizon.storage.service.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/storage")
@AllArgsConstructor
public class StorageController {
    private final StorageService storageService;

    @PostMapping("/create")
    public ResponseEntity<AssetRespond> createAsset(@RequestBody UploadCompleteRequest uploadData) {
        AssetRespond asset = storageService.createAsset(uploadData);
        return ResponseEntity.ok(asset);
    }

    @GetMapping("/asset/{id}")
    public ResponseEntity<Asset> getAssetById(@PathVariable UUID id) {
        Optional<Asset> asset = storageService.getAssetById(id);
        return asset.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/asset/public/{publicId}")
    public ResponseEntity<Asset> getAssetByPublicId(@PathVariable String publicId) {
        Optional<Asset> asset = storageService.getAssetByPublicId(publicId);
        return asset.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable UUID id) {
        storageService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/signature")
    public ResponseEntity<Map<String, Object>> generateUploadSignature(@RequestBody Map<String, Object> paramsFromClient) {
        Map<String, Object> signature = storageService.generateUploadSignature(paramsFromClient);
        return ResponseEntity.ok(signature);
    }
}