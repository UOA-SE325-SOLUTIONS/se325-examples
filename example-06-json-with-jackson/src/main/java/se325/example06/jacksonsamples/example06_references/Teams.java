package se325.example06.jacksonsamples.example06_references;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Teams {

    private Map<String, List<Employee>> teams = new HashMap<>();

    public Map<String, List<Employee>> getTeams() {
        return teams;
    }

    public void setTeams(Map<String, List<Employee>> teams) {
        this.teams = teams;
    }
}
