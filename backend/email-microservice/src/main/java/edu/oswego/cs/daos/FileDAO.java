package edu.oswego.cs.daos;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Id;
import java.io.IOException;
import java.io.InputStream;

@NoArgsConstructor
public class FileDAO {
    @Id @JsonbProperty public String fileName;
    @JsonbProperty public int assignmentID;
    @JsonbProperty public String courseID;
    @JsonbProperty public InputStream file;

    @JsonbCreator
    public FileDAO(
            @NonNull @JsonbProperty("file_name") String fileName,
            @NonNull @JsonbProperty("assignment_id") int assignmentID,
            @NonNull @JsonbProperty("course_id") String courseID) {
        this.fileName = fileName;
        this.assignmentID = assignmentID;
        this.courseID = courseID;
    }

    public FileDAO(
            @NonNull @JsonbProperty("file_name") String fileName,
            @NonNull @JsonbProperty("assignment_id") int assignmentID,
            @NonNull @JsonbProperty("course_id") String courseID,
            @NonNull @JsonbProperty("inputStream") InputStream file) {
        this.fileName = fileName;
        this.assignmentID = assignmentID;
        this.courseID = courseID;
        this.file = file;
    }
}