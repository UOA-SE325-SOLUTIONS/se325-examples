package se325.example06.jacksonsamples.example04_maps;

public class PhoneBookEntry {

    private String phoneNumber;
    private String address;

    public PhoneBookEntry() {
    }

    public PhoneBookEntry(String phoneNumber, String address) {
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
