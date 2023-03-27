package edu.oswego.cs.rest.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.rest.daos.AssignmentDAO;
import edu.oswego.cs.rest.daos.AssignmentNoPeerReviewDAO;
import edu.oswego.cs.rest.daos.FileDAO;
import edu.oswego.cs.rest.daos.PeerReviewAddOnDAO;
import edu.oswego.cs.rest.util.CPRException;
import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.bson.types.Binary;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class AssignmentInterface {
    private final MongoCollection<Document> assignmentsCollection;
    private final MongoCollection<Document> courseCollection;
    private final MongoCollection<Document> submissionCollection;
    private static String reg;


    public AssignmentInterface() {
        try {
            DatabaseManager manager = new DatabaseManager();
            MongoDatabase assignmentDatabase = manager.getAssignmentDB();
            assignmentsCollection = assignmentDatabase.getCollection("assignments");
            submissionCollection = assignmentDatabase.getCollection("submissions");
            MongoDatabase courseDatabase = manager.getCourseDB();
            courseCollection = courseDatabase.getCollection("courses");
        } catch (CPRException e) {
            throw new CPRException(Response.Status.BAD_REQUEST,"Failed to retrieve collections.");
        }
    }


    /**
     * Write file binary data and file name of the assignment instructions to its respective assignment document in the
     * database.
     *
     * @param fileDAO  type FileDAO: Representation of File Data
     */

    /**
     * Write file binary data and file name of the assignment instructions to its respective assignment document in the
     * database.
     *
     * @param fileDAO  type FileDAO: Representation of File Data
     */

    public void writeToAssignment(FileDAO fileDAO) throws IOException {
        //the line below will get the document we are searching for
        Document result = assignmentsCollection.find(and(eq("course_id", fileDAO.courseID), eq("assignment_id", fileDAO.assignmentID))).first();
        //makes sure the result isn't null
        if (result == null) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment found");

        //add the assignment instructions binary data and file name to the database
        result.append("assignment_instructions_data", Base64.getDecoder().decode(new String(fileDAO.file.readAllBytes())));
        result.append("assignment_instructions_name", fileDAO.fileName);
        assignmentsCollection.replaceOne(and(eq("course_id", fileDAO.courseID), eq("assignment_id", fileDAO.assignmentID)), result);
    }

    /**
     *
     * @param fileDAO
     * @throws IOException
     */
    public void writeRubricToPeerReviews(FileDAO fileDAO) throws IOException {
        //the line below will get the document we are searching for
        Document result = assignmentsCollection.find(and(eq("course_id", fileDAO.courseID), eq("assignment_id", fileDAO.assignmentID))).first();
        //makes sure the result isn't null
        if (result == null) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment found");

        //add the assignment instructions binary data and file name to the database
        result.append("rubric_data", Base64.getDecoder().decode(new String(fileDAO.file.readAllBytes())));
        result.append("rubric_name", fileDAO.fileName);
        assignmentsCollection.replaceOne(and(eq("course_id", fileDAO.courseID), eq("assignment_id", fileDAO.assignmentID)), result);
    }

    /**
     *
     * @param fileDAO
     * @throws IOException
     */
    public void writeTemplateToPeerReviews(FileDAO fileDAO) throws IOException {
        //the line below will get the document we are searching for
        Document result = assignmentsCollection.find(and(eq("course_id", fileDAO.courseID), eq("assignment_id", fileDAO.assignmentID))).first();
        //makes sure the result isn't null
        if (result == null) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment found");

        //add the assignment instructions binary data and file name to the database
        result.append("peer_review_template_data", Base64.getDecoder().decode(new String(fileDAO.file.readAllBytes())));
        result.append("peer_review_template_name", fileDAO.fileName);
        assignmentsCollection.replaceOne(and(eq("course_id", fileDAO.courseID), eq("assignment_id", fileDAO.assignmentID)), result);
    }


    /**
     * Grabs the binary data of the assignment instructions for the respective assignment
     *
     * @param courseID  type String
     * @param assignmentID  type Integer
     */

    public byte[] getInstructionFileData(String courseID, Integer assignmentID){
        Document result = assignmentsCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        //makes sure the result isn't null
        if (result == null) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment found");

        //grab the assignment instructions data and return it, ensure the assignment instructions data exists first
        if(!result.containsKey("assignment_instructions_data")) throw new CPRException(Response.Status.NOT_FOUND, "No assignment instruction data uploaded");

        Binary data = (Binary) result.get("assignment_instructions_data");
        return data.getData();
    }

    /**
     * Grabs the name of the instructions file
     *
     * @param courseID  type String
     * @param assignmentID  type Integer
     */

    public String getInstructionFileName(String courseID, Integer assignmentID){
        Document result = assignmentsCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        //makes sure the result isn't null
        if (result == null) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment found");

        //grab the assignment instructions data and return it, ensure the assignment instructions data exists first
        if(!result.containsKey("assignment_instructions_name")) throw new CPRException(Response.Status.NOT_FOUND, "No assignment instruction data uploaded");

        return (String) result.get("assignment_instructions_name");
    }


    /**
     * Grabs the name of the rubric file
     *
     * @param courseID  type String
     * @param assignmentID  type Integer
     */

    public String getRubricFileName(String courseID, Integer assignmentID){
        Document result = assignmentsCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        //makes sure the result isn't null
        if (result == null) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment found");

        //grab the assignment instructions data and return it, ensure the assignment instructions data exists first
        if(!result.containsKey("rubric_name")) throw new CPRException(Response.Status.NOT_FOUND, "No assignment instruction data uploaded");

        return (String) result.get("rubric_name");
    }


    /**
     * Grab the binary data of the assignment rubric for the respective assignment
     *
     * @param courseID  type String
     * @param assignmentID  type Integer
     */

    public byte[] getRubricFileData(String courseID, Integer assignmentID){
        Document result = assignmentsCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        //makes sure the result isn't null
        if (result == null) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment found");

        //grab the assignment instructions data and return it, ensure the assignment instructions data exists first
        if(!result.containsKey("rubric_data")) throw new CPRException(Response.Status.NOT_FOUND, "No rubric data uploaded");

        Binary data = (Binary) result.get("rubric_data");
        return data.getData();
    }


    /**
     * Grabs the name of the rubric file
     *
     * @param courseID  type String
     * @param assignmentID  type Integer
     */

    public String getTemplateFileName(String courseID, Integer assignmentID){
        Document result = assignmentsCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        //makes sure the result isn't null
        if (result == null) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment found");

        //grab the assignment instructions data and return it, ensure the assignment instructions data exists first
        if(!result.containsKey("peer_review_template_name")) throw new CPRException(Response.Status.NOT_FOUND, "No assignment instruction data uploaded");

        return (String) result.get("peer_review_template_name");
    }

    /**
     * Grabs the binary data of the peer review template for the respective assignment
     *
     * @param courseID  type String
     * @param assignmentID  type Integer
     */

    public byte[] getPeerReviewTemplateData(String courseID, Integer assignmentID){
        Document result = assignmentsCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        //makes sure the result isn't null
        if (result == null) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment found");

        //grab the assignment instructions data and return it, ensure the assignment instructions data exists first
        if(!result.containsKey("peer_review_template_data")) throw new CPRException(Response.Status.NOT_FOUND, "No template data uploaded");

        Binary data = (Binary) result.get("peer_review_template_data");
        return data.getData();
    }

    public void removeFile(String courseID, int assignmentID) {
        Document result = assignmentsCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        //makes sure the result isn't null
        if (result == null) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment found");

        //grab the assignment instructions data and return it, ensure the assignment instructions data exists first
        if(!result.containsKey("assignment_instructions_data")) throw new CPRException(Response.Status.NOT_FOUND, "No template data uploaded");
        //add the assignment instructions binary data and file name to the database
        result.remove("assignment_instructions_data");
        result.remove("assignment_instructions_name");
        assignmentsCollection.replaceOne(and(eq("course_id", courseID), eq("assignment_id", assignmentID)), result);
    }

    public void removePeerReviewTemplate(String courseID, int assignmentID) {
        Document result = assignmentsCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        //makes sure the result isn't null
        if (result == null) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment found");

        //grab the assignment instructions data and return it, ensure the assignment instructions data exists first
        if(!result.containsKey("peer_review_template_data")) throw new CPRException(Response.Status.NOT_FOUND, "No template data uploaded");
        //add the assignment instructions binary data and file name to the database
        result.remove("peer_review_template_data");
        result.remove("peer_review_template_name");
        assignmentsCollection.replaceOne(and(eq("course_id", courseID), eq("assignment_id", assignmentID)), result);
    }

    public void removePeerReviewRubric(String courseID, int assignmentID) {
        Document result = assignmentsCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        //makes sure the result isn't null
        if (result == null) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment found");

        //grab the assignment instructions data and return it, ensure the assignment instructions data exists first
        if(!result.containsKey("rubric_data")) throw new CPRException(Response.Status.NOT_FOUND, "No template data uploaded");
        //add the assignment instructions binary data and file name to the database
        result.remove("rubric_data");
        result.remove("rubric_name");
        assignmentsCollection.replaceOne(and(eq("course_id", courseID), eq("assignment_id", assignmentID)), result);
    }

    /**
     * Creates the assignment data based on the POST request's sent data. Previously, this function would make
     * a file structure on the host machine to store the PDFs. Now it just stores the assignment data JSON
     * and the writeToAssignment function handles writing the Assignment PDF data in the database.
     *
     * @param assignmentDAO  type AssignmentDAO: Representation of Assignment Data
     * @return Document
     */

    public Document createAssignment(AssignmentDAO assignmentDAO) throws IOException {
        Document courseDocument = courseCollection.find(eq("course_id", assignmentDAO.courseID)).first();
        if (courseDocument == null) throw new CPRException(Response.Status.BAD_REQUEST,"Course not found.");
        int nextPos = generateAssignmentID();
        assignmentDAO.assignmentID = nextPos;

        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> assignmentDAOEntity = Entity.entity(jsonb.toJson(assignmentDAO), MediaType.APPLICATION_JSON_TYPE);
        Document assignmentDocument = Document.parse(assignmentDAOEntity.getEntity());
        assignmentDocument
                .append("submission_is_past_due", false)
                .append("peer_review_is_past_due", false)
                .append("grade_finalized", false)
                .append("has_peer_review", true);

        MongoCursor<Document> query = assignmentsCollection.find(assignmentDocument).iterator();
        if (query.hasNext()) {
            query.close();

            throw new CPRException(Response.Status.BAD_REQUEST,"This assignment already exists.");
        }

        assignmentsCollection.insertOne(assignmentDocument);
        return assignmentDocument;
    }

    /**
     *Creates assignment data for assignments without any peer review data
     *
     * @param assignmentDAO  type AssignmentNoPeerReviewDAO: Representation of Assignment Data
     * @return Document
     */

    public Document createAssignmentNoPeerReview(AssignmentNoPeerReviewDAO assignmentDAO) throws IOException {
        Document courseDocument = courseCollection.find(eq("course_id", assignmentDAO.courseID)).first();
        if (courseDocument == null) throw new CPRException(Response.Status.BAD_REQUEST,"Course not found.");
        int nextPos = generateAssignmentID();
        assignmentDAO.assignmentID = nextPos;

        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> assignmentDAOEntity = Entity.entity(jsonb.toJson(assignmentDAO), MediaType.APPLICATION_JSON_TYPE);
        Document assignmentDocument = Document.parse(assignmentDAOEntity.getEntity());
        assignmentDocument
                .append("submission_is_past_due", false)
                .append("grade_finalized", false)
                .append("has_peer_review", false);

        MongoCursor<Document> query = assignmentsCollection.find(assignmentDocument).iterator();
        if (query.hasNext()) {
            query.close();

            throw new CPRException(Response.Status.BAD_REQUEST,"This assignment already exists.");
        }

        assignmentsCollection.insertOne(assignmentDocument);
        return assignmentDocument;
    }

    /**
     * Appends peer review data onto an assignment that previously had no peer review data
     *
     * @param courseID
     * @param AssignmentID
     * @param peerReviewAddOnDAO
     */
    public String addPeerReviewDataToAssignment(String courseID, int AssignmentID, PeerReviewAddOnDAO peerReviewAddOnDAO){
        Document assignmentDocument = assignmentsCollection.find(and(eq("assignment_id", AssignmentID),eq("course_id", courseID))).first();
        if (assignmentDocument == null) throw new CPRException(Response.Status.BAD_REQUEST,"This assignment does not exist.");
        assignmentDocument.append("peer_review_due_date", peerReviewAddOnDAO.peerReviewDueDate)
                .append("peer_review_instructions", peerReviewAddOnDAO.peerReviewInstructions)
                .append("peer_review_points", peerReviewAddOnDAO.peerReviewPoints)
                .append("peer_review_is_past_due", false);
        assignmentDocument.replace("has_peer_review", true);

        assignmentsCollection.replaceOne(and(eq("assignment_id", AssignmentID),eq("course_id", courseID)), assignmentDocument);
        return (String) assignmentDocument.get("assignment_name");
    }

    public List<Document> getAllAssignments() {
        MongoCursor<Document> query = assignmentsCollection.find().iterator();
        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            assignments.add(document);
        }
        return assignments;
    }

    /**
     *
     * @param courseID
     * @return
     */

    public List<Document> getAssignmentsByCourse(String courseID) {
        MongoCursor<Document> query = assignmentsCollection.find(eq("course_id", courseID)).iterator();
        if (!query.hasNext()) return Collections.emptyList();

        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            assignments.add(document);
        }
        return assignments;
    }

    /**
     *
     * @param courseID
     * @param AssignmentID
     * @return
     */

    public Document getSpecifiedAssignment(String courseID, int AssignmentID) {
        Document assignment = assignmentsCollection.find(and(
                eq("course_id", courseID),
                eq("assignment_id", AssignmentID))).first();
        if (assignment == null) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment by this name found");
        return assignment;
    }

    /**
     *
     * @param assignmentDAO
     * @param courseID
     * @param assignmentID
     */

    public void updateAssignment(AssignmentDAO assignmentDAO, String courseID, int assignmentID) {
        Document assignmentDocument = assignmentsCollection.find(and(eq("assignment_id", assignmentID),eq("course_id", courseID))).first();
        if (assignmentDocument == null) throw new CPRException(Response.Status.BAD_REQUEST,"This assignment does not exist.");
        assignmentDocument.replace("assignment_name", assignmentDAO.assignmentName);
        assignmentDocument.replace("due_date", assignmentDAO.dueDate);
        assignmentDocument.replace("instructions", assignmentDAO.instructions);
        assignmentDocument.replace("points", assignmentDAO.points);
        assignmentDocument.replace("peer_review_instructions", assignmentDAO.peerReviewInstructions);
        assignmentDocument.replace("peer_review_due_date", assignmentDAO.peerReviewDueDate);
        assignmentDocument.replace("peer_review_points", assignmentDAO.peerReviewPoints);

        assignmentsCollection.replaceOne(and(eq("assignment_id", assignmentID),eq("course_id", courseID)), assignmentDocument);
    }

    /**
     * Update an assignment's data that has no peer review
     *
     * @param assignmentNoPeerReviewDAO
     * @param courseID
     * @param assignmentID
     */
    public void updateAssignmentWithNoPeerReview(AssignmentNoPeerReviewDAO assignmentNoPeerReviewDAO, String courseID, int assignmentID) {
        Document assignmentDocument = assignmentsCollection.find(and(eq("assignment_id", assignmentID),eq("course_id", courseID))).first();
        if (assignmentDocument == null) throw new CPRException(Response.Status.BAD_REQUEST,"This assignment does not exist.");
        assignmentDocument.replace("assignment_name", assignmentNoPeerReviewDAO.assignmentName);
        assignmentDocument.replace("due_date", assignmentNoPeerReviewDAO.dueDate);
        assignmentDocument.replace("instructions", assignmentNoPeerReviewDAO.instructions);
        assignmentDocument.replace("points", assignmentNoPeerReviewDAO.points);

        assignmentsCollection.replaceOne(and(eq("assignment_id", assignmentID),eq("course_id", courseID)), assignmentDocument);
    }

    /**
     *
     * @param AssignmentID
     * @param courseID
     * @throws IOException
     */

    public void removeAssignment(int AssignmentID, String courseID) throws IOException {
        MongoCursor<Document> results = assignmentsCollection.find(and(
                eq("assignment_id", AssignmentID),
                eq("course_id", courseID))).iterator();
        if (!results.hasNext()) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment by this name found.");

        while (results.hasNext()) {
            Document assignment = results.next();
            assignmentsCollection.findOneAndDelete(assignment);
        }
        removeSubmissions(AssignmentID, courseID);
    }

    public void removeSubmissions(int AssignmentID, String courseID) throws IOException {
        for (Document submissionDoc : submissionCollection.find(and(eq("assignment_id", AssignmentID), eq("course_id", courseID))))
             submissionCollection.findOneAndDelete(submissionDoc);
    }

    /**
     *
     * @param courseID
     * @throws IOException
     */

    public void removeCourse(String courseID) throws IOException {
        MongoCursor<Document> results = assignmentsCollection.find(eq("course_id", courseID)).iterator();
        if (!results.hasNext()) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment by this name found.");

        while (results.hasNext()) {
            Document assignmentDocument = results.next();
            assignmentsCollection.findOneAndDelete(assignmentDocument);
        }
    }


    /**
    *
    * Iterates the assignment id by one based on how many assignments currently exist in the DB
    *
    **/


    /**
    *
    * Iterates the assignment id by one based on how many assignments currently exist in the DB
    *
    **/

    public int generateAssignmentID() {
        List<Document> assignmentsDocuments = getAllAssignments();

        Set<Integer> assignmentIDs = new HashSet<>();
        for (Document assignmentDocument : assignmentsDocuments)
            assignmentIDs.add(assignmentDocument.getInteger("assignment_id"));
        int max = 0;
        for (Integer assignmentID : assignmentIDs) {
            if (max < assignmentID)
                max = assignmentID;
        }
        return ++max;
    }
}