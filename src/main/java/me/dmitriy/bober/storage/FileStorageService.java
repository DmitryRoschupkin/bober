package me.dmitriy.bober.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {

    StoredFile store(MultipartFile file, String subfolder) throws IOException;

    String storeBytes(byte[] bytes, String subfolder, String extension) throws IOException;

    void delete(String storedPath);

    java.nio.file.Path resolve(String storedPath);
}
