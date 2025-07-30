package se325.example06.jacksonsamples.example05_customserialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class BufferedImageSerializer extends StdSerializer<BufferedImage> {

    public BufferedImageSerializer() {
        this(null);
    }

    public BufferedImageSerializer(Class<BufferedImage> clazz) {
        super(clazz);
    }

    /**
     * Serializes a BufferedImage as a Base64 Sgtring
     *
     * @param bufferedImage the image to serialize
     * @param jsonGenerator the JSON generator to which the string will be written
     * @param serializerProvider
     * @throws IOException if anything goes wrong
     */
    @Override
    public void serialize(BufferedImage bufferedImage, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (bufferedImage == null) {
            jsonGenerator.writeNull();
            return;
        }
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            jsonGenerator.writeString(base64Image);
        }
    }
}
