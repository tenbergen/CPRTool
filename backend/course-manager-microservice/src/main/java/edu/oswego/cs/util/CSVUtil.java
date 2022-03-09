package edu.oswego.cs.util;

import edu.oswego.cs.daos.StudentDAO;

import java.util.ArrayList;
import java.util.List;

public class CSVUtil {

    public static List<StudentDAO> parseStudentCSV(List<String> csvLines) {
        List<StudentDAO> students = new ArrayList<>();
        csvLines.forEach( line -> {
            String[] delimitedLine = line.split(",");
            if (delimitedLine.length < 10)
                try { throw new Exception("CSV file Is Not Formatted Correctly");}
                catch (Exception ignored) {}

            String fullname = delimitedLine[1] + ", " + delimitedLine[2] + " " + delimitedLine[3];
            String email = delimitedLine[7];
            students.add( new StudentDAO(fullname, email) );
        });
        return students;
    }


}
