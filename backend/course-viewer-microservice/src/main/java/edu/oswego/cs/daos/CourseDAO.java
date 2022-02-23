package edu.oswego.cs.daos;

import lombok.*;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Getter
public class CourseDAO {

    @Id
    public String courseID;
    public @JsonbProperty("courseName") @NonNull String courseName;
    public @JsonbProperty("courseSection") @NonNull int courseSection;
    public @JsonbProperty("CRN") @NonNull String CRN;
    public @JsonbProperty("semester") @NonNull String semester;
    public @JsonbProperty("abbreviation") @NonNull String abbreviation;

    @JsonbCreator
    public CourseDAO(@JsonbProperty("courseName") String courseName,
                     @JsonbProperty("courseSection") int courseSection,
                     @JsonbProperty("CRN") String CRN,
                     @JsonbProperty("semester") String semester
                     ) {
        this.courseName = courseName;
        this.courseSection = courseSection;
    }

    public String toString() {
        return String.format("{\"courseName\":\"%s\",\"courseSection\":%d}", courseName, courseSection);
    }
}