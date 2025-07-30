package se325.example06.jacksonsamples.example05_customserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;

public class BufferedImageDeserializer extends StdDeserializer<BufferedImage> {

    public BufferedImageDeserializer() {
        this(null);
    }

    public BufferedImageDeserializer(Class<BufferedImage> clazz) {
        super(clazz);
    }

    /**
     * Reads in a JSON string, parses it as a Base64 string, and returns a BufferedImage
     * representation of that string.
     *
     * @param p         the JSON parser from which the string is read
     * @param ctxt
     * @return a BufferedImage
     * @throws IOException
     */
    @Override
    public BufferedImage deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String base64String = p.getValueAsString();
        if (base64String == null) return null;

        byte[] imageBytes = Base64.getDecoder().decode(base64String);
        try (java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(imageBytes)) {
            return javax.imageio.ImageIO.read(bis);
        }
    }
}
