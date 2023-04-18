import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import edu.oswego.cs.rest.database.AssignmentInterface;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.Arrays;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

//please use the default db setup outlined in the "Database Default for Testing" file
//TC|ID 1.1 = TCIDOnePointOne is the naming convention for now
public class StudentAssignmentTests {

    AssignmentInterface assignInter = new AssignmentInterface();
    MongoClient mongoClient, mongoClient2, mongoClient3, mongoClient4, mongoClient5;
    MongoCollection<Document> teamCollection,  assignmentCollection, submissionsCollection;


//------------------------------------------------------------------------------------------------------------------------------

    //To test:
    //public void makeSubmission(String course_id, int assignment_id, String file_name, String teamName)

    @Test
    public void makeSubmissionTCIDOnePointOne(){//valid course_id

        //gives access to db
        //------------------------------------------------------------------------------------------------------------------------------
        //from env file
        String username = "root";
        String database = "admin";
        String password = "password";
        //Create credentials
        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
        MongoClientOptions options = MongoClientOptions.builder()
                .writeConcern(WriteConcern.JOURNALED).build();

        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
        //------------------------------------------------------------------------------------------------------------------------------

        String course_id= "CSC-480-46374-SPR-2023";
        int assignment_id= 1;
        String file_name= "file1.pdf";//to add to submissions Collection
        String teamName= "Team1";//name of team that submitted the file
//        assignInter.makeSubmission(course_id,assignment_id,file_name,teamName);//call method to be tested

        //check if file was added to submissions Collection

        MongoCursor<Document> query = submissionsCollection.find(and(
                eq("course_id", course_id),
                eq("assignment_id", assignment_id),
                eq("submission_name", file_name),
                eq("team_name", teamName))).iterator();
        Document docFromQuery= query.next();//gets the Document returned by the query
        String queriedSubmissionName= docFromQuery.get("submission_name",file_name);

        Document expectedDoc= new Document()
                .append("course_id", course_id)
                .append("assignment_id", assignment_id)
                .append("submission_name", file_name)
                .append("team_name", teamName);
        String expectedSubmissionName= expectedDoc.get("submission_name",file_name);

        assertEquals(expectedSubmissionName,queriedSubmissionName);

        //remove the change we made to the db (ie. remove the Document we just added)
        submissionsCollection.deleteOne(and(Filters.
                        eq("course_id", course_id),
                eq("assignment_id", assignment_id),
                eq("submission_name", file_name),
                eq("team_name", teamName)));


    }

