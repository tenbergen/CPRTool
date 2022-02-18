package edu.oswego.cs.daos;

import lombok.*;

import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Getter @ToString
public class CourseDAO {

    @Id
    private @JsonbProperty("id") @ToString.Exclude int ID;
    private @NonNull String courseName;
    private @NonNull int courseSection;
    private @ToString.Exclude String professor;

}
