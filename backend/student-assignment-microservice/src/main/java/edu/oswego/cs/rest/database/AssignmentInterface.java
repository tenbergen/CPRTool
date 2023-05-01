package edu.oswego.cs.rest.database;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import edu.oswego.cs.rest.daos.FileDAO;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;

import javax.print.Doc;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.set;


public class AssignmentInterface {

    static MongoDatabase assignmentDatabase;
    static MongoDatabase teamsDatabase;
    static MongoCollection<Document> assignmentsCollection;

    static MongoCollection<Document> courseCollection;
    static MongoCollection<Document> teamsCollection;
    static MongoCollection<Document> submissionCollection;
    static MongoCollection<Document> professorCollection;
    static MongoCollection<Document> studentCollection;

    static ConcurrentHashMap<String, Boolean> assignmentLock = new ConcurrentHashMap<>();

    static String reg = "/";

    public AssignmentInterface() {
        try {
            DatabaseManager manager = new DatabaseManager();
            assignmentDatabase = manager.getAssignmentDB();
            teamsDatabase = manager.getTeamDB();
            assignmentsCollection = assignmentDatabase.getCollection("assignments");
            submissionCollection = assignmentDatabase.getCollection("submissions");
            teamsCollection = teamsDatabase.getCollection("teams");
            professorCollection = manager.getProfessorDB().getCollection("professors");
            courseCollection = manager.getCourseDB().getCollection("courses");
            studentCollection = manager.getStudentDB().getCollection("students");

        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
        }
    }

    public void writeToAssignment(FileDAO fileDAO) throws IOException {
        makeSubmission(fileDAO.getCourseID(), fileDAO.getAssignmentID(), fileDAO.getFilename(), fileDAO.getTeamName(), fileDAO.getFile());
    }

    public List<String> getCourseProfanityWords(String courseID) {
        return courseCollection.find(eq("course_id", courseID)).first().getList("blocked_words", String.class);
    }

