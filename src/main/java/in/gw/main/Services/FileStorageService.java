package in.gw.main.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * FILE STORAGE SERVICE
 * ---------------------
 * Handles saving uploaded files (profile photos, query photos) to disk.
 *
 * Files are stored in:
 *   uploads/profiles/   → student profile photos
 *   uploads/queries/    → query attachment photos
 *
 * Each file gets a unique name (UUID) to prevent collisions.
 * Returns the relative path (e.g. "profiles/abc-123.jpg") that is stored in DB.
 */
@Service
public class FileStorageService {

    private final Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

    /**
     * Save a profile photo. Returns the relative path stored in DB.
     * @param file the uploaded file
     * @return relative path like "profiles/uuid-filename.jpg"
     */
    public String saveProfilePhoto(MultipartFile file) throws IOException {
        return saveFile(file, "profiles");
    }

    /**
     * Save a query photo. Returns the relative path stored in DB.
     * @param file the uploaded file
     * @return relative path like "queries/uuid-filename.jpg"
     */
    public String saveQueryPhoto(MultipartFile file) throws IOException {
        return saveFile(file, "queries");
    }

    /**
     * Generic file save method.
     * Creates subdirectory if needed, generates unique filename, copies file.
     */
    private String saveFile(MultipartFile file, String subDir) throws IOException {
        Path targetDir = uploadDir.resolve(subDir);
        Files.createDirectories(targetDir);

        // Generate unique filename: UUID + original extension
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String uniqueName = UUID.randomUUID().toString() + extension;

        Path targetPath = targetDir.resolve(uniqueName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Return relative path (used in DB and URL)
        return subDir + "/" + uniqueName;
    }
}
