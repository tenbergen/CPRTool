package edu.oswego.cs.daos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDAO {
    String user_id;
    String role;
    String first_name;
    String last_name;
}
