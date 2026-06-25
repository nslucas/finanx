package com.example.prospera.Resources;

import com.example.prospera.DTO.UserPreferenceRecord;
import com.example.prospera.Services.UserPreferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
public class MeResource {
    private final UserPreferenceService preferenceService;

    public MeResource(UserPreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @GetMapping("/preferences")
    public ResponseEntity<UserPreferenceRecord> preferences() {
        return ResponseEntity.ok(preferenceService.findAuthenticatedUserPreferences());
    }

    @PutMapping("/preferences")
    public ResponseEntity<UserPreferenceRecord> updatePreferences(@RequestBody UserPreferenceRecord record) {
        return ResponseEntity.ok(preferenceService.updateAuthenticatedUserPreferences(record));
    }
}
