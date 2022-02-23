package edu.oswego.cs.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;

@ApplicationScoped
public class DatabaseManager {
    String hostname = "moxie.cs.oswego.edu";//System.getenv("MONGO_HOSTNAME");

    int ProfessorPort = 27027;//Integer.parseInt(System.getenv("MONGO_PORT"));
    String ProfessorDatabase = "cpr";//System.getenv("MONGO_DATABASE");
    String ProfessorUser = "root";//System.getenv("MONGO_USERNAME");
    String ProfessorPassword = "toor";//System.getenv("MONGO_PASSWORD");


    int Studentport = 27027;//Integer.parseInt(System.getenv("MONGO_PORT"));
    String Studentdatabase = "cpr";//System.getenv("MONGO_DATABASE");
    String Studentuser = "root";//System.getenv("MONGO_USERNAME");
    String Studentpassword = "toor";//System.getenv("MONGO_PASSWORD");
    public MongoDatabase getDB() {
        MongoCredential credentials = MongoCredential.createCredential(ProfessorUser, ProfessorDatabase, ProfessorPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, ProfessorPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(ProfessorDatabase);
    }

    public MongoDatabase getProfessorDB() {
        MongoCredential credentials = MongoCredential.createCredential(ProfessorUser, ProfessorDatabase, ProfessorPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, ProfessorPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(ProfessorDatabase);
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
   // public void close(@Disposes MongoClient toClose) {
   //     toClose.close();
   // }

    public DatabaseManager() {}
}