    public List<Document> getAllUserAssignments(String courseID, String studentID) {
        MongoCursor<Document> query = submissionCollection.find(and(eq("course_id", courseID),
                eq("members", studentID),
                eq("type", "team_submission"))).iterator();
        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            assignments.add(document);
        }
        if (assignments.isEmpty())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist").build());
        query.close();
        return assignments;
    }

    public Document getSpecifiedTeamSubmission(String courseID, int assignmentID, String teamID) {
        Document teamSubmission = submissionCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID), eq("team_name", teamID), eq("type", "team_submission"))).first();
        if (teamSubmission == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist").build());
        return teamSubmission;
    }

    public List<Document> getSpecifiedUserAssignment(String courseID, int assignmentID, String studentID) {
        MongoCursor<Document> query = submissionCollection.find(and(eq("course_id", courseID),
                eq("members", studentID),
                eq("assignment_id", assignmentID),
                eq("type", "team_submission"))).iterator();
        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            assignments.add(document);
        }
        if (assignments.isEmpty())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist").build());
        query.close();
        return assignments;
    }

    public List<Document> getAssignmentSubmissions(String courseID, int assignmentID) {
        MongoCursor<Document> query = submissionCollection.find(and(eq("course_id", courseID),
                eq("assignment_id", assignmentID),
                eq("type", "team_submission"))).iterator();
        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            assignments.add(document);
        }
        if (assignments.isEmpty())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist").build());
        query.close();
        return assignments;
    }

    public void makeSubmission(String course_id, int assignment_id, String file_name, String teamName, byte[] fileData) throws IOException {
        Document team = teamsCollection.find(and(eq("team_id", teamName), eq("course_id", course_id))).first();
        Document assignment = assignmentsCollection.find(and(
                eq("course_id", course_id),
                eq("assignment_id", assignment_id)
        )).first();
        if (assignment == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("this assignment was not found in this course").build());
        String assignmentName = assignment.getString("assignment_name");

        if (team == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("this team was not found in this course").build());
        }
        if (team.getList("team_members", String.class) == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Members not defined in team").build());
        }
        if (team.get("team_id", String.class) == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("team_id not defined").build());
        }
        Document new_submission = new Document()
                .append("course_id", course_id)
                .append("assignment_id", assignment_id)
                .append("assigment_name", assignmentName)
                .append("submission_name", file_name)
                .append("submission_data", Base64.getDecoder().decode(new String(fileData)))
                .append("team_name", team.getString("team_id"))
                .append("members", team.getList("team_members", String.class))
                .append("type", "team_submission")
                .append("grade", -1)
                .append("peer_review_due_date", assignment.get("peer_review_due_date"))
                .append("due_date", assignment.get("due_date"));

        boolean submissionCheck = submissionCollection.find(and(eq("course_id", course_id), eq("assignment_id", assignment_id), eq("team_name", team.getString("team_id")))).iterator().hasNext();
        // do some sort of spinning lock here so that only one teammate at a time is submitting an assignment at a time
        //key is assignment_ID+team_name+type
        while(assignmentLock.containsKey(assignment_id+team.getString("team_id")+"team_submission"));
        //set the lock
        assignmentLock.put(assignment_id+team.getString("team_id")+"team_submission", true);
        if (submissionCheck) {
            Document extensionCheck = submissionCollection.find(and(eq("course_id", course_id), eq("assignment_id", assignment_id), eq("team_name", team.getString("team_id")))).first();
            if (extensionCheck.getString("submission_name").equals(file_name)) {
                //remove the lock
                assignmentLock.remove(assignment_id+team.getString("team_id")+"team_submission");
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("submission already exists").build());
            } else {
                submissionCollection.deleteOne(extensionCheck);
                submissionCollection.insertOne(new_submission);
                //remove the lock
                assignmentLock.remove(assignment_id+team.getString("team_id")+"team_submission");
            }
        } else {
            submissionCollection.insertOne(new_submission);
            //remove the lock
            assignmentLock.remove(assignment_id+team.getString("team_id")+"team_submission");
        }
    }

    public Document allAssignments(String course_id, String student_id) {

        //Set up for checking due date
        Calendar calendar = Calendar.getInstance();
        String[] splitPeerReviewDueDate;
        Date peerReviewDueDate;
        Date currentDate = new Date();

        MongoCursor<Document> submissions = submissionCollection.find(
                and(
                        eq("course_id", course_id),
                        or(eq("members", student_id), eq("reviewed_by_members", student_id)),
                        or(eq("type", "team_submission"), eq("type", "peer_review_submission"))
                )
        ).iterator();
        if (!submissions.hasNext())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No submissions").build());
        List<Document> AllSubmissions = new ArrayList<>();
        while (submissions.hasNext()) {
            Document submission = submissions.next();

            // check if peer review is pass due and not graded, finalize grades if so.     sideNote: date format -> yyyy-MM-dd
            if ((int) submission.get("grade") != -1) {
                splitPeerReviewDueDate = submission.get("peer_review_due_date").toString().split("-");
                calendar.set(Calendar.YEAR, Integer.parseInt(splitPeerReviewDueDate[0]));
                calendar.set(Calendar.MONTH, Integer.parseInt(splitPeerReviewDueDate[1]));
                calendar.set(Calendar.DATE, Integer.parseInt(splitPeerReviewDueDate[2]));
                peerReviewDueDate = calendar.getTime();

                if (currentDate.after(peerReviewDueDate)) {
                    makeFinalGrades(course_id, (int) submission.get("assignment_id"));
                }
            }
            if (submission.getInteger("assignment_id") == null) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No ID in this submission").build());
            }
            Document Assignment = assignmentsCollection.find(
                    and(
                            eq("course_id", course_id),
                            eq("assignment_id", submission.getInteger("assignment_id"))
                    )
            ).first();
            if (Assignment == null) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No assignment matching the ID for this course").build());
            }
            int grade = -1;
            if (Assignment.getInteger("grade") != null) {
                grade = Assignment.getInteger("grade");
            }
            AllSubmissions.add(new Document()
                    .append("assignment_name", Assignment.getString("assignment_name"))
                    .append("grade", grade));
        }
        return new Document("submissions", AllSubmissions);
    }

    public void makeFinalGrades(String courseID, int assignmentID) {
        Document assignment = assignmentsCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        if (assignment == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment not found.").build());
        List<String> allTeams = assignment.getList("all_teams", String.class);
        int points = assignment.getInteger("points");
        for (String team : allTeams) {
            Document team_submission = submissionCollection.find(and(
                    eq("course_id", courseID),
                    eq("assignment_id", assignmentID),
                    eq("team_name", team),
                    eq("type", "team_submission"))).first();

            if (team_submission == null) {
                Document blankSubmission = new Document()
                        .append("course_id", courseID)
                        .append("assignment_id", assignmentID)
                        .append("team_name", team)
                        .append("type", "team_submission")
                        .append("grade", 0);
                submissionCollection.insertOne(blankSubmission);
            } else {
                List<String> teams_that_graded = team_submission.getList("reviews", String.class);
                if (teams_that_graded == null)
                    throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assigned teams not found for: " + team + "for assignment: " + assignmentID).build());
                int total_points = 0;
                int count_of_reviews_submitted = teams_that_graded.size();
                for (String review : teams_that_graded) {
                    Document team_review = submissionCollection.find(and(
                            eq("course_id", courseID),
                            eq("assignment_id", assignmentID),
                            eq("reviewed_by", review),
                            eq("type", "peer_review_submission"))).first();
                    if (team_review == null) {
                        count_of_reviews_submitted--;
                    } else {
                        if (team_review.get("grade", Integer.class) == null) {
                            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("team: " + review + "'s review has no points.").build());
                        } else {
                            total_points += team_review.get("grade", Integer.class);

                            /* Insert the peer review score into each student in the team in the student collection */
                            List<String> teamMembers = team_review.getList("reviewed_team", String.class);
                            String teamName = team_review.getString("reviewed_team");
                            double grade = team_review.getDouble("grade");
                            String reviewedBy = team_review.getString("reviewed_by");
                            int assignmentId = team_review.getInteger("assignment_id");
                            teamMembers.forEach(studentId -> {
                               Bson studentQuery = eq("student_id", studentId);
                               Document student = studentCollection.find(studentQuery).first();
                               List<Document> peerReviewGrades = student.getList("peer_reviews", Document.class);
                               Document newPeerReview = new Document()
                                       .append("team_name", teamName)
                                       .append("grade", grade)
                                       .append("reviewed_by", reviewedBy)
                                       .append("assignment_id", assignmentId);
                                peerReviewGrades.add(newPeerReview);
                                Bson updateRule = Updates.set("peer_reviews", peerReviewGrades);
                                UpdateOptions options = new UpdateOptions().upsert(true);
                                studentCollection.updateOne(studentQuery, updateRule, options);
                            });
                        }
                    }
                }
                double final_grade = (((double) total_points / count_of_reviews_submitted) / points) * 100;
                final_grade = ((int) (final_grade * 100) / 100.0); //round to the nearest 10th
                submissionCollection.findOneAndUpdate(team_submission, set("grade", final_grade));
                assignmentsCollection.findOneAndUpdate(and(eq("course_id", courseID), eq("assignment_id", assignmentID)), set("grade_finalized", true));

                /* Updating student's grade */
                for (String member : team_submission.getList("members", String.class)) {
                    List<Document> grades = new ArrayList<>();
                    grades.addAll(studentCollection.find(eq("student_id", member)).first().getList("grades", Document.class));
                    Document newAssignmentGrade = new Document()
                            .append("assignment_id", assignmentID)
                            .append("grade", final_grade)
                            .append("team_name", team_submission.getString("team_name"));
                    grades.add(newAssignmentGrade);
                    Bson filter = eq("student_id", member);
                    UpdateOptions options = new UpdateOptions().upsert(true);
                    Bson update = Updates.set("team_submissions", grades);
                    studentCollection.updateOne(filter, update, options);
                }
            }
        }
    }

    public List<Document> getToDosByCourse(String courseID, String studentID) {
        MongoCursor<Document> query = assignmentsCollection.find(eq("course_id", courseID)).iterator();

        if (!query.hasNext())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This course does not exist").build());

        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            Document ifNotSubmitted = submissionCollection.find(and(
                    eq("course_id", courseID),
                    eq("assignment_id", document.get("assignment_id")),
                    eq("type", "team_submission"),
                    eq("members", studentID))).first();
            if (ifNotSubmitted == null)
                assignments.add(document);
        }
        return assignments;
    }

    /**
     * Ensures that a request carried out by a client that needs to access assignment information is actually
     * the professor of the course in context
     * @param securityContext
     * @param courseID
     */
    public void checkProfessor(SecurityContext securityContext, String courseID){
        String professorID = securityContext.getUserPrincipal().getName().split("@")[0];
        Document professorDocument = professorCollection.find(eq("professor_id", professorID)).first();
        if (professorDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("This professor does not exist.").build());
        Document courseDocument = courseCollection.find(Filters.eq("course_id", courseID)).first();
        if(courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("This course does not exist.").build());
        String professorIDActual = courseDocument.getString("professor_id");
        if (!professorIDActual.equals(professorID))  throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity("User principal name doesn't match").build());
    }


    /**
     * Inserts the file data related to a predefined assignment
     * @param submissions
     * @param zipFolder
     */
    public void insertAssignmentFiles(MongoCursor<Document> submissions, String courseID, ZipOutputStream zipFolder) throws IOException {
        while(submissions.hasNext()){
            //get the current submission
            Document currentSubmission = submissions.next();
            //make the file path to save the submission in
            String path;
            String assignmentNameNoSpaces = currentSubmission.getString("assigment_name").replace(" ", "_");
            Binary submission_data = (Binary) currentSubmission.get("submission_data");
            if(currentSubmission.getString("type").equals("team_submission")){
                path = courseID+"/"+assignmentNameNoSpaces+"/"+currentSubmission.getString("team_name")+"/submission/"+currentSubmission.getString("submission_name");
                System.out.println(path);
                ZipEntry curEntry = new ZipEntry(path);
                zipFolder.putNextEntry(curEntry);
                zipFolder.write(submission_data.getData(), 0, submission_data.getData().length);
                zipFolder.closeEntry();
            }else{
                //if it is a peer review, save the data in the folder in both the to/from teams
                String fromTeam = currentSubmission.getString("reviewed_by");
                String toTeam = currentSubmission.getString("reviewed_team");
                //from team first
                path = courseID+"/"+assignmentNameNoSpaces+"/"+fromTeam+"/peer-reviews/given/"+currentSubmission.getString("submission_name");
                ZipEntry curEntryFromTeam = new ZipEntry(path);
                zipFolder.putNextEntry(curEntryFromTeam);
                zipFolder.write(submission_data.getData(), 0, submission_data.getData().length);
                zipFolder.closeEntry();


                //to team last
                path = courseID+"/"+assignmentNameNoSpaces+"/"+toTeam+"/peer-reviews/received/"+currentSubmission.getString("submission_name");
                ZipEntry curEntryToTeam = new ZipEntry(path);
                zipFolder.putNextEntry(curEntryToTeam);
                zipFolder.write(submission_data.getData(), 0, submission_data.getData().length);
                zipFolder.closeEntry();
            }
        }

    }

    /**
     * Add all assignment files related to a specific student
     * @param submissions
     * @param courseID
     * @param studentID
     * @param zipFolder
     * @throws IOException
     */
    public void insertAssignmentFilesStudent(MongoCursor<Document> submissions, String courseID, String studentID, ZipOutputStream zipFolder) throws IOException {
        while(submissions.hasNext()){
            //get the current submission
            Document currentSubmission = submissions.next();
            //check and make sure the submission has the current studentID attached to the submitter. If it does not, just continue the loop
            if(currentSubmission.getString("type").equals("team_submission")){
                List<String> submission_members = currentSubmission.getList("members", String.class);
                if (submission_members.stream().noneMatch(s -> s.equals(studentID))) {
                    continue;
                }
            }else{
                List<String> submission_members = currentSubmission.getList("reviewed_by_members", String.class);
                if (submission_members.stream().noneMatch(s -> s.equals(studentID))) {
                    continue;
                }
            }

            //make the file path to save the submission in
            String path;
            String assignmentNameNoSpaces = currentSubmission.getString("assigment_name").replace(" ", "_");
            Binary submission_data = (Binary) currentSubmission.get("submission_data");
            if(currentSubmission.getString("type").equals("team_submission")){
                path = courseID+"/"+assignmentNameNoSpaces+"/"+currentSubmission.getString("team_name")+"/submission/"+currentSubmission.getString("submission_name");
                System.out.println(path);
                ZipEntry curEntry = new ZipEntry(path);
                zipFolder.putNextEntry(curEntry);
                zipFolder.write(submission_data.getData(), 0, submission_data.getData().length);
                zipFolder.closeEntry();
            }else{
                //if it is a peer review, save the data in the folder in both the to/from teams
                String fromTeam = currentSubmission.getString("reviewed_by");
                String toTeam = currentSubmission.getString("reviewed_team");
                //from team first
                path = courseID+"/"+assignmentNameNoSpaces+"/"+fromTeam+"/peer-reviews/given/"+currentSubmission.getString("submission_name");
                ZipEntry curEntryFromTeam = new ZipEntry(path);
                zipFolder.putNextEntry(curEntryFromTeam);
                zipFolder.write(submission_data.getData(), 0, submission_data.getData().length);
                zipFolder.closeEntry();


                //to team last
                path = courseID+"/"+assignmentNameNoSpaces+"/"+toTeam+"/peer-reviews/received/"+currentSubmission.getString("submission_name");
                ZipEntry curEntryToTeam = new ZipEntry(path);
                zipFolder.putNextEntry(curEntryToTeam);
                zipFolder.write(submission_data.getData(), 0, submission_data.getData().length);
                zipFolder.closeEntry();
            }
        }

    }


    /**
     * Returns a zip file containing all completed assignment submissions
     * @param courseID
     * @return File
     */
    public File aggregateSubmissions(String courseID) throws IOException {
        //make the temporary zip folder
        File tempFile = Files.createTempFile(courseID, ".zip").toFile();
        String tempPath = tempFile.getAbsolutePath();
        ZipOutputStream zipFolder = new ZipOutputStream(new FileOutputStream(tempPath));
        //get a list of all the assignments
        for (Document currentAssignment : assignmentsCollection.find(eq("course_id", courseID))) {
            //get the next assignment related to the current course
            //get this to get the submissions quicker
            Integer assignment_ID = currentAssignment.getInteger("assignment_id");
            //now iterate through the submissions for the assignment and put them in the proper place in the zip folder
            insertAssignmentFiles(submissionCollection.find(eq("assignment_id", assignment_ID)).iterator(), courseID, zipFolder);
        }
        zipFolder.close();

        //return the file with all the assignment data
        return tempFile;
    }

    /**
     * Returns a zip file containing all completed assignment submissions for a particular assignment
     * @param courseID
     * @param assignment_ID
     * @return File
     */
    public File aggregateSubmissions(String courseID, Integer assignment_ID) throws IOException {
        //make the temporary zip folder
        File tempFile = Files.createTempFile(courseID, ".zip").toFile();
        String tempPath = tempFile.getAbsolutePath();
        ZipOutputStream zipFolder = new ZipOutputStream(new FileOutputStream(tempPath));
        insertAssignmentFiles(submissionCollection.find(eq("assignment_id", assignment_ID)).iterator(), courseID, zipFolder);
        zipFolder.close();

        //return the file with all the assignment data
        return tempFile;
    }


    /**
     * Returns a zip file containing all submissions related to the student with the given studentID
     * @param courseID
     * @param studentID
     * @return
     * @throws IOException
     */
    public File aggregateSubmissionsStudent(String courseID, String studentID) throws IOException {
        //make the temporary zip folder
        File tempFile = Files.createTempFile(courseID+studentID, ".zip").toFile();
        String tempPath = tempFile.getAbsolutePath();
        ZipOutputStream zipFolder = new ZipOutputStream(new FileOutputStream(tempPath));
        //get a list of all the assignments
        for (Document currentAssignment : assignmentsCollection.find(eq("course_id", courseID))) {
            //get the next assignment related to the current course
            //get this to get the submissions quicker
            Integer assignment_ID = currentAssignment.getInteger("assignment_id");
            //now iterate through the submissions for the assignment and put them in the proper place in the zip folder
            insertAssignmentFilesStudent(submissionCollection.find(eq("assignment_id", assignment_ID)).iterator(), courseID, studentID, zipFolder);
        }
        zipFolder.close();

        //return the file with all the assignment data
        return tempFile;
    }

    /**
     * Retrieves all assignments pertaining to a specific course.
     *
     * @param courseId id of the course
     */
    public List<Document> getAllCourseAssignmentsAndPeerReviews(String courseId) {

        /* Get all individual grades on assignments for students in the course */
        Document course = courseCollection.find(eq("course_id", courseId)).first();
        if (course == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Course does not exist.").toString());
        }
        List<String> enrolledStudents = course.getList("students", String.class);
        List<Document> submissions = new ArrayList<>();
        for (String studentId : enrolledStudents) {
            List<Document> subs = submissionCollection.find(
                    and(
                            eq("course_id", courseId),
                            (eq("members", studentId)),
                            (eq("type", "team_submission"))
                    )
            ).into(new ArrayList<>());
            for (Document sub : subs) {
                sub.append("student_id", studentId);
                submissions.add(sub);
            }
        }

        /* Get all peer review scores for each team for assignments done by said team */
        List<Document> peerReviews = submissionCollection.find(
                                and(
                                    eq("course_id", courseId),
                                    (eq("type", "peer_review_submission"))
                        )
                ).into(new ArrayList<>());
        submissions.addAll(peerReviews);
        return submissions;
    }

    /**
     * Updates an individual's score for an assignment or specific peer review.
     *
     * @param studentId id of the student whose grade is being updated.
     * @param submissionId the id of the student's submission to be edited.
     * @param newGrade the new grade for the student on the given assignment.
     * @param type indicates if the grade being updated is for an assignment or a peer review.
     * @throws WebApplicationException if the student does not exist or the student has no grade
     *         for the assignment.
     */
    public void updateIndividualGrade(String studentId, int submissionId, String reviewingTeam, String newGrade, String type) {
        /* Find the correct student */
        System.out.println(studentId);
        Bson studentQuery = eq("student_id", studentId);
        Document student = studentCollection.find(studentQuery).first();

        /* Throw an exception if no such student exists. */
        if (student == null) {
            throw new WebApplicationException("Student could not be found.", 300);
        }

        /* Find the correct assignment to update for the student. */
        List<Document> assignmentGrades = student.getList("team_submissions", Document.class);
        List<Document> peerReviewGrades = student.getList("peer_reviews", Document.class);
        Document assignment = null;

        if (type.equalsIgnoreCase("teamSubmission")) {
            for (Document studentAssignment : assignmentGrades) {
                if (studentAssignment.getInteger("assignment_id") == submissionId) {
                    assignmentGrades.remove(studentAssignment);
                    assignment = new Document()
                            .append("assignment_id", studentAssignment.getInteger("assignment_id"))
                            .append("grade", Double.valueOf(newGrade))
                            .append("team_name", studentAssignment.getString("team_name"));
                    assignmentGrades.add(assignment);
                    break;
                }
            }

            /* Throw an exception if the student does not have a grade for the specified assignment */
            if (assignment == null) {
                throw new WebApplicationException("The student does not have a grade for the given assignment.", 402);
            }

            /* Update the student's grade */
            Bson replaceStudentQuery = eq("student_id", studentId);
            UpdateOptions options = new UpdateOptions().upsert(true);
            Bson update = Updates.set("team_submissions", assignmentGrades);
        } else if (type.equalsIgnoreCase("peerReview")) {
            for (Document studentAssignment : peerReviewGrades) {
                if (studentAssignment.getInteger("assignment_id") == submissionId && studentAssignment.getString("reviewed_by").equals(reviewingTeam)) {
                    peerReviewGrades.remove(studentAssignment);
                    assignment = new Document()
                            .append("assignment_id", studentAssignment.getInteger("assignment_id"))
                            .append("grade", Double.valueOf(newGrade))
                            .append("reviewed_by", studentAssignment.getString("reviewed_by"))
                            .append("reviewed_team", studentAssignment.getString("reviewed_team"));
                    peerReviewGrades.add(assignment);
                    break;
                }
            }

            /* Throw an exception if the student does not have a grade for the specified assignment */
            if (assignment == null) {
                throw new WebApplicationException("The student does not have a grade for the given assignment.", 404);
            }

            /* Update the student's grade */
            Bson replaceStudentQuery = eq("student_id", studentId);
            UpdateOptions options = new UpdateOptions().upsert(true);
            Bson update = Updates.set("peer_reviews", peerReviewGrades);
            studentCollection.updateOne(replaceStudentQuery, update, options);
        } else {
            throw new WebApplicationException("Invalid type given.", 400);
        }
    }

    /**
     * Updates a team's grade for either an assignment or an individual peer review.
     *
     * @param reviewedTeamName the name of the team that was reviewed
     * @param reviewingTeamName the name of the team who completed the peer review (if applicable)
     * @param assignmentId id of the assignment whose grade is to be edited
     * @param newGrade the new grade for the submission
     * @param type indicating if it is a team submission or peer review to be edited
     */
    public void updateTeamGrade(String reviewedTeamName, String reviewingTeamName, int assignmentId, String newGrade, String type) {
        /* Find the correct submission */
        Bson assignmentQuery = null;
        if (type.equalsIgnoreCase("teamSubmission")) {
            assignmentQuery = and(
                    eq("team_name", reviewedTeamName),
                    eq("assignment_id", assignmentId),
                    eq("type", "team_submission")
            );
        } else if (type.equalsIgnoreCase("peerReview")) {
            assignmentQuery = and(
                    eq("reviewed_by", reviewingTeamName),
                    eq("reviewed_team", reviewedTeamName),
                    eq("assignment_id", assignmentId),
                    eq("type", "peer_review_submission")
            );
        } else {
            throw new WebApplicationException("Invalid type given.", 400);
        }
        Document assignment = submissionCollection.find(assignmentQuery).first();

        /* Throw an exception if the given submission does not exist */
        if (assignment == null) {
            throw new WebApplicationException("No such assignment exists.", 303);
        }

        /* Update the grade in the submissions collection */
        Bson submissionUpdate = Updates.set("grade", newGrade);
        submissionCollection.updateOne(assignmentQuery, submissionUpdate, new UpdateOptions().upsert(true));

        /* Update the grade for each student in the students collection */
        if (type.equalsIgnoreCase("teamSubmission")) {
            List<String> teamMembers = assignment.getList("members", String.class);
            teamMembers.forEach(teamMemberId -> {
                Bson studentQuery = eq("student_id", teamMemberId);
                Document student = studentCollection.find(studentQuery).first();
                assert student != null;
                List<Document> teamSubmissionGrades = student.getList("team_submissions", Document.class);
                Document teamSubmissionDocument = null;
                for (Document teamSubmission : teamSubmissionGrades) {
                    if (teamSubmission.getInteger("assignment_id") == assignmentId && teamSubmission.getString("team_name").equals(reviewedTeamName)) {
                        teamSubmissionGrades.remove(teamSubmission);
                        teamSubmissionDocument = new Document()
                                .append("assignment_id", teamSubmission.getInteger("assignment_id"))
                                .append("grade", Double.valueOf(newGrade))
                                .append("team_name", teamSubmission.getString("team_name"));
                        teamSubmissionGrades.add(teamSubmissionDocument);
                        break;
                    }
                }
                if (teamSubmissionDocument == null) {
                    throw new WebApplicationException("This student does not have a grade for the specified assignment", 301);
                }
                Bson update = Updates.set("team_submissions", teamSubmissionGrades);
                UpdateOptions options = new UpdateOptions().upsert(true);
                studentCollection.updateOne(studentQuery, update, options);
            });
        } else {
            List<String> teamMembers = assignment.getList("reviewed_team_members", String.class);
            teamMembers.forEach(teamMemberId -> {
                Bson studentQuery = eq("student_id", teamMemberId);
                Document student = studentCollection.find(studentQuery).first();
                assert student != null;
                List<Document> peerReviewGrades = student.getList("peer_reviews", Document.class);
                Document peerReviewDocument = null;
                for (Document peerReview : peerReviewGrades) {
                    System.out.println("new one");
                    System.out.println("Student ID: " + teamMemberId);

                    System.out.println("Assignment ID");
                    System.out.println(peerReview.getInteger("assignment_id") == assignmentId);
                    System.out.println(assignmentId);
                    System.out.println(peerReview.getInteger("assignment_id"));

                    System.out.println("Reviewed By");
                    System.out.println(peerReview.getString("reviewed_by").equalsIgnoreCase(reviewingTeamName));
                    System.out.println(reviewingTeamName);
                    System.out.println(peerReview.getString("reviewed_by"));

                    System.out.println("Team name");
                    System.out.println(peerReview.getString("reviewed_team").equalsIgnoreCase(reviewedTeamName));
                    System.out.println(reviewedTeamName);
                    System.out.println(peerReview.getString("reviewed_team"));

                    if ((peerReview.getInteger("assignment_id") == assignmentId) && (peerReview.getString("reviewed_by").equalsIgnoreCase(reviewingTeamName))
                            && (peerReview.getString("reviewed_team").equalsIgnoreCase(reviewedTeamName))) {
                        peerReviewGrades.remove(peerReview);
                        peerReviewDocument = new Document()
                                .append("assignment_id", peerReview.getInteger("assignment_id"))
                                .append("grade", Double.valueOf(newGrade))
                                .append("team_name", peerReview.getString("reviewed_team"))
                                .append("reviewed_by", reviewingTeamName);
                        peerReviewGrades.add(peerReviewDocument);
                        break;
                    }
                }
                if (peerReviewDocument == null) {
                    throw new WebApplicationException("This student does not have a grade for the specified assignment", 300);
                }
             Bson update = Updates.set("peer_reviews", peerReviewGrades);
                UpdateOptions options = new UpdateOptions().upsert(true);
                studentCollection.updateOne(studentQuery, update, options);
            });
        }
    }
}