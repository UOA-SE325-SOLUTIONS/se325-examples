package se325.example06.jacksonsamples.example05_customserialization;

import java.time.LocalDate;
import java.util.Objects;

public class Movie {

    private String title;
    private LocalDate releaseDate;

    public Movie() {}

    public Movie(String title, LocalDate pubDate) {
        this.title = title;
        this.releaseDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie book = (Movie) o;
        return Objects.equals(title, book.title) &&
                Objects.equals(releaseDate, book.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, releaseDate);
    }
}