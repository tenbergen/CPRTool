package edu.oswego.cs.rest.daos;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Id;
import java.io.*;

@Getter
@NoArgsConstructor
//@AllArgsConstructor
public class FileDAO {
    @Id
    @JsonbProperty private String filename;
    @JsonbProperty private String courseID;
    @JsonbProperty private InputStream file;
    @JsonbProperty private int assignmentID;

    public FileDAO(
            @NonNull @JsonbProperty("file_name") String filename,
            @NonNull @JsonbProperty("course_id") String courseID,
            @NonNull @JsonbProperty("inputStream") InputStream file,
            @NonNull @JsonbProperty("assignment_id") int assignmentID) {
        this.filename = filename;
        this.courseID = courseID;
        this.file = file;
        this.assignmentID = assignmentID;
    }

    @JsonbCreator
    public FileDAO(
            @NonNull @JsonbProperty("file_name") String filename,
            @NonNull @JsonbProperty("course_id") String courseID,
            @NonNull @JsonbProperty("assignment_id") int assignmentID) {
        this.filename = filename;
        this.courseID = courseID;
        this.assignmentID = assignmentID;
    }

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
//        String courseName = fileName.split("\\.")[0];
        InputStream inputStream = attachment.getDataHandler().getInputStream();
        System.out.println("fileName: " + fileName + "courseID: " + courseID);
        return new FileDAO(fileName, courseID, inputStream, assignmentID);
    }

    /**
     * Writes the inputStream to a file.
     */
    public void writeFile(String filePath) throws IOException {
        OutputStream outputStream = new FileOutputStream(new File(filePath));
        outputStream.write(file.readAllBytes());
        outputStream.close();
    }
}
