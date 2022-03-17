package edu.oswego.cs.rest.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DatabaseManager {
    String hostname = "moxie.cs.oswego.edu";//System.getenv("MONGO_HOSTNAME");

    int TeamPort = 27029;//Integer.parseInt(System.getenv("MONGO_PORT"));
    String TeamDatabase = "cpr";//System.getenv("MONGO_DATABASE");
    String TeamUser = "root";//System.getenv("MONGO_USERNAME");
    String TeamPassword = "toor";//System.getenv("MONGO_PASSWORD");


    int Studentport = 27027;//Integer.parseInt(System.getenv("MONGO_PORT"));
    String Studentdatabase = "cpr";//System.getenv("MONGO_DATABASE");
    String Studentuser = "root";//System.getenv("MONGO_USERNAME");
    String Studentpassword = "toor";//System.getenv("MONGO_PASSWORD");

    public MongoDatabase getDB() {
        MongoCredential credentials = MongoCredential.createCredential(TeamUser, TeamDatabase, TeamPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, TeamPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(TeamDatabase);
    }

    public MongoDatabase getTeamDB() {
        MongoCredential credentials = MongoCredential.createCredential(TeamUser, TeamDatabase, TeamPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, TeamPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(TeamDatabase);
    }
    public MongoDatabase getStudentDB(){
        MongoCredential credentials = MongoCredential.createCredential(Studentuser, Studentdatabase, Studentpassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, Studentport),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(Studentdatabase);
    }

    public DatabaseManager() {}
}
