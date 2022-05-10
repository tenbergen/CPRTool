package edu.oswego.cs.util;

import edu.oswego.cs.daos.StudentDAO;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {

    public static List<StudentDAO> parseStudentCSV(List<String> csvLines) {
        List<StudentDAO> students = new ArrayList<>();
        csvLines.forEach(line -> {
            String[] delimitedLine = line.split(",");
            if (delimitedLine.length < 10) throw new CPRException(Response.Status.CONFLICT, "CSV file is not formatted correctly.");
            String fullName = delimitedLine[1] + ", " + delimitedLine[2] + " " + delimitedLine[3];
            String email = delimitedLine[7];
            students.add(new StudentDAO(fullName, email));
        });
        return students;
    }
}
