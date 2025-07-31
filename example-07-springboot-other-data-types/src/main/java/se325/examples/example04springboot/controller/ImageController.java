package se325.examples.example04springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@RequestMapping("/images")
public class ImageController {

    @GetMapping(value = "/{imageName}", produces = "image/png")
    public BufferedImage getImage(@PathVariable("imageName") String imageName) throws IOException {
        // Load the image from the classpath
        ClassLoader classLoader = getClass().getClassLoader();
        try (var resourceStream = classLoader.getResourceAsStream(imageName)) {
            if (resourceStream == null) {
                throw new ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Image not found: " + imageName);
            }
            // Read the image as a BufferedImage
            return javax.imageio.ImageIO.read(resourceStream);
        }
    }

}
