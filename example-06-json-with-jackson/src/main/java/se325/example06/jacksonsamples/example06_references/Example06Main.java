package se325.example06.jacksonsamples.example06_references;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;

public class Example06Main {

    public static void main(String[] args) throws IOException {

        Employee alice = new Employee(1, "Alice");
        Employee bob = new Employee(2, "Bob");
        Employee caitlin = new Employee(3, "Caitlin");
        Employee dave = new Employee(4, "Dave");

        Teams teams = new Teams();

        teams.getTeams().put("Project 1", Arrays.asList(alice, bob));
        teams.getTeams().put("Project 2", Arrays.asList(bob, caitlin, dave));

        ObjectMapper mapper = new ObjectMapper();
        String teamsJson = mapper.writeValueAsString(teams);
        System.out.println("Teams json: " + teamsJson);

        Teams deserializedTeams = mapper.readValue(teamsJson, Teams.class);
        System.out.println("Teams deserialized");
        Employee firstBob = deserializedTeams.getTeams().get("Project 1").get(1);
        Employee secondBob = deserializedTeams.getTeams().get("Project 2").get(0);
        if (firstBob == secondBob) {
            System.out.println("Bobs are the same object!");
        } else {
            System.out.println("Bobs are NOT the same object!");
        }

        Manager theBoss = new Manager(5, "The Boss", alice, bob);
        String bossJson = mapper.writeValueAsString(theBoss);
        System.out.println("Boss json: " + bossJson);

        Manager deserializedBoss = mapper.readValue(bossJson, Manager.class);
        System.out.println("The boss deserialized");

    }

}
