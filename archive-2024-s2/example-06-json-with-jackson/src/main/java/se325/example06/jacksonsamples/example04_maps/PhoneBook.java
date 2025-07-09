package se325.example06.jacksonsamples.example04_maps;

import java.util.Map;
import java.util.TreeMap;

public class PhoneBook {

    private Map<String, PhoneBookEntry> entries = new TreeMap<>();

    public Map<String, PhoneBookEntry> getEntries() {
        return entries;
    }

    public void setEntries(Map<String, PhoneBookEntry> entries) {
        this.entries = entries;
    }
}
