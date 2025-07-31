package se325.examples.example04springboot;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BufferedImageHttpMessageConverter extends AbstractHttpMessageConverter<BufferedImage> {

    public BufferedImageHttpMessageConverter() {
        super(MediaType.IMAGE_PNG);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return BufferedImage.class.isAssignableFrom(clazz);
    }

    @Override
    protected BufferedImage readInternal(Class<? extends BufferedImage> clazz, HttpInputMessage inputMessage)
            throws IOException {
        return ImageIO.read(inputMessage.getBody());
    }

    @Override
    protected void writeInternal(BufferedImage image, HttpOutputMessage outputMessage)
            throws IOException {
        ImageIO.write(image, "png", outputMessage.getBody());
    }
}
