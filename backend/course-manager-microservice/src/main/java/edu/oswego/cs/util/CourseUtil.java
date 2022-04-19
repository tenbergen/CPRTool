package edu.oswego.cs.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import org.bson.Document;

public class CourseUtil {

    public void collectionWipeOff(MongoCollection<Document> collection) {
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            collection.deleteOne(cursor.next());
        }
        cursor.close();
    }
}