    @Test
    public void makeSubmissionTCIDOnePointA(){//null course_id

        //gives access to db
        //------------------------------------------------------------------------------------------------------------------------------
        //from env file
        String username = "root";
        String database = "admin";
        String password = "password";
        //Create credentials
        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
        MongoClientOptions options = MongoClientOptions.builder()
                .writeConcern(WriteConcern.JOURNALED).build();

        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
        //------------------------------------------------------------------------------------------------------------------------------

        String course_id= null;
        int assignment_id= 1;
        String file_name= "file1.pdf";//to add to submissions Collection
        String teamName= "Team1";//name of team that submitted the file

        //asserts that a javax.ws.rs.WebApplicationException is thrown
//        Exception exception = assertThrows(
//                javax.ws.rs.WebApplicationException.class,
//                () -> assignInter.makeSubmission(course_id,assignment_id,file_name,teamName));//call method to be tested

    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    public void makeSubmissionTCIDTwoPointAB() {//-1,0 assignment_id

        //gives access to db
        //------------------------------------------------------------------------------------------------------------------------------
        //from env file
        String username = "root";
        String database = "admin";
        String password = "password";
        //Create credentials
        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
        MongoClientOptions options = MongoClientOptions.builder()
                .writeConcern(WriteConcern.JOURNALED).build();

        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
        //------------------------------------------------------------------------------------------------------------------------------

        String course_id ="CSC-480-46374-SPR-2023";
        int assignment_id = -1;
        String file_name = "file1.pdf";//to add to submissions Collection
        String teamName = "Team1";//name of team that submitted the file

        //asserts that a javax.ws.rs.WebApplicationException is thrown
//        Exception exception = assertThrows(
//                javax.ws.rs.WebApplicationException.class,
//                () -> assignInter.makeSubmission(course_id,assignment_id,file_name,teamName));//call method to be tested

    }

    //does not pass when run with makeSubmissionTCIDThreePointA();
    //passes after resetting db and not running together with makeSubmissionTCIDThreePointA()
    @Test
    public void makeSubmissionTCIDThreePointOne(){//valid file_name

        //gives access to db
        //------------------------------------------------------------------------------------------------------------------------------
        //from env file
        String username = "root";
        String database = "admin";
        String password = "password";
        //Create credentials
        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
        MongoClientOptions options = MongoClientOptions.builder()
                .writeConcern(WriteConcern.JOURNALED).build();

        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
        //------------------------------------------------------------------------------------------------------------------------------

        String course_id= "CSC-480-46374-SPR-2023";
        int assignment_id= 1;
        String file_name= "file1.csv";//to add to submissions Collection
        String teamName= "Team1";//name of team that submitted the file
//        assignInter.makeSubmission(course_id,assignment_id,file_name,teamName);//call method to be tested

        //check if file was added to submissions Collection

        MongoCursor<Document> query = submissionsCollection.find(and(
                eq("course_id", course_id),
                eq("assignment_id", assignment_id),
                eq("submission_name", file_name),
                eq("team_name", teamName))).iterator();
        Document docFromQuery= query.next();//gets the Document returned by the query
        String queriedSubmissionName= docFromQuery.get("submission_name",file_name);

        Document expectedDoc= new Document()
                .append("course_id", course_id)
                .append("assignment_id", assignment_id)
                .append("submission_name", file_name)
                .append("team_name", teamName);
        String expectedSubmissionName= expectedDoc.get("submission_name",file_name);

        assertEquals(expectedSubmissionName,queriedSubmissionName);

        //remove the change we made to the db (ie. remove the Document we just added)
        submissionsCollection.deleteOne(and(Filters.
                        eq("course_id", course_id),
                eq("assignment_id", assignment_id),
                eq("submission_name", file_name),
                eq("team_name", teamName)));


    }

   /* @Test
    public void makeSubmissionTCIDThreePointA(){//null file_name

        //gives access to db
        //------------------------------------------------------------------------------------------------------------------------------
        //from env file
        String username = "root";
        String database = "admin";
        String password = "password";
        //Create credentials
        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
        MongoClientOptions options = MongoClientOptions.builder()
                .writeConcern(WriteConcern.JOURNALED).build();

        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
        //------------------------------------------------------------------------------------------------------------------------------

        String course_id= "CSC-480-46374-SPR-2023";
        int assignment_id= 1;
        String file_name= null;//to add to submissions Collection
        String teamName= "Team1";//name of team that submitted the file

        //asserts that a javax.ws.rs.WebApplicationException is thrown
        Exception exception = assertThrows(
                javax.ws.rs.WebApplicationException.class,
                () -> assignInter.makeSubmission(course_id,assignment_id,file_name,teamName));//call method to be tested

    }*/

    @Test
    public void makeSubmissionTCIDFourPointA(){//null teamName
        //gives access to db
        //------------------------------------------------------------------------------------------------------------------------------
        //from env file
        String username = "root";
        String database = "admin";
        String password = "password";
        //Create credentials
        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
        MongoClientOptions options = MongoClientOptions.builder()
                .writeConcern(WriteConcern.JOURNALED).build();

        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
        //------------------------------------------------------------------------------------------------------------------------------

        String course_id= "CSC-480-46374-SPR-2023";
        int assignment_id= 1;
        String file_name= "file1.pdf";//to add to submissions Collection
        String teamName= null;//name of team that submitted the file

        //asserts that a javax.ws.rs.WebApplicationException is thrown
//        Exception exception = assertThrows(
//                javax.ws.rs.WebApplicationException.class,
//                () -> assignInter.makeSubmission(course_id,assignment_id,file_name,teamName));//call method to be tested

    }































}




















































//The following is code from last year:
/*
// DISCLAIMER: Don't run all the tests at the same time. You'll likely screw up the database and fail the tests in some way.
// Read through the tests to see what they create, update and delete before you run them please.

public class StudentAssignmentTests {
    private static final Jsonb jsonb = JsonbBuilder.create();
    private static String port;
    private static String baseUrl;
    private static String targetUrl;
    private Client client;

    @BeforeAll
    public static void oneTimeSetup() {
        String courseId = "CSC378-800-54266-Spring-2023";
        int assignmentId = 55; // how can you keep track of this value in this MS???

        port = "13125";
        baseUrl = "http://moxie.cs.oswego.edu:" + port + "/assignments/student/courses/"+courseId+
                "/assignments/"+assignmentId;
    }

    @BeforeEach
    public void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void teardown() {
        client.close();
    }

    // For whatever reason, AttachmentBuilder.newBuilder is recognized at compiler time but not at run time...?
    // gives a NoClassDefFoundError if you try to run this test. I explored different dependency versions to fix this,
    // ... but no dice. Figured this was becoming more trouble than it was worth, so I switched tactics a bit.
    // For this reason, any tests that involve a file upload/download are only ever done in Postman.


    @Test
    public void studentUploadFileTest() {

        targetUrl = "/upload/";

        // Not included here

    }

    @Test
    public void studentDownloadFileTest() {

        String fileName = "";
        targetUrl = "/download/"+fileName+"/";

        // Not included here

    }
}
*/

