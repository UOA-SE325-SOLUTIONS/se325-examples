package se325.examples.example04springboot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.awt.*;
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

    @GetMapping(value = "/placeholder", produces = "image/png")
    public BufferedImage getPlaceholderImage(@RequestParam(value = "width", defaultValue = "32") int width,
                                             @RequestParam(value = "height", defaultValue = "32") int height,
                                             @RequestParam(value = "color", defaultValue = "255,255,255") String color,
                                             @RequestParam(value = "background-color", defaultValue = "20,20,20") String backgroundColor,
                                             @RequestParam(value = "text", required = false) String text) throws IOException {

        if (width <= 0 || height <= 0) throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Width and height must be greater than 0");

        // Set background color
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(parseColor(backgroundColor));
        g.fillRect(0, 0, width, height);

        // Draw placeholder text
        if (text == null) text = "Placeholder " + width + " x " + height;
        g.setColor(parseColor(color));
        int fontSize = 24;
        Font font = new Font("Arial", Font.BOLD, fontSize);
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics(font);

        while ((metrics.stringWidth(text) > width || metrics.getHeight() > height) && fontSize > 1) {
            fontSize--;
            font = new Font("Arial", Font.BOLD, fontSize);
            g.setFont(font);
            metrics = g.getFontMetrics(font);
        }

        g.drawString(text, (width - metrics.stringWidth(text)) / 2, (height - metrics.getHeight()) / 2 + metrics.getAscent());

        return image;
    }

    private Color parseColor(String color) {
        // Parse the color string "r,g,b"
        String[] colorParts = color.split(",");
        if (colorParts.length != 3)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Background color format must be 'r,g,b'");

        try {
            int r = Integer.parseInt(colorParts[0]);
            int g = Integer.parseInt(colorParts[1]);
            int b = Integer.parseInt(colorParts[2]);

            // Validate color components to be within 0-255 range
            if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Background color values must be in the range 0-255");

            // Return the parsed color
            return new Color(r, g, b);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Background color values must be valid integers");
        }
    }

}
