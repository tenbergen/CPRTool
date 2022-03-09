package edu.oswego.cs.util;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import edu.oswego.cs.daos.StudentDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public static String getModifiedFileName(IAttachment attachment) throws IOException {
        String modifiedFileName = "";
        InputStream inStream = attachment.getDataHandler().getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        String line = "";
        try {
            while ((line = reader.readLine()) != null) { modifiedFileName = line; }
            reader.close();
        } catch (IOException ignored) {}
        return modifiedFileName;
    }


}
