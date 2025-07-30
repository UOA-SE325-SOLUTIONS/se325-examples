package se325.example06.jacksonsamples.example05_customserialization;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.awt.image.BufferedImage;

public class Pokemon {
    private int dexNumber;
    private String name;

    @JsonSerialize(using = BufferedImageSerializer.class)
    @JsonDeserialize(using = BufferedImageDeserializer.class)
    private BufferedImage image;

    public Pokemon(){}

    public Pokemon(int dexNumber, String name, BufferedImage image) {
        this.dexNumber = dexNumber;
        this.name = name;
        this.image = image;
    }

    public int getDexNumber() {
        return dexNumber;
    }

    public void setDexNumber(int dexNumber) {
        this.dexNumber = dexNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
