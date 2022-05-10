package edu.oswego.cs.services;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import edu.oswego.cs.util.CPRException;
import org.bson.Document;

public class IdentifyingService {
    public void identifyingStudentService(SecurityContext securityContext, String studentID) {
        if (!securityContext.isUserInRole("professor")) {
            String userID = securityContext.getUserPrincipal().getName().split("@")[0];
            if (!studentID.equals(userID))
                throw new CPRException(Response.Status.FORBIDDEN,"User principal name doesn't match");
        }
    }

    public void identifyingProfessorService(SecurityContext securityContext, MongoCollection<Document> courseCollection, String courseID) {
        if (securityContext.isUserInRole("professor")) {
            String userID = securityContext.getUserPrincipal().getName().split("@")[0];
            Document courseDocument = courseCollection.find(Filters.eq("course_id", courseID)).first();
            String professorID = courseDocument.getString("professor_id");
            if (!userID.equals(professorID))
                throw new CPRException(Response.Status.FORBIDDEN,"User principal name doesn't match");
        }
    }
}
