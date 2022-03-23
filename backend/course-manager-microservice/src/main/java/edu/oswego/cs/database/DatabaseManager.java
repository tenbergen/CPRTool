package edu.oswego.cs.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DatabaseManager {
    String hostname = "moxie.cs.oswego.edu";//System.getenv("MONGO_HOSTNAME");

    int studentPort = 27027;//Integer.parseInt(System.getenv("MONGO_PORT"));
    String studentDatabase = "cpr";//System.getenv("MONGO_DATABASE");
    String studentUser = "root";//System.getenv("MONGO_USERNAME");
    String studentPassword = "toor";//System.getenv("MONGO_PASSWORD");

    int professorPort = 27028;//Integer.parseInt(System.getenv("MONGO2_PORT"));
    String professorDatabase = "cpr";//System.getenv("MONGO_DATABASE");
    String professorUser = "root";//System.getenv("MONGO_USERNAME");
    String professorPassword = "toor";//System.getenv("MONGO_PASSWORD");

    int coursePort = 27029;//Integer.parseInt(System.getenv("MONGO3_PORT"));
    String courseDatabase = "cpr";//System.getenv("MONGO_DATABASE");
    String courseUser = "root";//System.getenv("MONGO_USERNAME");
    String coursePassword = "toor";//System.getenv("MONGO_PASSWORD");

    int teamPort = 27030;//Integer.parseInt(System.getenv("MONGO4_PORT"));
    String teamDatabase = "cpr";//System.getenv("MONGO_DATABASE");
    String teamUser = "root";//System.getenv("MONGO_USERNAME");
    String teamPassword = "toor";//System.getenv("MONGO_PASSWORD");

    public MongoDatabase getStudentDB() {
        MongoCredential credentials = MongoCredential.createCredential(studentUser, studentDatabase, studentPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, studentPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(studentDatabase);
    }

    public MongoDatabase getProfessorDB() {
        MongoCredential credentials = MongoCredential.createCredential(professorUser, professorDatabase, professorPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, professorPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(professorDatabase);
    }

    public MongoDatabase getCourseDB() {
        MongoCredential credentials = MongoCredential.createCredential(courseUser, courseDatabase, coursePassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, coursePort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(courseDatabase);
    }

    public MongoDatabase getTeamDB() {
        MongoCredential credentials = MongoCredential.createCredential(teamUser, teamDatabase, teamPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, teamPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(teamDatabase);
    }

    public DatabaseManager() {
    }
}
