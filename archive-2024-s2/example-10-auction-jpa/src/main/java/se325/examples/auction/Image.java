package se325.examples.auction;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

/**
 * Embeddable class to represent images. Image instances form part of the state
 * of auctionable Items.
 */
@Embeddable
public class Image {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String filename;

    private int sizeX;
    private int sizeY;

    // Required by JPA.
    protected Image() {
    }

    public Image(String title, String filename, int sizeX, int sizeY) {
        this.title = title;
        this.filename = filename;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public String getTitle() {
        return title;
    }

    public String getFilename() {
        return filename;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return sizeX == image.sizeX &&
                sizeY == image.sizeY &&
                Objects.equals(title, image.title) &&
                Objects.equals(filename, image.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, filename, sizeX, sizeY);
    }
}
