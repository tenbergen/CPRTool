package edu.oswego.cs.rest.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DatabaseManager {
<<<<<<< HEAD
    int studentPort = Integer.parseInt(System.getenv("MONGO_PORT"));
    int professorPort = Integer.parseInt(System.getenv("MONGO2_PORT"));
    int coursePort = Integer.parseInt(System.getenv("MONGO3_PORT"));
    int assignmentPort = Integer.parseInt(System.getenv("MONGO4_PORT"));
    int teamPort = Integer.parseInt(System.getenv("MONGO5_PORT"));
    String hostname = System.getenv("MONGO_HOSTNAME");
    String mongoDatabase = System.getenv("MONGO_DATABASE");
    String mongoUser = System.getenv("MONGO_USERNAME");
    String mongoPassword = System.getenv("MONGO_PASSWORD");

=======
    String hostname = "moxie.cs.oswego.edu";//System.getenv("MONGO_HOSTNAME");

    int studentPort = 27030; //Integer.parseInt(System.getenv("MONGO_PORT"));
    String studentDatabase = "cpr";//System.getenv("MONGO_DATABASE");
    String studentUser = "root";//System.getenv("MONGO_USERNAME");
    String studentPassword = "toor"; //System.getenv("MONGO_PASSWORD");

    int assPort = 27030;//Integer.parseInt(System.getenv("MONGO2_PORT"));
    String assDatabase = "cpr";//System.getenv("MONGO_DATABASE");
    String assUser = "root"; //System.getenv("MONGO_USERNAME");
    String assPassword = "toor"; //System.getenv("MONGO_PASSWORD");
>>>>>>> aeb0baef353cade318324f01b959bccb10811cbf

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