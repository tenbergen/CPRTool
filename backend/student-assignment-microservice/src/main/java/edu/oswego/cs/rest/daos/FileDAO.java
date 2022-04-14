package edu.oswego.cs.rest.daos;
import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;

@Getter
@AllArgsConstructor
public class FileDAO {
    private String filename;
    private String courseID;
    private InputStream file;
    private int assignmentID;

    /**
     * Takes form-data from a POST request, converts it to an inputStream, and return the FileDOA containing
     * the files' information including the inputStream
     *
     * @param fileName   form-data String representation of file name
     * @param courseID   String
     * @param attachment form-data
     * @return FileDAO Instance
     * @throws IOException File Corruption Exception
     */
    public static FileDAO fileFactory(String fileName, String courseID, IAttachment attachment, int assignmentID) throws IOException {
        InputStream inputStream = attachment.getDataHandler().getInputStream();
        return new FileDAO(fileName, courseID, inputStream, assignmentID);
    }

    /**
     * Writes the inputStream to a file.
     */
    public void writeFile(String filePath) throws IOException {
        OutputStream outputStream = new FileOutputStream(filePath);
        outputStream.write(file.readAllBytes());
        outputStream.close();
    }
}
