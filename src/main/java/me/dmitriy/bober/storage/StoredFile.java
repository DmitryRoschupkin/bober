package me.dmitriy.bober.storage;

public record StoredFile(String storedPath, String originalFileName, long sizeBytes) {
}
