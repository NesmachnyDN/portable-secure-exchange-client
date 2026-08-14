package io.github.nesmachnydn.secureexchange.service;

import org.springframework.stereotype.Component;

@Component
public class FileNameSanitizer {
    public String sanitize(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            throw new IllegalArgumentException("File name must not be blank");
        }
        String normalized = rawName.replace('\\', '/');
        String baseName = normalized.substring(normalized.lastIndexOf('/') + 1).trim();
        baseName = baseName.replaceAll("[\\p{Cntrl}:*?\"<>|]", "_");
        if (baseName.isBlank() || baseName.equals(".") || baseName.equals("..")) {
            throw new IllegalArgumentException("Invalid file name");
        }
        return baseName;
    }
}
