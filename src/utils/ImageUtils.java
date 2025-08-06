package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImageUtils {
    
    // Directorio donde se guardarán las imágenes
    private static final String IMAGES_DIRECTORY = "data/images/products";
    
    // Formatos de imagen permitidos
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
    
    static {
        // Crear el directorio de imágenes si no existe
        createImagesDirectory();
    }
    
    /**
     * Crea el directorio de imágenes si no existe
     */
    private static void createImagesDirectory() {
        try {
            Path path = Paths.get(IMAGES_DIRECTORY);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("Directorio de imágenes creado: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error al crear directorio de imágenes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Copia una imagen al directorio de productos y devuelve el nuevo nombre de archivo
     * @param sourceFile Archivo de imagen original
     * @param productId ID del producto
     * @return Nombre del archivo guardado, o null si hay error
     */
    public static String saveProductImage(File sourceFile, int productId) {
        if (sourceFile == null || !sourceFile.exists()) {
            System.err.println("Archivo de imagen no válido");
            return null;
        }
        
        // Validar extensión
        String fileName = sourceFile.getName().toLowerCase();
        boolean validExtension = false;
        for (String ext : ALLOWED_EXTENSIONS) {
            if (fileName.endsWith(ext)) {
                validExtension = true;
                break;
            }
        }
        
        if (!validExtension) {
            System.err.println("Formato de imagen no válido: " + fileName);
            return null;
        }
        
        try {
            // Generar nombre único para el archivo
            String extension = getFileExtension(fileName);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String newFileName = "product_" + productId + "_" + timestamp + extension;
            
            // Ruta de destino
            Path destinationPath = Paths.get(IMAGES_DIRECTORY, newFileName);
            
            // Copiar archivo
            Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("Imagen guardada: " + destinationPath.toAbsolutePath());
            return newFileName;
            
        } catch (IOException e) {
            System.err.println("Error al guardar imagen: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene la ruta completa de una imagen de producto
     * @param imageName Nombre del archivo de imagen
     * @return Ruta completa del archivo
     */
    public static String getImagePath(String imageName) {
        if (imageName == null || imageName.trim().isEmpty()) {
            return null;
        }
        
        Path imagePath = Paths.get(IMAGES_DIRECTORY, imageName);
        if (Files.exists(imagePath)) {
            return imagePath.toAbsolutePath().toString();
        }
        
        return null;
    }
    
    /**
     * Obtiene la URL de una imagen para cargar en ImageView
     * @param imageName Nombre del archivo de imagen
     * @return URL del archivo o null si no existe
     */
    public static String getImageURL(String imageName) {
        String path = getImagePath(imageName);
        if (path != null) {
            return new File(path).toURI().toString();
        }
        return null;
    }
    
    /**
     * Elimina una imagen de producto
     * @param imageName Nombre del archivo de imagen
     * @return true si se eliminó correctamente
     */
    public static boolean deleteProductImage(String imageName) {
        if (imageName == null || imageName.trim().isEmpty()) {
            return false;
        }
        
        try {
            Path imagePath = Paths.get(IMAGES_DIRECTORY, imageName);
            if (Files.exists(imagePath)) {
                Files.delete(imagePath);
                System.out.println("Imagen eliminada: " + imagePath.toAbsolutePath());
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error al eliminar imagen: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Obtiene la extensión de un archivo
     * @param fileName Nombre del archivo
     * @return Extensión del archivo con punto incluido
     */
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }
    
    /**
     * Verifica si existe una imagen para un producto
     * @param imageName Nombre del archivo de imagen
     * @return true si existe la imagen
     */
    public static boolean imageExists(String imageName) {
        if (imageName == null || imageName.trim().isEmpty()) {
            return false;
        }
        
        Path imagePath = Paths.get(IMAGES_DIRECTORY, imageName);
        return Files.exists(imagePath);
    }
}