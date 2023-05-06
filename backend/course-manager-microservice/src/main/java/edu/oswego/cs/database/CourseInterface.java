package edu.oswego.cs.database;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.FileDAO;
import edu.oswego.cs.daos.StudentDAO;
import edu.oswego.cs.util.CPRException;
import edu.oswego.cs.util.CourseUtil;
import org.bson.Document;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.push;
import static com.mongodb.client.model.Updates.set;
import static edu.oswego.cs.util.CSVUtil.parseStudentCSV;

public class CourseInterface {
    private final MongoCollection<Document> studentCollection;
    private final MongoCollection<Document> professorCollection;
    private final MongoCollection<Document> courseCollection;
    private final MongoCollection<Document> assignmentCollection;
    private final MongoCollection<Document> submissionCollection;
    private final MongoCollection<Document> teamCollection;

    /**
     * A hashmap of course IDs to a boolean value indicating whether the there is a request
     * trying to add a student to the specified course in addStudent().
     */
    private static final ConcurrentHashMap<String, Boolean> courseLocks = new ConcurrentHashMap<>();

    public CourseInterface() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            MongoDatabase studentDB = databaseManager.getStudentDB();
            MongoDatabase professorDB = databaseManager.getProfessorDB();
            MongoDatabase courseDB = databaseManager.getCourseDB();
            MongoDatabase assignmentDB = databaseManager.getAssignmentDB();
            MongoDatabase teamDB = databaseManager.getTeamDB();
            studentCollection = studentDB.getCollection("students");
            professorCollection = professorDB.getCollection("professors");
            courseCollection = courseDB.getCollection("courses");
            assignmentCollection = assignmentDB.getCollection("assignments");
            submissionCollection = assignmentDB.getCollection("submissions");
            teamCollection = teamDB.getCollection("teams");
        } catch (WebApplicationException e) {
            throw new CPRException(Response.Status.BAD_REQUEST, "Failed to retrieve collections.");
        }
    }

    public void addCourse(SecurityContext securityContext, CourseDAO dao) {
        String professorID = securityContext.getUserPrincipal().getName().split("@")[0];
        dao.professorID = professorID;

        Document courseDocument = courseCollection.find(and(
                eq("course_id", dao.courseID),
                eq("professor_id", professorID)
        )).first();
        if (courseDocument != null) throw new CPRException(Response.Status.CONFLICT, "Course already existed.");

        Document professorDocument = professorCollection.find(eq("professor_id", professorID)).first();
        if (professorDocument == null)
            throw new CPRException(Response.Status.NOT_FOUND, "This professor does not exist.");

        List<String> professorDocumentCourses = professorDocument.getList("courses", String.class);
        if (professorDocumentCourses == null)
            throw new CPRException(Response.Status.CONFLICT, "Professor profile is not set up properly.");

        if (dao.courseName == null || dao.abbreviation == null || dao.courseName == null
                || dao.courseSection == null || dao.crn == null || dao.semester == null) {
            throw new CPRException(Response.Status.NOT_FOUND, "At least one required field is null.");
        }


        professorDocumentCourses.add(dao.courseID);
        professorCollection.updateOne(eq("professor_id", professorID), Updates.set("courses", professorDocumentCourses));

        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(dao), MediaType.APPLICATION_JSON_TYPE);
        Document course = Document.parse(courseDAOEntity.getEntity());
        courseCollection.insertOne(course);

        List<String> students = course.getList("students", String.class);
        for (String student : students) {
            Document studentDocument = studentCollection.find(eq("student_id", student)).first();
            if (studentDocument != null)
                studentCollection.updateOne(eq("student_id", student), push("courses", dao.courseID));
        }
    }

    public String updateCourse(SecurityContext securityContext, CourseDAO dao) {
        String professorID = securityContext.getUserPrincipal().getName().split("@")[0];

        Document courseDocument = courseCollection.find(and(eq("course_id", dao.getCourseID()), eq("professor_id", professorID))).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "This course does not exist.");

        String originalCourseID = dao.courseID;
        String newCourseID = dao.abbreviation + "-" + dao.courseSection + "-" + dao.crn + "-" + dao.semester + "-" + dao.year;
        int originalTeamSize = courseDocument.getInteger("team_size");
        dao.blockedWords = courseDocument.getList("blocked_words", String.class);
        int newTeamSize = dao.teamSize;
        dao.courseID = newCourseID;
        dao.students = courseDocument.getList("students", String.class);
        dao.professorID = professorID;

        if (!originalCourseID.equals(newCourseID)) {
            Document duplicatedCourseDocument = courseCollection.find(and(
                    eq("course_id", newCourseID),
                    eq("professor_id", professorID)
            )).first();
            if (duplicatedCourseDocument != null)
                throw new CPRException(Response.Status.CONFLICT, "This course_id already exist.");
        }

        if (originalTeamSize != newTeamSize) {
            new CourseUtil().updateTeamSize(teamCollection, originalCourseID, newTeamSize);
        }

        new CourseUtil().updateCoursesArrayInProfessorDb(securityContext, professorCollection, originalCourseID, newCourseID, "UPDATE");
        new CourseUtil().updateCoursesArrayInStudentDb(studentCollection, originalCourseID, newCourseID, "UPDATE");
        new CourseUtil().updateCoursesKeyInDBs(assignmentCollection, originalCourseID, newCourseID, "UPDATE");
        new CourseUtil().updateCoursesKeyInDBs(submissionCollection, originalCourseID, newCourseID, "UPDATE");
        new CourseUtil().updateCoursesKeyInDBs(teamCollection, originalCourseID, newCourseID, "UPDATE");

        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(dao), MediaType.APPLICATION_JSON_TYPE);
        Document course = Document.parse(courseDAOEntity.getEntity());
        courseCollection.replaceOne(eq("course_id", originalCourseID), course);
        return newCourseID;
    }

    /**
     * Adds a student to the given course, also adds the course to the
     * student's list of enrolled courses.
     * <p>
     * The spin block is to ensure that there is only one request currently working
     * to add the student to the course. If the spin block is not present, it is possible
     * that two requests will try to add the student to the course (nearly) at the same time,
     * which will cause the student to be added twice.
     *
     * @param securityContext the content of the application (for professor information)
     * @param student         the student to be added to the course
     * @param courseID        the course to add the student to
     */
    public void addStudent(SecurityContext securityContext, StudentDAO student, String courseID) {
        while (courseLocks.containsKey(courseID)) ; /* spin block (see explanation above) */
        courseLocks.put(courseID, true);
        String professorID = securityContext.getUserPrincipal().getName().split("@")[0];
        String studentId = student.email.split("@")[0];
        String studentLastName = student.fullName.split(", ")[0];
        String studentFirstName = student.fullName.split(", ")[1];

        Document courseDocument = courseCollection.find(and(eq("course_id", courseID), eq("professor_id", professorID))).first();
        if (courseDocument == null) {
            courseLocks.remove(courseID);
            throw new CPRException(Response.Status.NOT_FOUND, "This course does not exist.");
        }

        List<String> students = courseDocument.getList("students", String.class);
        if (students.contains(studentId)) {
            courseLocks.remove(courseID);
            throw new CPRException(Response.Status.CONFLICT, "This student is already in the course.");
        }
        courseCollection.updateOne(eq("course_id", courseID), push("students", studentId));

        Document studentDocument = studentCollection.find(eq("student_id", studentId)).first();
        boolean studentNotFound = false;
        if (studentDocument == null) {
            studentNotFound = true;
            studentDocument = new Document()
                    .append("first_name", studentFirstName)
                    .append("last_name", studentLastName)
                    .append("student_id", studentId)
                    .append("courses", new ArrayList<String>())
                    .append("team_submissions", new ArrayList<Document>())
                    .append("peer_reviews", new ArrayList<Document>());
        }

        List<String> courseList = studentDocument.getList("courses", String.class);
        boolean isAlreadyEnrolled = courseList.contains(courseID);
        if (isAlreadyEnrolled) {
            courseLocks.remove(courseID);
            throw new CPRException(Response.Status.CONFLICT, "This student is already in the course.");
        } else {
            if (studentNotFound) {
                studentDocument.put("courses", new ArrayList<>(List.of(courseID)));
                studentCollection.insertOne(studentDocument);
            } else studentCollection.updateOne(eq("student_id", studentId), push("courses", courseID));
        }
        courseLocks.remove(courseID);
    }

    public void removeCourse(SecurityContext securityContext, String courseID) {
        String professorID = securityContext.getUserPrincipal().getName().split("@")[0];
        Document courseDocument = courseCollection.find(and(eq("course_id", courseID), eq("professor_id", professorID))).first();
        if (courseDocument == null) throw new CPRException(Response.Status.BAD_REQUEST, "This course does not exist.");
        new CourseUtil().updateCoursesArrayInProfessorDb(securityContext, professorCollection, courseID, null, "DELETE");
        new CourseUtil().updateCoursesArrayInStudentDb(studentCollection, courseID, null, "DELETE");
        new CourseUtil().updateCoursesKeyInDBs(assignmentCollection, courseID, null, "DELETE");
        new CourseUtil().updateCoursesKeyInDBs(submissionCollection, courseID, null, "DELETE");
        new CourseUtil().updateCoursesKeyInDBs(teamCollection, courseID, null, "DELETE");
        courseCollection.deleteOne(eq("course_id", courseID));
    }

    /**
     * Removes a student from the roster of a given course, removes the course from the
     * student's list of courses, removes the student from the team they are on
     * within the course (if applicable), "randomly" assigns a new team leader if
     * the student removed was the leader of a team, and removes the team if the team
     * is empty after the removal of the student.
     *
     * @param securityContext the context of the application (for professor information)
     * @param studentID       the net ID of the student to be removed
     * @param courseID        the ID of the course from which the student is to be removed
     */
    public void removeStudent(SecurityContext securityContext, String studentID, String courseID) {
        String professorID = securityContext.getUserPrincipal().getName().split("@")[0];
        Document studentDocument = studentCollection.find(and(eq("student_id", studentID), eq("courses", courseID))).first();
        if (studentDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "This student does not exist.");
        Document courseDocument = courseCollection.find(and(eq("course_id", courseID), eq("professor_id", professorID))).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "This course does not exist.");
        List<String> courses = studentDocument.getList("courses", String.class);
        courses.remove(courseID);
        studentCollection.updateOne(eq("student_id", studentID), set("courses", courses));
        List<String> students = courseDocument.getList("students", String.class);
        students.remove(studentID);
        courseCollection.updateOne(eq("course_id", courseID), set("students", students));

        Document teamDocument = teamCollection.find(and(eq("course_id", courseID), eq("team_members", studentID))).first();
        if (teamDocument == null) return;
        int teamSize = teamDocument.getInteger("team_size", -1);
        boolean isOnlyMember = teamDocument.getInteger("team_size", -1) == 1;
        boolean isTeamLeader = teamDocument.getString("team_lead").equals(studentID);
        String teamID = teamDocument.getString("team_id");
        if (teamDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "This team does not exist.");
        List<String> teamMembers = teamDocument.getList("team_members", String.class);
        if (isOnlyMember) {
            teamCollection.deleteOne(eq("course_id", courseID));
            return;
        }
        teamMembers.remove(studentID);
        teamCollection.updateOne(eq("team_id", teamID), set("team_size", teamSize - 1));
        teamCollection.updateOne(eq("team_id", teamID), set("team_members", teamMembers));
        if (isTeamLeader) {
            /* "Randomly" select a new team leader (for now) */
            String newTeamLeader = teamMembers.get(0);
            teamCollection.updateOne(eq("team_id", teamID), set("team_leader", newTeamLeader));
        }
    }

    public void addStudentsFromCSV(SecurityContext securityContext, FileDAO fileDAO) {
        String professorID = securityContext.getUserPrincipal().getName().split("@")[0];
        List<StudentDAO> allStudents = parseStudentCSV(fileDAO.getCsvLines());

        String cid = fileDAO.getFilename();
        cid = cid.substring(0, cid.length() - 4);

        Document course = courseCollection.find(and(eq("course_id", cid), eq("professor_id", professorID))).first();
        if (course == null) throw new CPRException(Response.Status.BAD_REQUEST, "This course does not exist.");

        List<String> oldStudentList = course.getList("students", String.class);
        String courseID = course.getString("course_id");
        ArrayList<String> newStudentList = new ArrayList<>();
        ArrayList<String> studentsToRemove = new ArrayList<>();
        ArrayList<String> studentsToAdd = new ArrayList<>();

        for (StudentDAO studentDAO : allStudents) newStudentList.add(studentDAO.email.split("@")[0]);

        for (String student : oldStudentList) {
            if (!newStudentList.contains(student)) studentsToRemove.add(student);
        }

        for (String student : newStudentList) {
            if (!oldStudentList.contains(student)) studentsToAdd.add(student);
        }

        for (String student : studentsToRemove) removeStudent(securityContext, student, courseID);

        for (StudentDAO student : allStudents.stream()
                .filter(s -> studentsToAdd.contains(s.email.split("@")[0]))
                .collect(Collectors.toList())) {
            addStudent(securityContext, student, courseID);
        }
    }

    public void updateBlockedWordsForCourse(String course_id, String payload) {
        Document courseDocument = courseCollection.find(eq("course_id", course_id)).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "This course does not exist.");
        Gson gson = new Gson();
        List<String> blockedWords = gson.fromJson(payload, List.class);
        courseDocument.put("blocked_words", blockedWords);
        courseCollection.updateOne(eq("course_id", course_id), set("blocked_words", blockedWords));
    }

    public String getBlockedWordsForCourse(String course_id) {
        Document courseDocument = courseCollection.find(eq("course_id", course_id)).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "This course does not exist.");
        Gson gson = new Gson();
        List<String> blockedWords = courseDocument.getList("blocked_words", String.class);
        return gson.toJson(blockedWords);
    }
}
