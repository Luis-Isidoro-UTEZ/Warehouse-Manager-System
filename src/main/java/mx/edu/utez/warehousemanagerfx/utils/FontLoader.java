package mx.edu.utez.warehousemanagerfx.utils;

import javafx.scene.text.Font;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FontLoader {
    private static final Logger LOGGER = Logger.getLogger(FontLoader.class.getName());

    /**
     * Carga todas las fuentes Albert Sans disponibles en el proyecto
     */
    public static void loadAlbertSansFonts() {
        String[] fontFiles = {
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-Black.ttf",
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-BlackItalic.ttf",
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-Bold.ttf",
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-BoldItalic.ttf",
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-ExtraBold.ttf",
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-ExtraBoldItalic.ttf",
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-ExtraLight.ttf",
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-ExtraLightItalic.ttf",
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-Italic.ttf",
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-Light.ttf",
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-LightItalic.ttf",
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-Medium.ttf",
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-MediumItalic.ttf",
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-Regular.ttf",
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-SemiBold.ttf",
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-SemiBoldItalic.ttf",
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-Thin.ttf",
                "/mx/edu/utez/warehousemanagerfx/fonts/AlbertSans-ThinItalic.ttf"
        };

        for (String fontPath : fontFiles) {
            loadFont(fontPath);
        }

        // Verificar que Albert Sans está disponible
        if (Font.getFamilies().contains("Albert Sans")) {
            LOGGER.info("Albert Sans font family loaded successfully");
        } else {
            LOGGER.warning("Albert Sans font family not found after loading");
        }
    }

    /**
     * Carga una fuente específica
     */
    private static void loadFont(String fontPath) {
        try (InputStream fontStream = FontLoader.class.getResourceAsStream(fontPath)) {
            if (fontStream != null) {
                Font font = Font.loadFont(fontStream, 12); // Tamaño por defecto
                if (font != null) {
                    LOGGER.info("Font loaded successfully: " + fontPath);
                } else {
                    LOGGER.warning("Failed to load font: " + fontPath);
                }
            } else {
                LOGGER.warning("Font file not found: " + fontPath);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading font: " + fontPath, e);
        }
    }

    /**
     * Verifica si Albert Sans está disponible
     */
    public static boolean isAlbertSansAvailable() {
        return Font.getFamilies().contains("Albert Sans");
    }

    /**
     * Lista todas las fuentes disponibles (para debugging)
     */
    public static void listAvailableFonts() {
        LOGGER.info("Available font families:");
        Font.getFamilies().forEach(family -> LOGGER.info("- " + family));
    }
}