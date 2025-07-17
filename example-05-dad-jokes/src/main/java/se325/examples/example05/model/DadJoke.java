package se325.examples.example05.model;

public class DadJoke {

    private Long id;
    private String text;

    // Default constructor
    public DadJoke() {
    }

    // Constructor to set text
    public DadJoke(String text) {
        this.text = text;
    }

    // Constructor to set both id and text
    public DadJoke(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    // Getter for id
    public Long getId() {
        return id;
    }

    // Setter for id
    public void setId(Long id) {
        this.id = id;
    }

    // Getter for text
    public String getText() {
        return text;
    }

    // Setter for text
    public void setText(String text) {
        this.text = text;
    }
}
