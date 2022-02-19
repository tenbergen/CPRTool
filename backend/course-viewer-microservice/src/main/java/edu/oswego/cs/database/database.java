package edu.oswego.cs.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class Database {

    String hostname = System.getenv("MONGO_HOSTNAME");
    int port = Integer.parseInt(System.getenv("MONGO_PORT"));
    String database = System.getenv("MONGO_DATABASE");
    String user = System.getenv("MONGO_USERNAME");
    String password = System.getenv("MONGO_PASSWORD");

    @Produces
    public MongoClient createMongoClient() {
        MongoCredential credentials = MongoCredential.createCredential(user, database, password.toCharArray());
        return new MongoClient(
                new ServerAddress(hostname, port),
                credentials,
                new MongoClientOptions.Builder().build()
        );
    }

    @Produces
    public MongoDatabase getDB(MongoClient client) {
        return client.getDatabase(database);
    }

    public void close(@Disposes MongoClient toClose) {
        toClose.close();
    }
}