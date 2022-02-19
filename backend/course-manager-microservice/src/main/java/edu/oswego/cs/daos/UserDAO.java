package edu.oswego.cs.daos;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class UserDAO{
    @Id
    private int ID;
    private String email;
    private String name;


    public UserDAO(int ID, String email, String name){
        this.email = email;
        this.name = name;
    }


}
