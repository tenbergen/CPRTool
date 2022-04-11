package edu.oswego.cs.distribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AssignmentDistribution {

    public static HashMap<String, List<String>> distribute(List<String> teams, int assignmentsPerTeam) throws Exception {
        if (assignmentsPerTeam >= teams.size())
            throw new IndexOutOfBoundsException();

        HashMap<String, List<String>> teamAssignments = new HashMap<>();

        for (int teamIndex = 0; teamIndex < teams.size(); teamIndex++) {
            String team = teams.get(teamIndex);
            teamAssignments.put(team, new ArrayList<>());

            for (int assignedTeamCount = 1; assignedTeamCount <= assignmentsPerTeam; assignedTeamCount++) {
                int assignedTeamIndex = teamIndex + assignedTeamCount;

                if (assignedTeamCount + teamIndex >= teams.size())
                    assignedTeamIndex = assignedTeamIndex - teams.size();

                String assignedTeam = teams.get(assignedTeamIndex);
                teamAssignments.get(team).add(assignedTeam);
            }
        }
        return teamAssignments;
    }

}
