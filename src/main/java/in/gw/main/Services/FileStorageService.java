package in.gw.main.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * FILE STORAGE SERVICE
 * ---------------------
 * Handles saving uploaded files (profile photos, query images) to disk.
 *
 * HOW IT WORKS:
 *   1. A file comes in from the HTML form (MultipartFile)
 *   2. We generate a UNIQUE file name (UUID) so two "photo.jpg" don't collide
 *   3. We save it to the "uploads/" folder inside the project directory
 *   4. We return the relative path like "uploads/profiles/abc-123.jpg"
 *   5. This path is stored in the database and used in <img> tags
 *
 * SUBFOLDER STRUCTURE:
 *   uploads/
 *     profiles/    → student profile photos
 *     queries/     → query/complaint images
 */
@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Save an uploaded file to a specific subfolder.
     *
     * @param file      the uploaded file from the form
     * @param subfolder e.g. "profiles" or "queries"
     * @return          relative path like "uploads/profiles/abc-123.jpg"
     */
    public String saveFile(MultipartFile file, String subfolder) {
        if (file == null || file.isEmpty()) {
            return null;  // No file uploaded, that's OK
        }

        try {
            // Create the directory if it doesn't exist
            // e.g., uploads/profiles/
            Path dirPath = Paths.get(uploadDir, subfolder);
            Files.createDirectories(dirPath);

            // Generate a unique file name to avoid collisions
            // e.g., "a1b2c3d4-photo.jpg"
            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            String uniqueName = UUID.randomUUID().toString() + extension;

            // Save the file to disk
            Path filePath = dirPath.resolve(uniqueName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return relative path (used in <img src="...">)
            return uploadDir + "/" + subfolder + "/" + uniqueName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a previously uploaded file.
     * Used when student re-uploads a photo.
     */
    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) return;
        try {
            Path filePath = Paths.get(relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log but don't crash - file might already be gone
            System.err.println("Could not delete file: " + relativePath);
        }
    }
}
