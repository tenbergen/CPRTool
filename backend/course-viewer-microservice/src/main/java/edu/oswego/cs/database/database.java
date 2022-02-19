package edu.oswego.cs.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class database {
    String database;

    @Produces
    public MongoClient createMongoClient(){
        String hostname = "127.0.0.1";
        int port = 27017;
        //Database name
        database = "cpr";
        //Database user name
        String user = "admin";
        //Database password for user
        String password = "Admin";

        MongoCredential credential = MongoCredential.createCredential(user,database,password.toCharArray());
        return new MongoClient(
                new ServerAddress(hostname,port),
                credential,
                new MongoClientOptions.Builder().build()
        );
    }

    @Produces
    public MongoDatabase getDB(MongoClient client){
        return client.getDatabase(database);
    }
}

