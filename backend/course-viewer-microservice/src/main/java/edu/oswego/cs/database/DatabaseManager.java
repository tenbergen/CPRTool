package edu.oswego.cs.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DatabaseManager {
    int studentPort = 27027; //Integer.parseInt(System.getenv("MONGO_PORT"));
    int professorPort = 27028;//Integer.parseInt(System.getenv("MONGO2_PORT"));
    int coursePort = 27029;//Integer.parseInt(System.getenv("MONGO3_PORT"));
    int assignmentPort = 27030;//Integer.parseInt(System.getenv("MONGO4_PORT"));
    int teamPort = 27031;//Integer.parseInt(System.getenv("MONGO5_PORT"));
    String hostname = "moxie.cs.oswego.edu";//System.getenv("MONGO_HOSTNAME");
    String mongoDatabase = "cpr";//System.getenv("MONGO_DATABASE");
    String mongoUser = "root"; //System.getenv("MONGO_USERNAME");
    String mongoPassword = "toor";//System.getenv("MONGO_PASSWORD");

    // Switch these for the above lines to use the test DB'S!
//    int studentPort = 27027;
//    int professorPort = 27028;
//    int coursePort = 27029;
//    int assignmentPort = 27030;
//    int teamPort = 27031;
//    String hostname = "moxie.cs.oswego.edu";
//    String mongoDatabase = "cpr";
//    String mongoUser = "root";
//    String mongoPassword = "toor";

    public MongoDatabase getStudentDB() {
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, studentPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDatabase);
    }

    public MongoDatabase getProfessorDB() {
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, professorPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDatabase);
    }

    public MongoDatabase getCourseDB() {
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, coursePort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDatabase);
    }

    public MongoDatabase getAssignmentDB() {
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, assignmentPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDatabase);
    }

    public MongoDatabase getTeamDB() {
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, teamPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDatabase);
    }

    public DatabaseManager() {
    }
}