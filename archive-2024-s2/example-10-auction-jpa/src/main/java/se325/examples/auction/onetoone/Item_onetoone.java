package se325.examples.auction.onetoone;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Item_onetoone {

    @Id
    @GeneratedValue
    private Long id;

    private String name;


    protected Item_onetoone() {
    }

    public Item_onetoone(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
