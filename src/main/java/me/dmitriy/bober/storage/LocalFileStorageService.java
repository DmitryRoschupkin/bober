package me.dmitriy.bober.storage;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final String rootDir;

    public LocalFileStorageService(@Value("${app.upload.root-dir}") String rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public StoredFile store(MultipartFile file, String subfolder) throws IOException {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + (extension != null ? "." + extension : "");
        String relativePath = subfolder + "/" + storedName;

        try {
            Path target = resolve(relativePath);
            Files.createDirectories(target.getParent());
            file.transferTo(target);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not store file " + file.getOriginalFilename(), e);
        }

        return new StoredFile(relativePath, storedName, file.getSize());

    }

    @Override
    public String storeBytes(byte[] bytes, String subfolder, String extension) throws IOException {
        String storedName = UUID.randomUUID() + "." + extension;
        String relativePath = subfolder + "/" + storedName;

        try {
            Path target = resolve(relativePath);
            Files.createDirectories(target.getParent());
            Files.write(target, bytes);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not store book cover " + storedName, e);
        }

        return relativePath;
    }

    @Override
    public void delete(String storedPath) {
        try {
            Files.deleteIfExists(resolve(storedPath));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not delete file " + storedPath, e);
        }
    }

    @Override
    public Path resolve(String storedPath) {
        return Paths.get(rootDir, storedPath).normalize();
    }

    @Override
    public Resource loadAsResource(String storedPath) {
        try {
            Path file = resolve(storedPath);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not find file " + storedPath);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not build file URI " + storedPath, e);
        }
    }
}
