package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.*;
import java.util.*;
import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;
import        com.mongodb.client.FindIterable;

public class PeerReviewAssignmentInterface {
    private final MongoCollection<Document> teamCollection;
    private final MongoCollection<Document> assignmentCollection;
    private final MongoCollection<Document> submissionsCollection;
    private final MongoCollection<Document> studentCollection;
    private final MongoCollection<Document> professorCollection;

    static ConcurrentHashMap<String, Boolean> peerReviewLock = new ConcurrentHashMap<String, Boolean>();
    MongoDatabase assignmentDB;

    public PeerReviewAssignmentInterface() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            MongoDatabase teamDB = databaseManager.getTeamDB();
            assignmentDB = databaseManager.getAssignmentDB();
            teamCollection = teamDB.getCollection("teams");
            assignmentCollection = assignmentDB.getCollection("assignments");
            submissionsCollection = assignmentDB.getCollection("submissions");
            professorCollection = databaseManager.getProfessorDB().getCollection("professors");
            studentCollection = databaseManager.getStudentDB().getCollection("students");

        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
        }
    }

    public void addPeerReviewSubmission(String course_id, int assignment_id, String srcTeamName, String destinationTeam, String fileName, int grade, InputStream fileData) throws IOException {
        Document reviewedByTeam = teamCollection.find(eq("team_id", srcTeamName)).first();
        Document reviewedTeam = teamCollection.find(eq("team_id", destinationTeam)).first();
        Document assignment = assignmentCollection.find(and(eq("course_id", course_id), eq("assignment_id", assignment_id))).first();
        if (assignment == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("this assignment was not found in this course").build());
        if (reviewedByTeam == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("no team for this student").build());
        if (reviewedTeam == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("no team for this student").build());
        if (reviewedByTeam.getList("team_members", String.class) == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Members not defined in team").build());
        if (reviewedTeam.getList("team_members", String.class) == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Members not defined in team").build());
        if (reviewedByTeam.get("team_id", String.class) == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("team_id not defined").build());
        if (reviewedTeam.get("team_id", String.class) == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("team_id not defined").build());

        String reg = "/";
        String path = "assignments" + reg + course_id + reg + assignment_id + reg + "peer-review-submissions";
        Document new_submission = new Document()
                .append("course_id", course_id)
                .append("assignment_id", assignment_id)
                .append("assigment_name", assignment.getString("assignment_name"))
                .append("submission_name", fileName)
                .append("submission_data", Base64.getDecoder().decode(new String(fileData.readAllBytes())))
                .append("reviewed_by", reviewedByTeam.getString("team_id"))
                .append("reviewed_by_members", reviewedByTeam.getList("team_members", String.class))
                .append("reviewed_team", reviewedTeam.getString("team_id"))
                .append("reviewed_team_members", reviewedTeam.getList("team_members", String.class))
                .append("type", "peer_review_submission")
                .append("peer_review_due_date", assignment.get("peer_review_due_date"))
                .append("due_date", assignment.getString("due_date"))
                .append("grade", grade);
        List<String> teamMembers = reviewedTeam.getList("team_members", String.class);
        for (String member : teamMembers) {
            Document newStudentSubmission = new Document()
                    .append("assignment_id", assignment_id)
                    .append("reviewed_team", reviewedTeam.getString("team_id"))
                    .append("reviewed_by", reviewedByTeam.getString("team_id"))
                    .append("grade", grade);
            Bson studentQuery = eq("student_id", member);
            Document student = studentCollection.find(studentQuery).first();
            List<Document> peerReviews = student.getList("peer_reviews", Document.class);
            peerReviews.add(newStudentSubmission);
            Bson update = Updates.set("peer_reviews", peerReviews);
            UpdateOptions options = new UpdateOptions().upsert(true);
            studentCollection.updateOne(studentQuery, update, options);
        }
        //wait for the lock to be dropped.
        //key is assignment_id+reviewed_by_team_id+reviewed_team+"peer_review_submission"
        while (peerReviewLock.containsKey(assignment_id + reviewedByTeam.getString("team_id") + reviewedTeam.getString("team_id") + "peer_review_submission"))
            ;
        //lock the submission
        peerReviewLock.put(assignment_id + reviewedByTeam.getString("team_id") + reviewedTeam.getString("team_id") + "peer_review_submission", true);
        if (submissionsCollection.find(new_submission).iterator().hasNext()) {
            //let go of the lock
            peerReviewLock.remove(assignment_id + reviewedByTeam.getString("team_id") + reviewedTeam.getString("team_id") + "peer_review_submission");
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("submission already exists").build());
        } else submissionsCollection.insertOne(new_submission);
        //remove the lock
        peerReviewLock.remove(assignment_id + reviewedByTeam.getString("team_id") + reviewedTeam.getString("team_id") + "peer_review_submission");

        // Store reviewed_team_members and teams in "teams" collection
        teamCollection.updateOne(
                eq("team_id", reviewedTeam.getString("team_id")),
                new Document("$set", new Document()
                        .append("reviewed_team", reviewedByTeam.getString("team_id"))
                        .append("reviewed_members", reviewedByTeam.getList("team_members", String.class))));
        System.out.println(reviewedTeam.getString("team_id"));
        System.out.println(reviewedByTeam.getList("team_members", String.class));
        addCompletedTeam(course_id, assignment_id, srcTeamName, destinationTeam);

    }

    public void addCompletedTeam(String courseID, int assignmentID, String sourceTeam, String targetTeam) {

        Document assignmentDocument = assignmentCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        if (assignmentDocument == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to find assignment. As a result the assignment could not update the completed_teams").build());

        Map<String, List<String>> completedTeams = (Map<String, List<String>>) assignmentDocument.get("completed_teams");
        Map<String, List<String>> finalTeams = completedTeams;
        List<String> temp = completedTeams.get(sourceTeam);
        temp.add(targetTeam);
        finalTeams.put(sourceTeam, temp);
        assignmentDocument.replace("completed_teams", completedTeams, finalTeams);
        assignmentCollection.replaceOne(and(eq("course_id", courseID), eq("assignment_id", assignmentID)), assignmentDocument);
        assignmentDocument = assignmentCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        completedTeams = (Map<String, List<String>>) assignmentDocument.get("completed_teams");
        int currentNumOfReviews = 0;
        for (Map.Entry<String, List<String>> entry : completedTeams.entrySet()) {
            List<String> list = entry.getValue();
            for (String s : list) {
                if (s.equals(targetTeam))
                    currentNumOfReviews++;
            }
        }
        if (currentNumOfReviews == (int) assignmentDocument.get("reviews_per_team")) {
            //makeFinalGrade(courseID, assignmentID, targetTeam);
            redoneMakeFinalGrade(courseID, assignmentID, targetTeam);
        }

        //check if all team grades have been finalized
        int num_of_reviews_needed = completedTeams.keySet().size()*assignmentDocument.getInteger("reviews_per_team");
        int total_num_of_reviews = 0;
        for (Map.Entry<String, List<String>> entry : completedTeams.entrySet()) {
            List<String> list = entry.getValue();
            total_num_of_reviews+=list.size();
        }


        if (total_num_of_reviews==num_of_reviews_needed) {
            assignmentCollection.findOneAndUpdate(and(eq("course_id", courseID), eq("assignment_id", assignmentID)), set("grade_finalized", true));
        }
    }

    public String downloadFinishedPeerReviewName(String courseID, int assignmentID, String srcTeamName, String destTeamName) {
        Document submittedPeerReview = submissionsCollection.find(and(eq("type", "peer_review_submission"), eq("assignment_id", assignmentID), eq("course_id", courseID), eq("reviewed_by", srcTeamName), eq("reviewed_team", destTeamName))).first();
        if (submittedPeerReview == null)
            throw new WebApplicationException("No peer review from team " + srcTeamName + " for " + destTeamName);
        return (String) submittedPeerReview.get("submission_name");

    }

    public Binary downloadFinishedPeerReview(String courseID, int assignmentID, String srcTeamName, String destTeamName) {
        Document submittedPeerReview = submissionsCollection.find(and(eq("type", "peer_review_submission"), eq("assignment_id", assignmentID), eq("course_id", courseID), eq("reviewed_by", srcTeamName), eq("reviewed_team", destTeamName))).first();
        if (submittedPeerReview == null)
            throw new WebApplicationException("No peer review from team " + srcTeamName + " for " + destTeamName);
        return (Binary) submittedPeerReview.get("submission_data");

    }

    public List<Document> getUsersReviewedAssignment(String courseID, int assignmentID, String studentID) {
        MongoCursor<Document> query = submissionsCollection.find(and(eq("course_id", courseID),
                eq("assignment_id", assignmentID),
                eq("reviewed_team_members", studentID),
                eq("type", "peer_review_submission"))).iterator();
        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            assignments.add(document);
        }
        if (assignments.isEmpty())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist").build());
        return assignments;
    }

    public List<Document> getUsersReviewedAssignment(String courseID, String studentID) {
        MongoCursor<Document> query = submissionsCollection.find(and(eq("course_id", courseID),
                eq("reviewed_team_members", studentID),
                eq("type", "peer_review_submission"))).iterator();
        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            assignments.add(document);
        }
        if (assignments.isEmpty())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist").build());
        return assignments;
    }

    public List<Document> getAssignmentsReviewedByUser(String courseID, String studentID) {
        MongoCursor<Document> query = submissionsCollection.find(and(
                eq("course_id", courseID),
                eq("reviewed_by_members", studentID),
                eq("type", "peer_review_submission"))).iterator();
        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            assignments.add(document);
        }
        if (assignments.isEmpty())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist").build());
        return assignments;
    }

    public List<String> getAssignedTeams(String courseID, int assignmentID, String teamName) {
        Document assignmentDocument = assignmentCollection.find(and(
                eq("course_id", courseID),
                eq("assignment_id", assignmentID))).first();
        if (assignmentDocument == null) throw new WebApplicationException("Course/Assignment ID does not exist.");
        Document teamAssignmentDocument = (Document) assignmentDocument.get("assigned_teams");
        return teamAssignmentDocument.getList(teamName, String.class);
    }

    public List<String> filterBySubmitted(List<String> allTeams, String course_id, int assignment_id) {
        List<String> finalTeams = new ArrayList<>();
        for (String teamName : allTeams) {
            if (submissionsCollection.find(and(
                    eq("course_id", course_id),
                    eq("assignment_id", assignment_id),
                    eq("team_name", teamName))).iterator().hasNext()) {
                finalTeams.add(teamName);
            }
        }
        return finalTeams;
    }

    public List<String> getCourseTeams(String courseID) {
        ArrayList<String> teamNames = new ArrayList<>();
        for (Document teamDocument : teamCollection.find(eq("course_id", courseID))) {
            String teamName = (String) teamDocument.get("team_id");
            teamNames.add(teamName);
        }
        return teamNames;
    }

    public Document addAssignedTeams(Map<String, List<String>> peerReviewAssignments, String courseID, int assignmentID) {

        Document assignmentDocument = assignmentCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        if (assignmentDocument == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to add assigned teams").build());

        Document doc = new Document();
        Document completedTeamsDoc = new Document();
        for (String team : peerReviewAssignments.keySet()) {
            doc.put(team, peerReviewAssignments.get(team));
            completedTeamsDoc.put(team, new ArrayList<>());
        }
        assignmentCollection.updateOne(assignmentDocument, set("assigned_teams", doc));
        assignmentCollection.updateOne(assignmentDocument, set("completed_teams", completedTeamsDoc));

        return doc;
    }

    public void addAllTeams(List<String> allTeams, String courseID, int assignmentID, int reviewsPerTeam) {
        Document result = assignmentCollection.findOneAndUpdate(and(eq("course_id", courseID), eq("assignment_id", assignmentID)), set("all_teams", allTeams));
        if (result == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to set all teams to assignment").build());
        result = assignmentCollection.findOneAndUpdate(and(eq("course_id", courseID), eq("assignment_id", assignmentID)), set("reviews_per_team", reviewsPerTeam));
        if (result == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to add number of reviews per team to database document").build());
    }

    public void addDistroToSubmissions(Map<String, List<String>> distro, String course_id, int assignment_id) {
        for (String team : distro.keySet()) {
            Document result = submissionsCollection.findOneAndUpdate(and(
                            eq("course_id", course_id),
                            eq("assignment_id", assignment_id),
                            eq("team_name", team), eq("type", "team_submission")),
                    set("reviews", distro.get(team)));
            if (result == null)
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to add teams to submission: " + team).build());
        }
    }

    public List<String> getTeams(String courseID, int assignmentID) {
        Document assignment = assignmentCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        if (assignment == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No assignment found.").build());
        List<String> teams = assignment.getList("all_teams", String.class);
        if (teams == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("All teams not found for: " + courseID).build());
        return teams;
    }


    public Document redoneGetTeamGrades(String courseID, int assignmentID, String teamName) {

        List<Document> results = new ArrayList<>();

        FindIterable<Document> iterable = submissionsCollection.find(and(
                eq("reviewed_team", teamName),
                eq("course_id", courseID),
                eq("assignment_id", assignmentID),
                eq("type", "peer_review_submission")
        ));

        iterable.into(results);
        System.out.println(results);
        //for each grade this team received
        List<Document> teams = new ArrayList<>();
        for (Document teamGradesReceived : results) {
            //grabs the team that assigned this team the grade
            Document document = new Document().append("team_name", teamGradesReceived.get("reviewed_by"));
            if (results == null) document.append("grade_given", "pending");
            else document.append("grade_given", teamGradesReceived.getInteger("grade"));
            teams.add(document);
        }

        return new Document().append("teams", teams);
    }

    @Deprecated
    public Document getTeamGrades(String courseID, int assignmentID, String teamName) {
        Document team = submissionsCollection.find(and(
                eq("course_id", courseID),
                eq("assignment_id", assignmentID),
                eq("team_name", teamName),
                eq("type", "team_submission"))).first();
        if (team == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Team not found.").build());
        List<String> reviews = team.getList("reviews", String.class);
        List<Document> teams = new ArrayList<>();
        for (String reviewTeam : reviews) {
            Document result = submissionsCollection.find(and(
                    eq("reviewed_by", reviewTeam),
                    eq("reviewed_team", teamName),
                    eq("course_id", courseID),
                    eq("assignment_id", assignmentID),
                    eq("type", "peer_review_submission")
            )).first();
            Document document = new Document().append("team_name", reviewTeam);
            if (result == null) document.append("grade_given", "pending");
            else document.append("grade_given", result.getInteger("grade"));
            teams.add(document);
        }
        return new Document().append("teams", teams);
    }
    public Document professorUpdate(String courseID, int assignmentID, String teamName, int grade) {
        Document team = submissionsCollection.findOneAndUpdate(and(
                        eq("course_id", courseID),
                        eq("assignment_id", assignmentID),
                        eq("team_name", teamName),
                        eq("type", "team_submission")),
                set("grade", grade));
        if (team == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Team not found.").build());
        return team;
    }

    //this is a method to test for creating a new makeFinalGrade method functionality, currently in testing
    public void redoneMakeFinalGrade(String courseID, int assignmentID, String teamName) {
        //need to know what the grade was 'out of' for rounding/final grade purposes
        Document assignment = assignmentCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        if (assignment == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment not found.").build());
        int points = assignment.getInteger("points");

        //get iterable of teams that have graded this team
        List<Document> results = new ArrayList<>();

        FindIterable<Document> iterable = submissionsCollection.find(and(
                eq("reviewed_team", teamName),
                eq("course_id", courseID),
                eq("assignment_id", assignmentID),
                eq("type", "peer_review_submission")
        ));

        iterable.into(results);
        System.out.println(results);

        if (iterable == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assigned teams not found for: " + teamName + "for assignment: " + assignmentID).build());

        int count_of_reviews_submitted = results.size();
        int total_points = 0;
        //for each grade this team received
        for (Document teamGradesReceived : results) {
            total_points += teamGradesReceived.get("grade", Integer.class);
        }

        DecimalFormat tenth = new DecimalFormat("0.##");
        double final_grade = Double.parseDouble(tenth.format((((double) total_points / count_of_reviews_submitted) / points) * 100));//round


        Document team_submission = submissionsCollection.find(and(
                eq("course_id", courseID),
                eq("assignment_id", assignmentID),
                eq("team_name", teamName),
                eq("type", "team_submission"))).first();
        submissionsCollection.findOneAndUpdate(team_submission, set("grade", final_grade));

        //set the peer review grades for the student.
        List<String> teams_that_graded = team_submission.getList("reviews", String.class);
        for (String review : teams_that_graded) {
            Document team_review = submissionsCollection.find(and(
                    eq("course_id", courseID),
                    eq("assignment_id", assignmentID),
                    eq("reviewed_by", review),
                    eq("reviewed_team", teamName),
                    eq("type", "peer_review_submission"))).first();
            if (team_review == null) {
                count_of_reviews_submitted--;
            } else {
                if (team_review.get("grade", Integer.class) == null) {
                    throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("team: " + review + "'s review has no points.").build());
                } else {
                    total_points += team_review.get("grade", Integer.class);
                    for (String teamMember : team_review.getList("reviewed_team_members", String.class)) {
                        Document newPeerReview = new Document()
                                .append("course_id", courseID)
                                .append("grade", team_review.getInteger("grade"))
                                .append("team_name", teamName);
                        List<Document> peerReviews = studentCollection.find(eq("student_id", teamMember)).first().getList("peer_reviews", Document.class);
                        peerReviews.add(newPeerReview);
                        Bson studentQuery = eq("student_id", teamMember);
                        Bson update = Updates.set("team_submissions", peerReviews);
                        UpdateOptions options = new UpdateOptions().upsert(true);
                        studentCollection.updateOne(studentQuery, update, options);
                    }
                }
            }
        }

        //set the final grade
        for (String member : team_submission.getList("members", String.class)) {
            List<Document> grades = new ArrayList<Document>();
            grades.addAll(studentCollection.find(eq("student_id", member)).first().getList("team_submissions", Document.class));
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


    @Deprecated
    public void makeFinalGrade(String courseID, int assignmentID, String teamName) {
        Document assignment = assignmentCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        if (assignment == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment not found.").build());
        int points = assignment.getInteger("points");
        Document team_submission = submissionsCollection.find(and(
                eq("course_id", courseID),
                eq("assignment_id", assignmentID),
                eq("team_name", teamName),
                eq("type", "team_submission"))).first();

        List<String> teams_that_graded = team_submission.getList("reviews", String.class);


        if (teams_that_graded == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assigned teams not found for: " + teamName + "for assignment: " + assignmentID).build());
        int total_points = 0;
        int count_of_reviews_submitted = teams_that_graded.size();

        //my code
        String[] temp = new String[count_of_reviews_submitted];
        int counter = 0;
        for (String teamsThatGraded : teams_that_graded) {
            temp[counter] = teamsThatGraded;
            counter++;
        }
        int currentTeam = 0;
        for (String review : teams_that_graded) {
            Document team_review = submissionsCollection.find(and(
                    eq("course_id", courseID),
                    eq("assignment_id", assignmentID),
                    eq("reviewed_by", review),
                    eq("reviewed_team", teamName),
                    eq("type", "peer_review_submission"))).first();
            if (team_review == null) {
                count_of_reviews_submitted--;
            } else {
                if (team_review.get("grade", Integer.class) == null) {
                    throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("team: " + review + "'s review has no points.").build());
                } else {
                    total_points += team_review.get("grade", Integer.class);
                    for (String teamMember : team_review.getList("reviewed_team", String.class)) {
                        Document newPeerReview = new Document()
                                .append("course_id", courseID)
                                .append("grade", team_review.getInteger("grade"))
                                .append("team_name", teamName);
                        List<Document> peerReviews = studentCollection.find(eq("student_id", teamMember)).first().getList("peer_reviews", Document.class);
                        peerReviews.add(newPeerReview);
                        Bson studentQuery = eq("student_id", teamMember);
                        Bson update = Updates.set("team_submissions", peerReviews);
                        UpdateOptions options = new UpdateOptions().upsert(true);
                        studentCollection.updateOne(studentQuery, update, options);
                    }
                }
            }
            currentTeam++;
        }
        DecimalFormat tenth = new DecimalFormat("0.##");
        double final_grade = Double.parseDouble(tenth.format((((double) total_points / count_of_reviews_submitted) / points) * 100));//round

        submissionsCollection.findOneAndUpdate(team_submission, set("grade", final_grade));
        for (String member : team_submission.getList("members", String.class)) {
            List<Document> grades = new ArrayList<Document>();
            grades.addAll(studentCollection.find(eq("student_id", member)).first().getList("team_submissions", Document.class));
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

    public void makeFinalGrades(String courseID, int assignmentID) {
        Document assignment = assignmentCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        if (assignment == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment not found.").build());
        List<String> allTeams = assignment.getList("all_teams", String.class);
        int points = assignment.getInteger("points");
        for (String team : allTeams) {
            Document team_submission = submissionsCollection.find(and(
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
                submissionsCollection.insertOne(blankSubmission);
            } else {
                List<String> teams_that_graded = team_submission.getList("reviews", String.class);
                if (teams_that_graded == null)
                    throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assigned teams not found for: " + team + "for assignment: " + assignmentID).build());
                int total_points = 0;
                int count_of_reviews_submitted = teams_that_graded.size();
                for (String review : teams_that_graded) {
                    Document team_review = submissionsCollection.find(and(
                            eq("course_id", courseID),
                            eq("assignment_id", assignmentID),
                            eq("reviewed_by", review),
                            //eq("reviewed_team", teamName),
                            eq("type", "peer_review_submission"))).first();
                    if (team_review == null) {
                        count_of_reviews_submitted--;
                    } else {
                        if (team_review.get("grade", Integer.class) == null) {
                            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("team: " + review + "'s review has no points.").build());
                        } else {
                            total_points += team_review.get("grade", Integer.class);

                        }
                    }
                }
                DecimalFormat tenth = new DecimalFormat("0.##");
                double final_grade = Double.parseDouble(tenth.format((((double) total_points / count_of_reviews_submitted) / points) * 100));//round

                submissionsCollection.findOneAndUpdate(team_submission, set("grade", final_grade));
                assignmentCollection.findOneAndUpdate(and(eq("course_id", courseID), eq("assignment_id", assignmentID)), set("grade_finalized", true));
            }
        }
    }

    public Document getGradeForTeam(String courseID, int assignmentID, String teamName) {
        Document result = submissionsCollection.find(and(
                eq("course_id", courseID),
                eq("assignment_id", assignmentID),
                eq("team_name", teamName),
                eq("type", "team_submission"))).first();
        if (result == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Team: " + teamName + " was not found for assignment").build());
        if (result.getInteger("grade") == null) return new Document("grade", -1);
        else return new Document("grade", result.getInteger("grade"));
    }
    /**
     * The method gets the team names and their members from teamCollection and gets the final grade from submissionsCollection,
     * then it returns a document object containg individual student and their grade.
     */
    public Document getGradeForStudent(String courseID, int assignmentID, String teamID, String studentID) {
        Document reviewedTeam = teamCollection.find(eq("reviewed_team", teamID)).first();
        //System.out.println(reviewedTeam);
        if (reviewedTeam == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Reviewed Team not found.").build());
        List<String> teamMembers = reviewedTeam.getList("reviewed_members", String.class);
        //System.out.println(teamMembers);
        if (teamMembers == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No students found.").build());
        if (!teamMembers.contains(studentID))
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Student not found.").build());
        Document result = submissionsCollection.find(and(
                eq("course_id", courseID),
                eq("assignment_id", assignmentID),
                eq("team_name", teamID),
                eq("type", "team_submission")
        )).first();
        if (result == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No submission found.").build());
        } else {
            Document gradeDoc = new Document()
                    .append("studentID", studentID)
                    .append("grade", result.getDouble("grade"));
            //
            // System.out.println(gradeDoc);
            return gradeDoc;
        }
    }


    //redone matrix outlier detection
    public Document getMatrixOfOutlierAndGrades(String courseID, int assignmentID) {

        //get all teams that were assigned this assignment in the course(will be the 'iterable')
        Document assignment = assignmentCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        if (assignment == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment not found.").build());
        //grab all teams assigned this
        List<String> allTeams = assignment.getList("all_teams", String.class);


        Document matrixHolder = new Document();
        //for every team assigned this assignment
        for (String individialTeam : allTeams) {

            //for each team, we will query the DB for a list of documents where they have received a review
            //get iterable of teams that have graded this team
            List<Document> teamsThatReviewedThisTeam = new ArrayList<>();
            FindIterable<Document> iterable = submissionsCollection.find(and(
                    eq("reviewed_team", individialTeam),
                    eq("course_id", courseID),
                    eq("assignment_id", assignmentID),
                    eq("type", "peer_review_submission")
            ));
            //put iterable contents into array list
            iterable.into(teamsThatReviewedThisTeam);

            //we then iterate through this document, grabbing the grade and then determining if that grade
            //is an outlier, if it is append a true flag to that grade, else append false

            //document to hold the team that graded and the grade
            Document gradeHolder = new Document();
            int totalGrade = 0;
            double averageGradeReceived;
            int numTeamsReviewed = 0; //= teamsThatReviewedThisTeam.size();
            for (Document respectiveGradesReceived : teamsThatReviewedThisTeam) {
                Document gradeToOutlier = new Document();
                //grab the grade received respectively
                int gradeReceived = respectiveGradesReceived.get("grade", Integer.class);
                totalGrade += gradeReceived;
                numTeamsReviewed++;

                //then find every team that graded them(should be a base function call already made inside this file)
                Document matrixOfGrades = new Document();

                String teamThatGraded = respectiveGradesReceived.get("reviewed_by", String.class);
                //if the value is an outlier, mark true, else false
                if (isOutlier(courseID, assignmentID, gradeReceived)) {
                    gradeToOutlier.append(String.valueOf(gradeReceived), true);
                } else {
                    gradeToOutlier.append(String.valueOf(gradeReceived), false);
                }

                gradeHolder.append(teamThatGraded, gradeToOutlier);
            }

            if (numTeamsReviewed == teamsThatReviewedThisTeam.size()) {
                double average = (double) totalGrade / (double) numTeamsReviewed;
                gradeHolder.append("Average Grade Received", new Document(String.valueOf(average), isOutlier(courseID, assignmentID, average)));
            }


            matrixHolder.append(individialTeam, gradeHolder);

        }


        //now that we have the grades and average grades received, we can easily qeury to find the grades
        //given using the mistaken query for iterating over the reviews array

        List<Document> getEachAssignment = new ArrayList<>();
        FindIterable<Document> iterable = submissionsCollection.find(and(
                eq("course_id", courseID),
                eq("assignment_id", assignmentID),
                eq("type", "team_submission")
        ));
        //put iterable contents into array list
        iterable.into(getEachAssignment);

        //document to hold grades given
        int numTeamsReviewed = 0;
        int sumOfTeamReviewGradesGiven = 0;
        Document gradesGivenHolder = new Document();
        //List<String> allTeams = assignment.getList("all_teams", String.class);
        for (Document individialTeam : getEachAssignment) {

            //List<String> reviews = individialTeam.getList("reviews", String.class);
            List<Document> individualReviews = new ArrayList<>();
            FindIterable<Document> getReviews = submissionsCollection.find(and(
                    eq("course_id", courseID),
                    eq("assignment_id", assignmentID),
                    eq("type", "peer_review_submission"),
                    eq("reviewed_by", individialTeam.get("team_name"))
            ));
            //put iterable contents into array list
            getReviews.into(individualReviews);

            int totalGradesGiven = 0;
            int counter = 0;
            Document indGradesHolder = new Document();
            for (Document review : individualReviews) {
                totalGradesGiven += review.get("grade", Integer.class);
                counter++;
            }
            double average = (double) totalGradesGiven / (double) counter;
            if (isOutlier(courseID, assignmentID, average))
                indGradesHolder.append(String.valueOf(average), true);
            else
                indGradesHolder.append(String.valueOf(average), false);

            String teamName = (String) individialTeam.get("team_name");
            //append to overall grades holder
            gradesGivenHolder.append(teamName, indGradesHolder);

        }

        matrixHolder.append("Average Grades Given", gradesGivenHolder);
        //   matrixHolder.append("Average Grade Given", gradeHolder);

        //create document to then append to the matrix doc(for grades given averages)
//        for (String key : temp.keySet()) {
//            //first calculate average
//            double average = (double) teamsToGradesGiven.get(assignmentNumber).get(key) / (double) teamsToCountOfReviews.get(assignmentNumber).get(key);
//        }
        return matrixHolder;
    }


    //redone outlier detection over time
    public Document getAllPotentialOutliersAndGrades(String courseID) {

        //grab all of the assignments currently finished for the course

        //must increment at end of each loop
        List<Document> allOutliers = new ArrayList<>();
        FindIterable<Document> iter = assignmentCollection.find();
        iter.into(allOutliers);

        Document allPotentialOutliers = new Document();
        for (Document eachAssignment : allOutliers) {
            Document eachMatrixHolder = new Document();

            int assignmentID = eachAssignment.get("assignment_id", Integer.class);

            //get all teams that were assigned this assignment in the course(will be the 'iterable')
            Document assignment = assignmentCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
            if (assignment == null)
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment not found.").build());
            //grab all teams assigned this
            List<String> allTeams = assignment.getList("all_teams", String.class);


            Document matrixHolder = new Document();
            //for every team assigned this assignment
            for (String individialTeam : allTeams) {

                //for each team, we will query the DB for a list of documents where they have received a review
                //get iterable of teams that have graded this team
                List<Document> teamsThatReviewedThisTeam = new ArrayList<>();
                FindIterable<Document> iterable = submissionsCollection.find(and(
                        eq("reviewed_team", individialTeam),
                        eq("course_id", courseID),
                        eq("assignment_id", assignmentID),
                        eq("type", "peer_review_submission")
                ));
                //put iterable contents into array list
                iterable.into(teamsThatReviewedThisTeam);

                //we then iterate through this document, grabbing the grade and then determining if that grade
                //is an outlier, if it is append a true flag to that grade, else append false

                //document to hold the team that graded and the grade
                Document gradeHolder = new Document();
                int totalGrade = 0;
                double averageGradeReceived;
                int numTeamsReviewed = 0; //= teamsThatReviewedThisTeam.size();
                for (Document respectiveGradesReceived : teamsThatReviewedThisTeam) {
                    Document gradeToOutlier = new Document();
                    //grab the grade received respectively
                    int gradeReceived = respectiveGradesReceived.get("grade", Integer.class);
                    totalGrade += gradeReceived;
                    numTeamsReviewed++;


                    String teamThatGraded = respectiveGradesReceived.get("reviewed_by", String.class);
                    //if the value is an outlier, mark true, else false
                    if (isOutlier(courseID, gradeReceived)) {
                        gradeToOutlier.append(String.valueOf(gradeReceived), true);
                    } else {
                        gradeToOutlier.append(String.valueOf(gradeReceived), false);
                    }

                    gradeHolder.append(teamThatGraded, gradeToOutlier);
                }

                if (numTeamsReviewed == teamsThatReviewedThisTeam.size()) {
                    double average = (double) totalGrade / (double) numTeamsReviewed;
                    gradeHolder.append("Average Grade Received", new Document(String.valueOf(average), isOutlier(courseID, average)));
                }


                matrixHolder.append(individialTeam, gradeHolder);

            }


            //now that we have the grades and average grades received, we can easily qeury to find the grades
            //given using the mistaken query for iterating over the reviews array

            List<Document> getEachAssignment = new ArrayList<>();
            FindIterable<Document> iterable = submissionsCollection.find(and(
                    eq("course_id", courseID),
                    eq("assignment_id", assignmentID),
                    eq("type", "team_submission")
            ));
            //put iterable contents into array list
            iterable.into(getEachAssignment);

            //document to hold grades given
            int numTeamsReviewed = 0;
            int sumOfTeamReviewGradesGiven = 0;
            Document gradesGivenHolder = new Document();
            //List<String> allTeams = assignment.getList("all_teams", String.class);
            for (Document individialTeam : getEachAssignment) {

                //List<String> reviews = individialTeam.getList("reviews", String.class);
                List<Document> individualReviews = new ArrayList<>();
                FindIterable<Document> getReviews = submissionsCollection.find(and(
                        eq("course_id", courseID),
                        eq("assignment_id", assignmentID),
                        eq("type", "peer_review_submission"),
                        eq("reviewed_by", individialTeam.get("team_name"))
                ));
                //put iterable contents into array list
                getReviews.into(individualReviews);

                int totalGradesGiven = 0;
                int counter = 0;
                Document indGradesHolder = new Document();
                for (Document review : individualReviews) {
                    totalGradesGiven += review.get("grade", Integer.class);
                    counter++;
                }
                double average = (double) totalGradesGiven / (double) counter;
                if (isOutlier(courseID, average))
                    indGradesHolder.append(String.valueOf(average), true);
                else
                    indGradesHolder.append(String.valueOf(average), false);

                String teamName = (String) individialTeam.get("team_name");
                //append to overall grades holder
                gradesGivenHolder.append(teamName, indGradesHolder);

            }
            //       matrixOfGrades.append(teamForThisAssignment, gradesToOutliers);

        }

        //    matrixHolder.append("Average Grades Given", gradesGivenHolder);

        //to get the grade document we need to go one step further
        //        Document gradesAndBoolean = (Document) valuesOfEachKey.get(subKeySet);

        //    allPotentialOutliers.append(String.valueOf(assignmentID), matrixHolder);

        return allPotentialOutliers;
    }

    //    matrixOfGrades.append("Average Grade Given", gradesHolder);

    //    return matrixOfGrades;
    //}

    /**
     * abstraction method that calls calculate IQR, and uses the values calculated from there
     * to return a boolean value of whether a number is an outlier or not, based on the current
     * grades received for this assignment(this function takes an int to compare)
     */
    public boolean isOutlier(String courseID, double numberToCompare) {
        HashMap<String, Integer> calculatedQuantities = new HashMap<String, Integer>();
        calculatedQuantities = calculateIQR(courseID);
        int Q1 = calculatedQuantities.get("Q1");
        int Q3 = calculatedQuantities.get("Q3");
        int IQR = calculatedQuantities.get("IQR");

        //if value is an outlier
        if ((numberToCompare < (Q1 - (1.5 * IQR))) || (numberToCompare > (Q3 + (1.5 * IQR)))) {
            return true;
        }
        //if its not an outlier
        else {
            return false;
        }
    }

    /**
     * abstraction method that calls calculate IQR, and uses the values calculated from there
     * to return a boolean value of whether a number is an outlier or not, based on the current
     * grades received for this assignment(this function takes an int to compare)
     */
    public boolean isOutlier(String courseID, int numberToCompare) {
        HashMap<String, Integer> calculatedQuantities = new HashMap<String, Integer>();
        calculatedQuantities = calculateIQR(courseID);
        int Q1 = calculatedQuantities.get("Q1");
        int Q3 = calculatedQuantities.get("Q3");
        int IQR = calculatedQuantities.get("IQR");

        //if value is an outlier
        if ((numberToCompare < (Q1 - (1.5 * IQR))) || (numberToCompare > (Q3 + (1.5 * IQR)))) {
            return true;
        }
        //if its not an outlier
        else {
            return false;
        }
    }



    /**
     * abstraction method that calls calculate IQR, and uses the values calculated from there
     * to return a boolean value of whether a number is an outlier or not, based on the current
     * grades received for this assignment(this function takes a double to compare)
     */
    public boolean isOutlier(String courseID, int assignmentID, double numberToCompare) {
        HashMap<String, Integer> calculatedQuantities = new HashMap<String, Integer>();
        calculatedQuantities = calculateIQR(courseID, assignmentID);
        int Q1 = calculatedQuantities.get("Q1");
        int Q3 = calculatedQuantities.get("Q3");
        int IQR = calculatedQuantities.get("IQR");

        //if value is an outlier
        if ((numberToCompare < (Q1 - (1.5 * IQR))) || (numberToCompare > (Q3 + (1.5 * IQR)))) {
            return true;
        }
        //if its not an outlier
        else {
            return false;
        }
    }

    /**
     * abstraction method that calls calculate IQR, and uses the values calculated from there
     * to return a boolean value of whether a number is an outlier or not, based on the current
     * grades received for this assignment(this function takes an int to compare)
     */
    public boolean isOutlier(String courseID, int assignmentID, int numberToCompare) {
        HashMap<String, Integer> calculatedQuantities = new HashMap<String, Integer>();
        calculatedQuantities = calculateIQR(courseID, assignmentID);
        int Q1 = calculatedQuantities.get("Q1");
        int Q3 = calculatedQuantities.get("Q3");
        int IQR = calculatedQuantities.get("IQR");

        //if value is an outlier
        if ((numberToCompare < (Q1 - (1.5 * IQR))) || (numberToCompare > (Q3 + (1.5 * IQR)))) {
            return true;
        }
        //if its not an outlier
        else {
            return false;
        }
    }

    /**
     * This function is used to calculate the IQR, returning a hashmap of values
     * that consist of the q1, q3, and IQR values to allow for computation and
     * outlier detection.
     * */
    /**
     * This function is used to calculate the IQR, returning a hashmap of values
     * that consist of the q1, q3, and IQR values to allow for computation and
     * outlier detection.
     */
    public HashMap<String, Integer> calculateIQR(String courseID, int assignmentID) {


        //for every team in the course, grab the points, and add them to an integer array
        List<Integer> gradesForAssignment = new ArrayList<Integer>();


        //grab each grade that has been given for this respectove assignment
        List<Document> getEachAssignment = new ArrayList<>();
        FindIterable<Document> iterable = submissionsCollection.find(and(
                eq("course_id", courseID),
                eq("assignment_id", assignmentID),
                eq("type", "peer_review_submission")
        ));
        //put iterable contents into array list
        iterable.into(getEachAssignment);

        //for every grade that has been given, add it to the list
        for (Document grade : getEachAssignment) {

            if (grade.get("grade", Integer.class) == null) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Error retreiving grade for team").build());
            } else {

                int respectiveGrade = grade.get("grade", Integer.class);
                gradesForAssignment.add(respectiveGrade);
            }

        }

        int IQR = 0;
        //after all of the team grades are obtained,

        //sort the list
        Collections.sort(gradesForAssignment);

        HashMap<Integer, List<Integer>> subsets = getSubsetOfArray(gradesForAssignment);

        //get subsets from hashmap
        List<Integer> q1Subset = subsets.get(1);
        List<Integer> q3Subset = subsets.get(2);

        //get medians from subsets
        int q1Median = findMedian(q1Subset);
        int q3Median = findMedian(q3Subset);

        //get IQR from subtracting both values
        IQR = q3Median - q1Median;
        HashMap<String, Integer> returnValues = new HashMap<String, Integer>();
        returnValues.put("Q1", q1Median);
        returnValues.put("Q3", q3Median);
        returnValues.put("IQR", IQR);

        return returnValues;
    }

    /**
     * This fucntion returns a subset of the array to then be able to calculate the
     * median for each 'Q'
     */
    public HashMap<Integer, List<Integer>> getSubsetOfArray(List<Integer> input) {
        List<Integer> firstSubSet = new ArrayList<Integer>();
        List<Integer> secondSubSet = new ArrayList<Integer>();

        HashMap<Integer, List<Integer>> subsetOfArrays = new HashMap<>();
        //if true this is odd
        if ((input.size() & 1) == 1) {
            firstSubSet = input.subList(0, input.size() / 2);
            secondSubSet = input.subList(input.size() / 2 + 1, input.size());
        } else {
            firstSubSet = input.subList(0, input.size() / 2);
            secondSubSet = input.subList(input.size() / 2, input.size());
        }
        subsetOfArrays.put(1, firstSubSet);
        subsetOfArrays.put(2, secondSubSet);

        return subsetOfArrays;

    }

    /**
     * This function returns the median of any dataset that is larger than 2, also
     * it assumes that the data is already sorted when passed in
     */
    public int findMedian(List<Integer> dataSet) {
        //this fucntion won't accept an array of length less than 2,
        if (dataSet.size() < 2 || dataSet == null)
            return -1;
        //& 1 is a bitwise operator that is much faster than modulo and determines whether a number is odd or even
        if ((dataSet.size() & 1) == 1)
            //use int division return median
            return dataSet.get(dataSet.size() / 2);
        else
            //must use formula (((dataSet.length/2) + (dataSet.length/2 -1)) / 2) to obtain the proper index of even length dataset )
            return (dataSet.get(dataSet.size() / 2) + dataSet.get(dataSet.size() / 2 - 1)) / 2;

    }


    /**
     * This method will be used to grab all of the averages/assignments given over for a given course
     */
    public HashMap<String, Integer> calculateIQR(String courseID) {

        //must increment at end of each loop
        List<Document> results = new ArrayList<>();
        FindIterable<Document> iterable = assignmentCollection.find(and(eq("course_id", courseID)));

        iterable.into(results);

        //for every team in the course, grab the points, and add them to an integer array
        List<Integer> gradesForAssignment = new ArrayList<Integer>();
        int gradeFinalizedCounter = 0;
        //for each assignment that is completed
        for (Document eachAssignment : results) {
            System.out.println("Iterating over new assignment");

            if (eachAssignment.get("grade_finalized", Boolean.class)) {
                //if a grade has been finalized, increment counter
                gradeFinalizedCounter++;
                //must make a query to the DB to grab all of the grades
                //grab each grade that has been given for this respectove assignment
                List<Document> getEachAssignment = new ArrayList<>();
                FindIterable<Document> tempIter = submissionsCollection.find(and(
                        eq("course_id", courseID),
                        eq("assignment_id", eachAssignment.get("assignment_id", Integer.class)),
                        eq("type", "peer_review_submission")
                ));
                //put iterable contents into array list
                tempIter.into(getEachAssignment);

                //for every grade that has been given, add it to the list
                for (Document grade : getEachAssignment) {

                    if (grade.get("grade", Integer.class) == null) {
                        throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Error retreiving grade for team").build());
                    } else {
                        int respectiveGrade = grade.get("grade", Integer.class);
                        System.out.println("Grade gotten: " + respectiveGrade);
                        gradesForAssignment.add(respectiveGrade);
                    }

                }

            }

        }


        if (gradesForAssignment == null || gradeFinalizedCounter == 0)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No grades have been finalized, therefore outlier detection over time will not be accurate").build());

        int IQR = 0;
        //after all of the team grades are obtained,

        //we must sort them
        Collections.sort(gradesForAssignment);
        System.out.println("all grades grabbed and sorted: " + gradesForAssignment);
        System.out.println("Size of array: " + gradesForAssignment.size());


        HashMap<Integer, List<Integer>> subsets = getSubsetOfArray(gradesForAssignment);

        //get subsets from hashmap
        List<Integer> q1Subset = subsets.get(1);
        List<Integer> q3Subset = subsets.get(2);

        //get medians from subsets
        int q1Median = findMedian(q1Subset);
        int q3Median = findMedian(q3Subset);

        //get IQR from subtracting both values
        IQR = q3Median - q1Median;
        HashMap<String, Integer> returnValues = new HashMap<String, Integer>();
        returnValues.put("Q1", q1Median);
        returnValues.put("Q3", q3Median);
        returnValues.put("IQR", IQR);

        return returnValues;
    }


    /**
     * This is a method to be used for testing purposes, it will simplify writiing the test cases
     * while keeping the same functionality as all other isOutlier methods
     */
    public static boolean isOutlier(int numberToCompare, int Q1, int Q3, int IQR) {


        //if value is an outlier
        if ((numberToCompare < (Q1 - (1.5 * IQR))) || (numberToCompare > (Q3 + (1.5 * IQR)))) {
            return true;
        }
        //if its not an outlier
        else {
            return false;
        }
    }

    /**
     * This method returns a JSON object(Document), that contains all relevant information regarding
     * the grades a team ahs received and the average of the grades received, as well as the average grade
     * each team has given to other teams. The JSON document it returns also has a boolean value associated
     * with the grade stating false if the value is not an outlier, and true if the value is an outlier
     * <p>
     * Notes:
     * This method has the assumed functionality that every team in the course has been assigned/performed
     * a peer review, as determining if a team was excluded from the assignment/peer-review process was far
     * too much work given the data available from querying from the respectove databases.
     */
    @Deprecated
    public Document getMatrixOfGrades(String courseID, int assignmentID) {

        //get all teams that were assigned this assignment in the course(will be the 'iterable')
        Document assignment = assignmentCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        if (assignment == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment not found.").build());
        //grab all teams assigned this
        List<String> allTeams = assignment.getList("all_teams", String.class);


        //we must sort them based on alphabetical order
        Collections.sort(allTeams);

        //then find every team that graded them(should be a base function call already made inside this file)
        Document matrixOfGrades = new Document();

        for (String teamForThisAssignment : allTeams) {

            //get document for each team for this assignment
            Document team_submission = submissionsCollection.find(and(
                    eq("course_id", courseID),
                    eq("assignment_id", assignmentID),
                    eq("team_name", teamForThisAssignment),
                    eq("type", "team_submission"))).first();

            //grade the teams that 'reviewed'/graded this team
            List<String> teams_that_graded = team_submission.getList("reviews", String.class);
            int sizeOfTeamsThatGraded = teams_that_graded.size();

            //sort teams_that_graded
            Collections.sort(teams_that_graded);

            //create doc for holding each team that graded and if its an outlier or not
            Document gradesToOutliers = new Document();

            //integer values to keep track of average of scores and number of reviews each team made
            int totalSum = 0, counter = 0;

            for (String teamThatGraded : teams_that_graded) {
                //for each team in the reviews, we need to find the team that reviewed the current team
                Document team_review = submissionsCollection.find(and(
                        eq("course_id", courseID),
                        eq("assignment_id", assignmentID),
                        eq("reviewed_by", teamThatGraded),
                        eq("reviewed_team", teamForThisAssignment),
                        eq("type", "peer_review_submission"))).first();

                //if the grade is an outlier(boolean = true, else boolean = false)
                int respectiveGrade = team_review.get("grade", Integer.class);
                //sum the total and increase counter
                totalSum += respectiveGrade;
                counter++;
                System.out.println("counter: " + counter);
                double average = (double) totalSum / (double) counter;


                if (isOutlier(courseID, assignmentID, respectiveGrade)) {
                    //fill in the values if they're an outlier
                    gradesToOutliers.append(teamThatGraded, new Document(String.valueOf(respectiveGrade), true));
                    if (counter == sizeOfTeamsThatGraded) {
                        gradesToOutliers.append("Average Grade Received", new Document(String.valueOf(average), isOutlier(courseID, assignmentID, average)));
                        System.out.println("value is an: " + isOutlier(courseID, assignmentID, average));
                    }


                } else {
                    gradesToOutliers.append(teamThatGraded, new Document(String.valueOf(respectiveGrade), false));
                    if (counter == sizeOfTeamsThatGraded) {
                        gradesToOutliers.append("Average Grade Received", new Document(String.valueOf(average), isOutlier(courseID, assignmentID, average)));
                        System.out.println("value is an: " + isOutlier(courseID, assignmentID, average));
                    }

                }


            }
            matrixOfGrades.append(teamForThisAssignment, gradesToOutliers);

        }

        //hashmaps to keep track of grades each team has given/they're review count
        HashMap<String, Integer> teamsToGradesGiven = new HashMap<>();
        HashMap<String, Integer> teamsToCountOfReviews = new HashMap<>();

        //create values for each team in a hashmap for their grades
        for (String teams : matrixOfGrades.keySet()) {
            teamsToGradesGiven.put(teams, 0);
            teamsToCountOfReviews.put(teams, 0);
        }

        //I know three loops is disgusting, but the way the document we created above is formatted,
        //the grade is three levels deep, so we need to go to that depth to get the grade

        //grab the keys of the doc(all the grades in document we just created)
        for (String keysInMatrixDoc : matrixOfGrades.keySet()) {

            //this will retrieve the list of teams who have given grades
            Object valuesOfKeys = matrixOfGrades.get(keysInMatrixDoc);

            //try casting the object to a document
            Document valuesOfEachKey = (Document) valuesOfKeys;

            for (String subKeySet : valuesOfEachKey.keySet()) {

                //to get the grade document we need to go one step further
                Document gradesAndBoolean = (Document) valuesOfEachKey.get(subKeySet);


                for (String grade : gradesAndBoolean.keySet()) {
                    //if current subKeyString equals the a hashmap key, sum current value and counter)
                    if (teamsToGradesGiven.containsKey(subKeySet)) {
                        teamsToGradesGiven.put(subKeySet, teamsToGradesGiven.get(subKeySet) + Integer.parseInt(grade));
                        teamsToCountOfReviews.put(subKeySet, teamsToCountOfReviews.get(subKeySet) + 1);
                    }

                }

            }

        }


        //document to find/grad the grades, will append this to each section of the documentToAppendGrades
        Document gradesHolder = new Document();

        //create document to then append to the matric doc(for grades given averages)
        for (String key : teamsToGradesGiven.keySet()) {
            //first calculate average
            double average = (double) teamsToGradesGiven.get(key) / (double) teamsToCountOfReviews.get(key);

            if (isOutlier(courseID, assignmentID, average)) {
                gradesHolder.append(key, new Document(String.valueOf(average), true));
            } else {
                gradesHolder.append(key, new Document(String.valueOf(average), false));
            }
        }

        matrixOfGrades.append("Average Grade Given", gradesHolder);

        return matrixOfGrades;
    }


    /**
     * This method is very similar to the matrix of grades method but returns every matrix over the course of
     * every assignment created, this is necessary as the front end can then decide what information they
     * would like to present the user(i.e. only the outliers, etc). This method takes every grade given/received
     * and uses that as the outlier detection system, rather than individually for each matrix. This allows for a
     * more broad evaluation of the outliers and if a team does grade harsher over the course of the semester
     * rather than on a weekly basis.
     **/
    @Deprecated
    public Document allPotentialOutliers(String courseID) {

        //must increment at end of each loop
        List<Document> results = new ArrayList<>();
        FindIterable<Document> iterable = assignmentCollection.find();
        iterable.into(results);


        Document allPotentialOutliers = new Document();


        //for each assignment that is peer-reviewed? or also just regular assignments
        for (Document assignment : results) {


            //error check to see if the assignment is completed(all grades have been finished for each respective assignment)
            if (assignment.get("grade_finalized", Boolean.class)) {
                //if the assignment_flag doesnt get set, we have to cast the assignment teams object
                //and then compare the length of that to the cast of the completed teams object


                List<String> allTeams = assignment.getList("all_teams", String.class);
                //grab assignment_id
                int numberOfAssignments = assignment.get("assignment_id", Integer.class);

                //we must sort them based on alphabetical order
                Collections.sort(allTeams);

                //then find every team that graded them(should be a base function call already made inside this file)
                Document matrixOfGrades = new Document();

                for (String teamForThisAssignment : allTeams) {

                    //get document for each team for this assignment
                    Document team_submission = submissionsCollection.find(and(
                            eq("course_id", courseID),
                            eq("assignment_id", numberOfAssignments),
                            eq("team_name", teamForThisAssignment),
                            eq("type", "team_submission"))).first();

                    //grade the teams that 'reviewed'/graded this team
                    List<String> teams_that_graded = team_submission.getList("reviews", String.class);
                    int sizeOfTeamsThatGraded = teams_that_graded.size();

                    //sort teams_that_graded
                    Collections.sort(teams_that_graded);

                    //create doc for holding each team that graded and if its an outlier or not
                    Document gradesToOutliers = new Document();

                    //integer values to keep track of average of scores and number of reviews each team made
                    int totalSum = 0, counter = 0;

                    for (String teamThatGraded : teams_that_graded) {
                        //for each team in the reviews, we need to find the team that reviewed the current team
                        Document team_review = submissionsCollection.find(and(
                                eq("course_id", courseID),
                                eq("assignment_id", numberOfAssignments),
                                eq("reviewed_by", teamThatGraded),
                                eq("reviewed_team", teamForThisAssignment),
                                eq("type", "peer_review_submission"))).first();

                        //if the grade is an outlier(boolean = true, else boolean = false)
                        int respectiveGrade = team_review.get("grade", Integer.class);
                        //sum the total and increase counter
                        totalSum += respectiveGrade;
                        counter++;
                        double average = (double) totalSum / (double) counter;


                        if (isOutlier(courseID, respectiveGrade)) {
                            //fill in the values if they're an outlier
                            gradesToOutliers.append(teamThatGraded, new Document(String.valueOf(respectiveGrade), true));
                            if (counter == sizeOfTeamsThatGraded) {
                                gradesToOutliers.append("Average Grade Received", new Document(String.valueOf(average), isOutlier(courseID, average)));
                            }

                        } else {
                            gradesToOutliers.append(teamThatGraded, new Document(String.valueOf(respectiveGrade), false));
                            if (counter == sizeOfTeamsThatGraded) {
                                gradesToOutliers.append("Average Grade Received", new Document(String.valueOf(average), isOutlier(courseID, average)));
                            }
                        }


                    }
                    matrixOfGrades.append(teamForThisAssignment, gradesToOutliers);


                }
                allPotentialOutliers.append(assignment.get("assignment_id", Integer.class).toString(), matrixOfGrades);

            }

        }


        HashMap<String, HashMap<String, Integer>> teamsToGradesGiven = new HashMap<>();
        HashMap<String, HashMap<String, Integer>> teamsToCountOfReviews = new HashMap<>();


        for (String key : allPotentialOutliers.keySet()) {
            Object val = allPotentialOutliers.get(key);

            Document keys = (Document) val;

            HashMap<String, Integer> temp = new HashMap<>();
            HashMap<String, Integer> otherTemp = new HashMap<>();
            for (String values : keys.keySet()) {
                temp.put(values, 0);
                otherTemp.put(values, 0);
            }
            teamsToGradesGiven.put(key, temp);
            teamsToCountOfReviews.put(key, otherTemp);

        }


        //again i know it is ridiculous for this many loops but the way the JSON doc created earlier is formatted
        //is is necessary to loop over all the information to grab the right information
        for (String keys : allPotentialOutliers.keySet()) {

            Object allMatrices = allPotentialOutliers.get(keys);

            Document matrixOfGrades = (Document) allMatrices;
            //grab the keys of the doc(all the grades in document we just created)
            for (String keysInMatrixDoc : matrixOfGrades.keySet()) {

                //this will retrieve the list of teams who have given grades
                Object valuesOfKeys = matrixOfGrades.get(keysInMatrixDoc);

                //try casting the object to a document
                Document valuesOfEachKey = (Document) valuesOfKeys;

                for (String subKeySet : valuesOfEachKey.keySet()) {


                    //to get the grade document we need to go one step further
                    Document gradesAndBoolean = (Document) valuesOfEachKey.get(subKeySet);

                    for (String grade : gradesAndBoolean.keySet()) {

                        //if current subKeyString equals the a hashmap key, sum current value and counter)
                        if (teamsToGradesGiven.get(keys).containsKey(subKeySet)) {
                            HashMap<String, Integer> innerGradesGiven = teamsToGradesGiven.get(keys);
                            HashMap<String, Integer> innerCountReviews = teamsToCountOfReviews.get(keys);
                            innerGradesGiven.put(subKeySet, innerGradesGiven.get(subKeySet) + Integer.valueOf(grade));
                            innerCountReviews.put(subKeySet, innerCountReviews.get(subKeySet) + 1);
                            teamsToGradesGiven.put(keys, innerGradesGiven);
                            teamsToCountOfReviews.put(keys, innerCountReviews);
                        }

                    }

                }

            }
        }


        for (String assignmentNumber : teamsToGradesGiven.keySet()) {
            //find the location of each respective doc and then add the averages to each respectove assignment
            HashMap<String, Integer> temp = new HashMap<>();
            temp = teamsToGradesGiven.get(assignmentNumber);
            Document gradesHolder = new Document();

            //create document to then append to the matric doc(for grades given averages)
            for (String key : temp.keySet()) {
                //first calculate average
                double average = (double) teamsToGradesGiven.get(assignmentNumber).get(key) / (double) teamsToCountOfReviews.get(assignmentNumber).get(key);

                if (isOutlier(courseID, average)) {
                    gradesHolder.append(key, new Document(String.valueOf(average), true));
                } else {
                    gradesHolder.append(key, new Document(String.valueOf(average), false));
                }
            }

            //grab portion related to this assignment number
            Object holder = allPotentialOutliers.get(assignmentNumber);
            Document holderr = (Document) holder;

            holderr.append("Average Grade Given", gradesHolder);
        }


        /**
         * This is a method to be used for testing purposes, it will simplify writiing the test cases
         * while keeping the same functionality as all other isOutlier methods
         * */

    /*public boolean isOutlier(int numberToCompare, int Q1, int Q3, int IQR){
        return allPotentialOutliers;
    }
    */


        return allPotentialOutliers;
    }

    public List<String> getReviewTeams(String courseID, int assignmentID, String teamName) {
        Document assignment = assignmentCollection.find(and(eq("assignment_id", assignmentID), eq("course_id", courseID))).first();
        Document assignedTeams = (Document) assignment.get("assigned_teams");
        List<String> teams = assignment.getList("all_teams", String.class);
        if(teams == null){
            return null;
        }
        ArrayList<String> reviewTeams = new ArrayList<>();
        for(String team : teams){
            List<String> teamList = assignedTeams.getList(team, String.class);
            if(teamList == null){
                continue;
            }
            if(teamList.contains(teamName)){
                reviewTeams.add(team);
            }
        }
        return reviewTeams;
    }

    /**
     * gets list of submissions to be peer-reviewed by a specified team
     *
     * @param courseID course in question
     * @param assignmentID assignment for which desired submissions are made
     * @param teamName aforementioned specified team
     * @return list of submissions to be peer-reviewed by a specified team
     */
    public List<Document> peerReviewsGiven(String courseID, int assignmentID, String teamName) {
        List<String> teams = getAssignedTeams(courseID, assignmentID, teamName);
        if(teams == null){
            return null;
        }
        List<Document> submissions = new ArrayList<>();
        for(String team : teams){
            Document submission = submissionsCollection.find(and(eq("team_name",team),
                    eq("course_id", courseID), eq("assignment_id", assignmentID),
                    eq("type", "team_submission"))).first();
            if(submission != null) {
                submissions.add(submission);
            }
        }
        return submissions;
    }

    /**
     * gets list of submissions to be peer-reviewed by a specified team
     *
     * @param courseID course in question
     * @param assignmentID assignment for which desired submissions are made
     * @param teamName aforementioned specified team
     * @return list of submissions to be peer-reviewed by a specified team
     */
    public List<Document> peerReviewsReceived(String courseID, int assignmentID, String teamName) {
        List<String> teams = getReviewTeams(courseID, assignmentID, teamName);
        if(teams == null){
            return null;
        }
        List<Document> submissions = new ArrayList<>();
        for(String team : teams){
            if(team == null){
                continue;
            }
            Document submission = submissionsCollection.find(and(eq("reviewed_by",team),
                    eq("course_id", courseID), eq("assignment_id", assignmentID),
                    eq("type", "peer_review_submission"), eq("reviewed_team", teamName)))
                    .first();
            if(submission != null) {
                submissions.add(submission);
            }else{
                submission = submissionsCollection.find(and(eq("team_name", team),
                        eq("course_id", courseID), eq("assignment_id", assignmentID),
                        eq("type", "team_submission"))).first();
                if(submission != null){
                    submissions.add(submission);
                }
            }
        }
        return submissions;
    }
}
