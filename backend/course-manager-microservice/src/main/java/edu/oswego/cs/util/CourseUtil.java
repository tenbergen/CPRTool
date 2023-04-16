package edu.oswego.cs.util;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class CourseUtil {
    public void updateCoursesArrayInProfessorDb(SecurityContext securityContext, MongoCollection<Document> collection, String originalCourseID, String newCourseID, String mode) {
        String professorID = securityContext.getUserPrincipal().getName().split("@")[0];

        Document professorDocument = collection.find(eq("professor_id", professorID)).first();
        if (professorDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "This professor does not exist.");

        List<String> professorDocumentCourses = professorDocument.getList("courses", String.class);
        if (professorDocumentCourses == null) throw new CPRException(Response.Status.CONFLICT, "Professor profile is not set up properly.");

        if (mode.equals("UPDATE")) Collections.replaceAll(professorDocumentCourses, originalCourseID, newCourseID);
        else if (mode.equals("DELETE")) professorDocumentCourses.remove(originalCourseID);

        collection.updateOne(eq("professor_id", professorID), Updates.set("courses", professorDocumentCourses));
    }

    public void updateCoursesArrayInStudentDb(MongoCollection<Document> collection, String originalCourseID, String newCourseID, String mode) {
        for (Document studentDocument : collection.find()) {
            List<String> studentDocumentCourses = studentDocument.getList("courses", String.class);
            Bson studentFilter = eq("student_id", studentDocument.getString("student_id"));

            if (mode.equals("UPDATE")) Collections.replaceAll(studentDocumentCourses, originalCourseID, newCourseID);
            else if (mode.equals("DELETE")) studentDocumentCourses.remove(originalCourseID);

            if (studentDocument.size() > 0) collection.updateOne(studentFilter, Updates.set("courses", studentDocumentCourses));
            else collection.deleteOne(studentFilter);
        }
    }

    public void updateCoursesKeyInDBs(MongoCollection<Document> collection, String originalCourseID, String newCourseID, String mode) {
        Bson documentFilter = eq("course_id", originalCourseID);
        if (mode.equals("UPDATE")) {
            collection.updateMany(documentFilter, Updates.set("course_id", newCourseID));
        } else if (mode.equals("DELETE")) {
            collection.deleteMany(documentFilter);
        }
    }

    public void updateTeamSize(MongoCollection<Document> collection, String courseID, int newTeamSize) {
        if (newTeamSize == 0) throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Team size can not be zero.").build());
        Bson teamFilter = eq("course_id", courseID);
        MongoCursor<Document> cursor = collection.find(teamFilter).iterator();

        List<Integer> teamMembersCount = collection.aggregate(
                        Arrays.asList(
                                Aggregates.match(eq("course_id", courseID)),
                                Aggregates.project(Projections.computed("membersCount", Projections.computed("$size", "$team_members")))))
                .map(follower -> follower.getInteger("membersCount"))
                .into(new ArrayList<>());

        if (teamMembersCount.size() > 0) {
            if (newTeamSize < Collections.max(teamMembersCount)) throw new CPRException(Response.Status.CONFLICT, "Team size conflicts with the number of team members.");
        }

        while (cursor.hasNext()) {
            Document teamDocument = cursor.next();
            List<String> teamMembers = teamDocument.getList("team_members", String.class);
            if (newTeamSize == teamMembers.size()) collection.updateOne(teamDocument, Updates.set("team_full", true));
            else collection.updateOne(teamDocument, Updates.set("team_full", false));
        }

        collection.updateMany(teamFilter, Updates.set("team_size", newTeamSize));
    }
}
