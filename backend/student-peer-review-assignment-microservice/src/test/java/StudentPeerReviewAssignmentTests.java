import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import com.mongodb.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import edu.oswego.cs.daos.FileDAO;
import edu.oswego.cs.database.DatabaseManager;
import edu.oswego.cs.database.PeerReviewAssignmentInterface;
import edu.oswego.cs.resources.PeerReviewAssignmentResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.bson.Document;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static edu.oswego.cs.database.PeerReviewAssignmentInterface.isOutlier;
import static org.junit.jupiter.api.Assertions.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.validation.constraints.AssertTrue;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/* *********************************************************************************************************************
ALL CODE THAT IS COMMENTED OUT CAUSES parseInt ERRORS DUE TO
ENVIRONMENT VARIABLE ISSUES OR BECAUSE THERE ARE PARAMETER MISMATCHES OR BECAUSE
THERE ARE METHODS BEING CALLED THAT DO NOT EXIST AND POSSIBLE OTHER ISSUES I DID NOT CATCH.
*********************************************************************************************************************
 */

//please use the default db setup outlined in the "Database Default for Testing" file
//If a test fails, please ensure db is back to default before testing again
public class StudentPeerReviewAssignmentTests {

//    PeerReviewAssignmentInterface pr = new PeerReviewAssignmentInterface();
//    MongoClient mongoClient, mongoClient2, mongoClient3, mongoClient4, mongoClient5;
//    MongoCollection<Document> teamCollection, assignmentCollection, submissionsCollection;



   /* //NOT giving access!!!
    @BeforeAll
    public static void setup(){
        //from env file
        String username = "root";
        String database = "admin";
        String password = "password";
        //Create credentials
        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
        MongoClientOptions options = MongoClientOptions.builder()
                .writeConcern(WriteConcern.JOURNALED).build();

        //connects to localhost and specified port (ex. 127.0.0.1:27017) (ie. connects Java program to MongoDB server on localhost at given port)
    *//*    MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27037), Arrays.asList(mongoCredential), options);//holds students Collection
        MongoClient mongoClient2 = new MongoClient(new ServerAddress("localhost", 27038), Arrays.asList(mongoCredential), options);//holds professors Collection
        MongoClient mongoClient3 = new MongoClient(new ServerAddress("localhost", 27039), Arrays.asList(mongoCredential), options);//holds courses Collection
        MongoClient mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);//holds assignments, submissions Collections
        MongoClient mongoClient5 = new MongoClient(new ServerAddress("localhost", 27041), Arrays.asList(mongoCredential), options);//holds teams Collection*//*

        //gets specified Collections
    MongoCollection<Document> teamCollection = mongoClient5.getDatabase("cpr").getCollection("teams");
    MongoCollection<Document> assignmentCollection = mongoClient4.getDatabase("cpr").getCollection("assignments");
    MongoCollection<Document> submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");

    }*/


    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //TC|ID 1.1 = TCIDOnePointOne is the naming convention for now

