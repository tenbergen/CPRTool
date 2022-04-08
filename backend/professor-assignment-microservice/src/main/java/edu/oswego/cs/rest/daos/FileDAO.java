package edu.oswego.cs.rest.daos;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Id;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@NoArgsConstructor
public class FileDAO {
    @Id @JsonbProperty public String filename;
    @JsonbProperty public int assignmentID;
    @JsonbProperty public String courseID;
    @JsonbProperty public InputStream file;

    @JsonbCreator
    public FileDAO(
            @NonNull @JsonbProperty("file_name") String filename,
            @NonNull @JsonbProperty("assignment_id") int assignmentID,
            @NonNull @JsonbProperty("course_id") String courseID,
            @NonNull @JsonbProperty("inputStream") InputStream file) {
        this.filename = filename;
        this.assignmentID = assignmentID;
        this.courseID = courseID;
        this.file = file;
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
        InputStream inputStream = attachment.getDataHandler().getInputStream();
        return new FileDAO(fileName, assignmentID, courseID, inputStream);
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