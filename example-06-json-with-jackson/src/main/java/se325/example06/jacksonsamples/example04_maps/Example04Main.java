package se325.example06.jacksonsamples.example04_maps;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Example04Main {

    public static void main(String[] args) throws IOException {

        PhoneBook phoneBook = new PhoneBook();
        phoneBook.getEntries().put("Alice", new PhoneBookEntry("021 123 4567", "123 Some Street"));
        phoneBook.getEntries().put("Bob", new PhoneBookEntry("021 987 6543", "456 Some Other Street"));

        System.out.println("Phone book entries class name: " + phoneBook.getEntries().getClass().getName());

        ObjectMapper mapper = new ObjectMapper();

        String phoneBookJson = mapper.writeValueAsString(phoneBook);

        System.out.println("Phone book json: " + phoneBookJson);

        PhoneBook deserialized = mapper.readValue(phoneBookJson, PhoneBook.class);
        System.out.println("Deserialized phone book entries class name: " + deserialized.getEntries().getClass().getName());

    }

}