    //To test:
    //public void addPeerReviewSubmission(String course_id,int assignment_id,String srcTeamName,String destinationTeam,String fileName,int grade)

//    @Test
//    public void addPeerReviewSubmissionTCIDOnePointOne() {//valid String ""CSC-234-2342-SPR-2023"
//
//        //gives access to db
//        //------------------------------------------------------------------------------------------------------------------------------
//        //from env file
//        String username = "root";
//        String database = "admin";
//        String password = "password";
//        //Create credentials
//        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
//        MongoClientOptions options = MongoClientOptions.builder()
//                .writeConcern(WriteConcern.JOURNALED).build();
//
//        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
//        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
//        //------------------------------------------------------------------------------------------------------------------------------
//
//        String course_id = "CSC-480-46374-SPR-2023";
//        int assignment_id = 1;
//        String srcTeamName = "Team1";//aka reviewed_by
//        String destinationTeam = "Team2";//aka reviewed_team
//        String fileName = "PeerReviewFile1.pdf";
//        int grade = 80;
//        pr.addPeerReviewSubmission(course_id, assignment_id, srcTeamName, destinationTeam, fileName, grade);//add a Document with the given values
//
//        Document docFromQuery = null;
//        MongoCursor<Document> query = submissionsCollection.find(and(
//                eq("course_id", course_id),
//                eq("assignment_id", assignment_id),
//                eq("submission_name", fileName),
//                eq("grade", grade),
//                eq("reviewed_by", srcTeamName),
//                eq("reviewed_team", destinationTeam),
//                eq("type", "peer_review_submission"))).iterator();
//        docFromQuery = query.next();//gets the Document returned by the query
//
//        assertFalse(docFromQuery == null);
//
//        //remove the change we made to the db (ie. remove the Document we just added)
//        submissionsCollection.deleteOne(and(Filters.
//                        eq("course_id", course_id),
//                eq("assignment_id", assignment_id),
//                eq("submission_name", fileName),
//                eq("grade", grade),
//                eq("reviewed_by", srcTeamName),
//                eq("reviewed_team", destinationTeam),
//                eq("type", "peer_review_submission")));
//
//    }
//
//    @Test
//    public void addPeerReviewSubmissionTCIDOnePointA() {//null
//        //gives access to db
//        //------------------------------------------------------------------------------------------------------------------------------
//        //from env file
//        String username = "root";
//        String database = "admin";
//        String password = "password";
//        //Create credentials
//        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
//        MongoClientOptions options = MongoClientOptions.builder()
//                .writeConcern(WriteConcern.JOURNALED).build();
//
//        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
//        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
//        //------------------------------------------------------------------------------------------------------------------------------
//
//        String course_id = null;
//        int assignment_id = 1;
//        String srcTeamName = "Team1";
//        String destinationTeam = "Team2";
//        String fileName = "PeerReviewFile1.pdf";
//        int grade = 80;
//
//        //asserts that a javax.ws.rs.WebApplicationException is thrown
//        Exception exception = assertThrows(
//                javax.ws.rs.WebApplicationException.class,
//                () -> pr.addPeerReviewSubmission(course_id, assignment_id, srcTeamName, destinationTeam, fileName, grade));
//
//    }
//
//    @Test
//    public void addPeerReviewSubmissionTCIDTwoPointA() {//assignment_id is -1
//
//        //gives access to db
//        //------------------------------------------------------------------------------------------------------------------------------
//        //from env file
//        String username = "root";
//        String database = "admin";
//        String password = "password";
//        //Create credentials
//        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
//        MongoClientOptions options = MongoClientOptions.builder()
//                .writeConcern(WriteConcern.JOURNALED).build();
//
//        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
//        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
//        //------------------------------------------------------------------------------------------------------------------------------
//
//        String course_id = "CSC-480-46374-SPR-2023";
//        int assignment_id = -1;
//        String srcTeamName = "Team1";
//        String destinationTeam = "Team2";
//        String fileName = "PeerReviewFile1.pdf";
//        int grade = 80;
//
//        //asserts that a javax.ws.rs.WebApplicationException is thrown
//        Exception exception = assertThrows(
//                javax.ws.rs.WebApplicationException.class,
//                () -> pr.addPeerReviewSubmission(course_id, assignment_id, srcTeamName, destinationTeam, fileName, grade));
//
//
//    }
//
//    @Test
//    public void addPeerReviewSubmissionTCIDTwoPointB() {//assignment_id is 0
//
//        //gives access to db
//        //------------------------------------------------------------------------------------------------------------------------------
//        //from env file
//        String username = "root";
//        String database = "admin";
//        String password = "password";
//        //Create credentials
//        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
//        MongoClientOptions options = MongoClientOptions.builder()
//                .writeConcern(WriteConcern.JOURNALED).build();
//
//        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
//        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
//        //------------------------------------------------------------------------------------------------------------------------------
//
//        String course_id = "CSC-480-46374-SPR-2023";
//        int assignment_id = 0;
//        String srcTeamName = "Team1";
//        String destinationTeam = "Team2";
//        String fileName = "PeerReviewFile1.pdf";
//        int grade = 80;
//
//        //asserts that a javax.ws.rs.WebApplicationException is thrown
//        Exception exception = assertThrows(
//                javax.ws.rs.WebApplicationException.class,
//                () -> pr.addPeerReviewSubmission(course_id, assignment_id, srcTeamName, destinationTeam, fileName, grade));
//
//
//    }
//
//    @Test
//    public void addPeerReviewSubmissionTCIDThreePointA() {//null srcTeamName
//        //gives access to db
//        //------------------------------------------------------------------------------------------------------------------------------
//        //from env file
//        String username = "root";
//        String database = "admin";
//        String password = "password";
//        //Create credentials
//        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
//        MongoClientOptions options = MongoClientOptions.builder()
//                .writeConcern(WriteConcern.JOURNALED).build();
//
//        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
//        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
//        //------------------------------------------------------------------------------------------------------------------------------
//
//        String course_id = "CSC-480-46374-SPR-2023";
//        int assignment_id = 1;
//        String srcTeamName = null;
//        String destinationTeam = "Team2";
//        String fileName = "PeerReviewFile1.pdf";
//        int grade = 80;
//
//        //asserts that a javax.ws.rs.WebApplicationException is thrown
//        Exception exception = assertThrows(
//                javax.ws.rs.WebApplicationException.class,
//                () -> pr.addPeerReviewSubmission(course_id, assignment_id, srcTeamName, destinationTeam, fileName, grade));
//
//    }
//
//    @Test
//    public void addPeerReviewSubmissionTCIDFourPointA() {//null destinationTeam
//        //gives access to db
//        //------------------------------------------------------------------------------------------------------------------------------
//        //from env file
//        String username = "root";
//        String database = "admin";
//        String password = "password";
//        //Create credentials
//        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
//        MongoClientOptions options = MongoClientOptions.builder()
//                .writeConcern(WriteConcern.JOURNALED).build();
//
//        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
//        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
//        //------------------------------------------------------------------------------------------------------------------------------
//
//        String course_id = "CSC-480-46374-SPR-2023";
//        int assignment_id = 1;
//        String srcTeamName = "Team1";
//        String destinationTeam = null;
//        String fileName = "PeerReviewFile1.pdf";
//        int grade = 80;
//
//        //asserts that a javax.ws.rs.WebApplicationException is thrown
//        Exception exception = assertThrows(
//                javax.ws.rs.WebApplicationException.class,
//                () -> pr.addPeerReviewSubmission(course_id, assignment_id, srcTeamName, destinationTeam, fileName, grade));
//
//    }
//
//    @Test
//    public void addPeerReviewSubmissionTCIDFivePointTwo() {//valid fileName
//
//        //gives access to db
//        //------------------------------------------------------------------------------------------------------------------------------
//        //from env file
//        String username = "root";
//        String database = "admin";
//        String password = "password";
//        //Create credentials
//        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
//        MongoClientOptions options = MongoClientOptions.builder()
//                .writeConcern(WriteConcern.JOURNALED).build();
//
//        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
//        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
//        //------------------------------------------------------------------------------------------------------------------------------
//
//        String course_id = "CSC-480-46374-SPR-2023";
//        int assignment_id = 1;
//        String srcTeamName = "Team1";//aka reviewed_by
//        String destinationTeam = "Team2";//aka reviewed_team
//        String fileName = "PeerReviewFile1.csv";
//        int grade = 80;
//
//        //asserts that a javax.ws.rs.WebApplicationException is thrown
//        Exception exception = assertThrows(
//                javax.ws.rs.WebApplicationException.class,
//                () -> pr.addPeerReviewSubmission(course_id, assignment_id, srcTeamName, destinationTeam, fileName, grade));
//
//    }
//
//
//    @Test
//    public void addPeerReviewSubmissionTCIDSixPointOne() {//valid grade 32
//
//        //gives access to db
//        //------------------------------------------------------------------------------------------------------------------------------
//        //from env file
//        String username = "root";
//        String database = "admin";
//        String password = "password";
//        //Create credentials
//        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
//        MongoClientOptions options = MongoClientOptions.builder()
//                .writeConcern(WriteConcern.JOURNALED).build();
//
//        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
//        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
//        //------------------------------------------------------------------------------------------------------------------------------
//
//        String course_id = "CSC-480-46374-SPR-2023";
//        int assignment_id = 1;
//        String srcTeamName = "Team1";//aka reviewed_by
//        String destinationTeam = "Team2";//aka reviewed_team
//        String fileName = "PeerReviewFile1.pdf";
//        int grade = 32;
//        pr.addPeerReviewSubmission(course_id, assignment_id, srcTeamName, destinationTeam, fileName, grade);//add a Document with the given values
//
//        Document docFromQuery = null;
//        MongoCursor<Document> query = submissionsCollection.find(and(
//                eq("course_id", course_id),
//                eq("assignment_id", assignment_id),
//                eq("submission_name", fileName),
//                eq("grade", grade),
//                eq("reviewed_by", srcTeamName),
//                eq("reviewed_team", destinationTeam),
//                eq("type", "peer_review_submission"))).iterator();
//        docFromQuery = query.next();//gets the Document returned by the query
//
//        assertFalse(docFromQuery == null);
//
//        //remove the change we made to the db (ie. remove the Document we just added)
//        submissionsCollection.deleteOne(and(Filters.
//                        eq("course_id", course_id),
//                eq("assignment_id", assignment_id),
//                eq("submission_name", fileName),
//                eq("grade", grade),
//                eq("reviewed_by", srcTeamName),
//                eq("reviewed_team", destinationTeam),
//                eq("type", "peer_review_submission")));
//
//    }
//
//    @Test
//    public void addPeerReviewSubmissionTCIDSixPointTwo() {//valid grade 0
//
//        //gives access to db
//        //------------------------------------------------------------------------------------------------------------------------------
//        //from env file
//        String username = "root";
//        String database = "admin";
//        String password = "password";
//        //Create credentials
//        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
//        MongoClientOptions options = MongoClientOptions.builder()
//                .writeConcern(WriteConcern.JOURNALED).build();
//
//        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
//        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
//        //------------------------------------------------------------------------------------------------------------------------------
//
//        String course_id = "CSC-480-46374-SPR-2023";
//        int assignment_id = 1;
//        String srcTeamName = "Team1";//aka reviewed_by
//        String destinationTeam = "Team2";//aka reviewed_team
//        String fileName = "PeerReviewFile1.pdf";
//        int grade = 0;
//        pr.addPeerReviewSubmission(course_id, assignment_id, srcTeamName, destinationTeam, fileName, grade);//add a Document with the given values
//
//        Document docFromQuery = null;
//        MongoCursor<Document> query = submissionsCollection.find(and(
//                eq("course_id", course_id),
//                eq("assignment_id", assignment_id),
//                eq("submission_name", fileName),
//                eq("grade", grade),
//                eq("reviewed_by", srcTeamName),
//                eq("reviewed_team", destinationTeam),
//                eq("type", "peer_review_submission"))).iterator();
//        docFromQuery = query.next();//gets the Document returned by the query
//
//        assertFalse(docFromQuery == null);
//
//        //remove the change we made to the db (ie. remove the Document we just added)
//        submissionsCollection.deleteOne(and(Filters.
//                        eq("course_id", course_id),
//                eq("assignment_id", assignment_id),
//                eq("submission_name", fileName),
//                eq("grade", grade),
//                eq("reviewed_by", srcTeamName),
//                eq("reviewed_team", destinationTeam),
//                eq("type", "peer_review_submission")));
//
//    }
//
//
//    @Test
//    public void addPeerReviewSubmissionTCIDSixPointA() {//grade -2
//        //gives access to db
//        //------------------------------------------------------------------------------------------------------------------------------
//        //from env file
//        String username = "root";
//        String database = "admin";
//        String password = "password";
//        //Create credentials
//        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
//        MongoClientOptions options = MongoClientOptions.builder()
//                .writeConcern(WriteConcern.JOURNALED).build();
//
//        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
//        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
//        //------------------------------------------------------------------------------------------------------------------------------
//
//        String course_id = "CSC-480-46374-SPR-2023";
//        int assignment_id = 1;
//        String srcTeamName = "Team1";
//        String destinationTeam = "Team2";
//        String fileName = "PeerReviewFile1.pdf";
//        int grade = -2;
//
//        //asserts that a javax.ws.rs.WebApplicationException is thrown
//        Exception exception = assertThrows(
//                javax.ws.rs.WebApplicationException.class,
//                () -> pr.addPeerReviewSubmission(course_id, assignment_id, srcTeamName, destinationTeam, fileName, grade));
//
//    }
//
//
//    @Test
//    public void addPeerReviewSubmissionTCIDFivePointA() {//null fileName
//        //gives access to db
//        //------------------------------------------------------------------------------------------------------------------------------
//        //from env file
//        String username = "root";
//        String database = "admin";
//        String password = "password";
//        //Create credentials
//        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
//        MongoClientOptions options = MongoClientOptions.builder()
//                .writeConcern(WriteConcern.JOURNALED).build();
//
//        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
//        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
//        //------------------------------------------------------------------------------------------------------------------------------
//
//        String course_id = "CSC-480-46374-SPR-2023";
//        int assignment_id = 1;
//        String srcTeamName = "Team1";
//        String destinationTeam = "Team2";
//        String fileName = null;
//        int grade = 80;
//
//        //asserts that a javax.ws.rs.WebApplicationException is thrown
//        Exception exception = assertThrows(
//                javax.ws.rs.WebApplicationException.class,
//                () -> pr.addPeerReviewSubmission(course_id, assignment_id, srcTeamName, destinationTeam, fileName, grade));
//
//    }
//
//    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//
//    //To test:
//    //public void uploadPeerReview(String courseID, int assignmentID, String srcTeamName, String destTeamName, IAttachment attachment)
//
//    IAttachment attachment = new IAttachment() {
//        @Override
//        public String getContentId() {
//            return null;
//        }
//
//        @Override
//        public MediaType getContentType() {
//            return null;
//        }
//
//        @Override
//        public DataHandler getDataHandler() {
//            DataHandler dh = new DataHandler(new DataSource() {
//                @Override
//                public InputStream getInputStream() throws IOException {
//                    InputStream in = new FileInputStream(new File("src/main/liberty/config/server.xml"));//random filepath
//                    return in;
//                }
//
//                @Override
//                public OutputStream getOutputStream() throws IOException {
//                    return null;
//                }
//
//                @Override
//                public String getContentType() {
//                    return null;
//                }
//
//                @Override
//                public String getName() {
//                    return null;
//                }
//            });
//
//            return dh;
//        }
//
//        @Override
//        public String getHeader(String s) {
//            return null;
//        }
//
//        @Override
//        public MultivaluedMap<String, String> getHeaders() {
//            return null;
//        }
//    };
//
//    @Test
//    public void uploadPeerReviewTCIDOnePointOne() throws IOException {//valid courseID
//        String courseID = "CSC-480-46374-SPR-2023";
//        int assignmentID = 1;
//        String srcTeamName = "Team1";
//        String destTeamName = "Team2";
//
//        pr.uploadPeerReview(courseID, assignmentID, srcTeamName, destTeamName, attachment);
//
//        String path = "assignments" + "/" + courseID + "/" + assignmentID + "/peer-review-submissions/";//copied from method being tested
//        String DAOfileName = "from-" + srcTeamName + "-to-" + destTeamName;
//        path = path + DAOfileName + ".pdf";
//
//        assertTrue(new File(path).exists());
//
//        //delete File created for testing
//        new File(path).delete();
//
//    }
//
//    @Test
//    public void uploadPeerReviewTCIDOnePointA() throws IOException {//null courseID
//        String courseID = null;
//        int assignmentID = 1;
//        String srcTeamName = "Team1";
//        String destTeamName = "Team2";
//
//        pr.uploadPeerReview(courseID, assignmentID, srcTeamName, destTeamName, attachment);
//
//        String path = "assignments" + "/" + courseID + "/" + assignmentID + "/peer-review-submissions/";//copied from method being tested
//        String DAOfileName = "from-" + srcTeamName + "-to-" + destTeamName;
//        path = path + DAOfileName + ".pdf";
//
//        assertFalse(new File(path).exists());
//
//        //delete File created for testing
//        new File(path).delete();
//
//    }
//
//    @Test
//    public void uploadPeerReviewTCIDTwoPointA() throws IOException {//assignmentID -2
//        String courseID = "CSC-480-46374-SPR-2023";
//        int assignmentID = -1;
//        String srcTeamName = "Team1";
//        String destTeamName = "Team2";
//
//        pr.uploadPeerReview(courseID, assignmentID, srcTeamName, destTeamName, attachment);
//
//        String path = "assignments" + "/" + courseID + "/" + assignmentID + "/peer-review-submissions/";//copied from method being tested
//        String DAOfileName = "from-" + srcTeamName + "-to-" + destTeamName;
//        path = path + DAOfileName + ".pdf";
//
//        assertFalse(new File(path).exists());
//
//        //delete File created for testing
//        new File(path).delete();
//
//    }
//
//    @Test
//    public void uploadPeerReviewTCIDTwoPointB() throws IOException {//assignmentID 0
//        String courseID = "CSC-480-46374-SPR-2023";
//        int assignmentID = 0;
//        String srcTeamName = "Team1";
//        String destTeamName = "Team2";
//
//        pr.uploadPeerReview(courseID, assignmentID, srcTeamName, destTeamName, attachment);
//
//        String path = "assignments" + "/" + courseID + "/" + assignmentID + "/peer-review-submissions/";//copied from method being tested
//        String DAOfileName = "from-" + srcTeamName + "-to-" + destTeamName;
//        path = path + DAOfileName + ".pdf";
//
//        assertFalse(new File(path).exists());
//
//        //delete File created for testing
//        new File(path).delete();
//
//    }
//
//    @Test
//    public void uploadPeerReviewTCIDThreePointA() throws IOException {//srcTeamName null
//        String courseID = "CSC-480-46374-SPR-2023";
//        int assignmentID = 1;
//        String srcTeamName = null;
//        String destTeamName = "Team2";
//
//        pr.uploadPeerReview(courseID, assignmentID, srcTeamName, destTeamName, attachment);
//
//        String path = "assignments" + "/" + courseID + "/" + assignmentID + "/peer-review-submissions/";//copied from method being tested
//        String DAOfileName = "from-" + srcTeamName + "-to-" + destTeamName;
//        path = path + DAOfileName + ".pdf";
//
//        assertFalse(new File(path).exists());
//
//        //delete File created for testing
//        new File(path).delete();
//
//    }
//
//    @Test
//    public void uploadPeerReviewTCIDFourPointA() throws IOException {//destTeamName null
//        String courseID = "CSC-480-46374-SPR-2023";
//        int assignmentID = 1;
//        String srcTeamName = "Team1";
//        String destTeamName = null;
//
//        pr.uploadPeerReview(courseID, assignmentID, srcTeamName, destTeamName, attachment);
//
//        String path = "assignments" + "/" + courseID + "/" + assignmentID + "/peer-review-submissions/";//copied from method being tested
//        String DAOfileName = "from-" + srcTeamName + "-to-" + destTeamName;
//        path = path + DAOfileName + ".pdf";
//
//        assertFalse(new File(path).exists());
//
//        //delete File created for testing
//        new File(path).delete();
//
//    }
//
//    @Test
//    public void uploadPeerReviewTCIDFivePointA() throws IOException {//attachment null
//        String courseID = "CSC-480-46374-SPR-2023";
//        int assignmentID = 1;
//        String srcTeamName = "Team1";
//        String destTeamName = "Team2";
//        attachment = null;
//
//        //asserts that a javax.ws.rs.WebApplicationException is thrown
//        Exception exception = assertThrows(
//                NullPointerException.class,
//                () -> pr.uploadPeerReview(courseID, assignmentID, srcTeamName, destTeamName, attachment));
//
//    }
//
//    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//
//    //To test:
//    // public File downloadFinishedPeerReview(String courseID, int assignmentID, String srcTeamName, String destTeamName)
//
//    @Test
//    public void downloadFinishedPeerReviewTCIDOnePointOne() throws IOException {//valid courseID
//
//        //gives access to db
//        //------------------------------------------------------------------------------------------------------------------------------
//        //from env file
//        String username = "root";
//        String database = "admin";
//        String password = "password";
//        //Create credentials
//        MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
//        MongoClientOptions options = MongoClientOptions.builder()
//                .writeConcern(WriteConcern.JOURNALED).build();
//
//        mongoClient4 = new MongoClient(new ServerAddress("localhost", 27040), Arrays.asList(mongoCredential), options);
//        submissionsCollection = mongoClient4.getDatabase("cpr").getCollection("submissions");
//        //------------------------------------------------------------------------------------------------------------------------------
//
//        pr.addPeerReviewSubmission("CSC-480-46374-SPR-2023", 1, "Team1", "Team2", "PeerReviewFile1.pdf", 80);
//        pr.uploadPeerReview("CSC-480-46374-SPR-2023", 1, "Team1", "Team2", attachment);
//
//        String courseID = "CSC-480-46374-SPR-2023";
//        int assignmentID = 1;
//        String srcTeamName = "Team1";
//        String destTeamName = "Team2";
//
//        File actualFile = pr.downloadFinishedPeerReview(courseID, assignmentID, srcTeamName, destTeamName);
//        System.out.println("file name= " + actualFile.getName());
//        System.out.println("file path= " + actualFile.getPath());
//        assertTrue(actualFile != null);
//
//
//        //delete File uploaded/created for testing
//        String path = "assignments" + "/" + "CSC-480-46374-SPR-2023" + "/" + "1" + "/peer-review-submissions/";
//        String DAOfileName = "from-" + "Team1" + "-to-" + "Team2";
//        path = path + DAOfileName + ".pdf";
//        System.out.println("test file just created was deleted after test= " + new File(path).delete());
//
//        //remove the Document we just added to the db
//        submissionsCollection.deleteOne(and(Filters.
//                        eq("course_id", "CSC-480-46374-SPR-2023"),
//                eq("assignment_id", 1),
//                eq("submission_name", "PeerReviewFile1.pdf"),
//                eq("grade", 80),
//                eq("reviewed_by", "Team1"),
//                eq("reviewed_team", "Team2"),
//                eq("type", "peer_review_submission")));
//
//    }

    @Test
    public void outlierDetectionTest() {
        int[] testOne = {65, 65, 65, 65, 100};
        int[] testTwo = {75, 50, 50, 50, 50};
        int[] testThree = {75, 90, 90, 90, 90};
        int[] testFour = {90, 90, 90, 90, 90};

        for (int i = 0; i < testOne.length; i++) {
            if (i == 4)
                assertEquals(true, isOutlier(testOne[i], 65, 65, 0));
            else
                assertEquals(false, isOutlier(testOne[i], 65, 65, 0));
        }

        for (int i = 0; i < testTwo.length; i++) {
            if (i == 0)
                assertEquals(true, isOutlier(testTwo[i], 50, 50, 0));
            else
                assertEquals(false, isOutlier(testTwo[i], 50, 50, 0));
        }

        for (int i = 0; i < testThree.length; i++) {
            if (i == 0)
                assertEquals(true, isOutlier(testThree[i], 90, 90, 0));
            else
                assertEquals(false, isOutlier(testThree[i], 90, 90, 0));
        }

        for (int j : testFour) {
            assertEquals(false, isOutlier(j, 90, 90, 0));
        }
    }
}