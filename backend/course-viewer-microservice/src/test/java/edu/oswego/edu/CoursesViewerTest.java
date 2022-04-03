package edu.oswego.edu;

import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.StudentDAO;
import org.junit.jupiter.api.*;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CoursesViewerTest {

    private static final Jsonb jsonb = JsonbBuilder.create();
    private static final ArrayList<CourseDAO> expectedCourses = new ArrayList<>();
    private static final ArrayList<StudentDAO> expectedStudents = new ArrayList<>();
    private static String port;
    private static String baseUrl;
    private static String targetUrl;
    private Client client;
    private static CourseDAO course1, course2, course3;
    private static StudentDAO student1, student2, student3, student4;

    @BeforeAll
    public static void oneTimeSetup() {
        port = "13125";
        baseUrl = "http://moxie.cs.oswego.edu:" + port + "/view/professor/";


        // variables for inserted courses
        String abbreviation1 = "CSC378";
        String courseName1 = "JUnit Theory";
        String courseSection1 = "800";
        String crn1 = "54226";
        String semester1 = "Spring";
        String year1 = "2023";

        String abbreviation2 = "CRW406";
        String courseName2 = "Advanced Fiction Writing";
        String courseSection2 = "700";
        String crn2 = "54321";
        String semester2 = "Spring";
        String year2 = "2023";

        String abbreviation3 = "PHL309";
        String courseName3 = "Logic, Language, and Thought";
        String courseSection3 = "HY1";
        String crn3 = "24680";
        String semester3 = "Fall";
        String year3 = "2021";

        course1 = new CourseDAO(abbreviation1, courseName1, courseSection1, crn1, semester1, year1);
        course2 = new CourseDAO(abbreviation2, courseName2, courseSection2, crn2, semester2, year2);
        course3 = new CourseDAO(abbreviation3, courseName3, courseSection3, crn3, semester3, year3);

        // add these courses to the database
        expectedCourses.add(course1);
        expectedCourses.add(course2);
        expectedCourses.add(course3);

        // variables for inserted students
        String sID1 = "lmcmahan";
        String sID2 = "tpark";
        String sID3 = "ecuevas";
        String sID4 = "cnoto";

        student1 = new StudentDAO(sID1);
        student2 = new StudentDAO(sID2);
        student3 = new StudentDAO(sID3);
        student4 = new StudentDAO(sID4);

        // add these courses to the database
        expectedStudents.add(student1);
        expectedStudents.add(student2);
        expectedStudents.add(student3);
        expectedStudents.add(student4);
    }

    // add and delete all the courses over the course of these tests.
    // we confirmed that add and delete work already, so we can call for each on all the elements here to clear them out
    // I wonder if this messes with any other elements already in the db...
    // NOPE! Only does it for each of the courses we made in "expected"
    @BeforeEach
    public void setup() {
        client = ClientBuilder.newClient();

        expectedCourses.forEach(course -> {
            String createURL = "http://moxie.cs.oswego.edu:13125/manage/professor/courses/course/create/";
            WebTarget target = client.target(createURL);
            target.request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(jsonb.toJson(course), MediaType.APPLICATION_JSON));
        });
    }

    @AfterEach
    public void teardown() {

        expectedCourses.forEach(course -> {
            String deleteURL = "http://moxie.cs.oswego.edu:13125/manage/professor/courses/course/delete/";
            WebTarget target = client.target(deleteURL);
            target.request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(jsonb.toJson(course), MediaType.APPLICATION_JSON));
        });
        client.close();
    }

    @Test
    public void viewCoursesTest() {
        targetUrl = "courses/";

        WebTarget target = client.target(baseUrl + targetUrl);
        Response response = target.request().get();
        System.out.println("response: " + response.readEntity(String.class));

        // read each of the objects from the db as jsons!
        List courseObjects = jsonb.fromJson(response.readEntity(String.class), ArrayList.class);
        ArrayList<CourseDAO> courses = new ArrayList<>();

        for (Object o : courseObjects) {
            HashMap mapO = (HashMap) o;
            // new CourseDAO(abbreviation1, courseName1, courseSection1, crn1, semester1, year1)
            courses.add(new CourseDAO(
                    (String) mapO.get("abbreviation"),
                    (String) mapO.get("course_name"),
                    (String) mapO.get("course_section"),
                    (String) mapO.get("crn"),
                    (String) mapO.get("semester"),
                    (String) mapO.get("year")
            ));
        }

        ArrayList<CourseDAO> actualCourses = new ArrayList<>();

        // compare the courses we made before to the courses in the database.
        // This should NOT work if we entered anything funky that could mess things up db side...
        courses.forEach(course -> {
            for (CourseDAO c : expectedCourses) {
                if (c.courseID.equals(course.courseID))
                    actualCourses.add(course);
            }
        });

        // test passes if the courses were successfully entered into the db
        Assertions.assertEquals(expectedCourses.size(), actualCourses.size(), "Not all courses were retrieved.");
    }

    // compare the courses we made before to the courses in the database.
    // This should NOT work if we entered anything funky that could mess things up db side...

    @Test
    public void viewSpecificCourseTest() {

        // view course1
        targetUrl = "courses/CSC999-723-92674-Summer-2025/";

        WebTarget target = client.target(baseUrl + targetUrl);
        Response response = target.request().get();
        System.out.println("response: " + response.readEntity(String.class));

        // read each of the objects from the db as jsons!
        Object o = jsonb.fromJson(response.readEntity(String.class), Object.class);
        HashMap mapO = (HashMap) o;

        CourseDAO courseFromDB = new CourseDAO(
                (String) mapO.get("abbreviation"),
                (String) mapO.get("course_name"),
                (String) mapO.get("course_section"),
                (String) mapO.get("crn"),
                (String) mapO.get("semester"),
                (String) mapO.get("year")
        );

        // test passes if we found the course we're looking for
        Assertions.assertEquals(courseFromDB, course1, "Course not found.");
    }

     @Test
     public void viewStudentsTest() {
         targetUrl = "students/";

         WebTarget target = client.target(baseUrl + targetUrl);
         Response response = target.request().get();
         System.out.println("response: " + response.readEntity(String.class));

         // read each of the objects from the db as jsons!
         List studentObjects = jsonb.fromJson(response.readEntity(String.class), ArrayList.class);
         ArrayList<StudentDAO> students = new ArrayList<>();

         for (Object o : studentObjects) {
             HashMap mapO = (HashMap) o;
             students.add(new StudentDAO(
                     (String) mapO.get("student_id")
             ));
         }

         ArrayList<StudentDAO> actualStudents = new ArrayList<>();

         // compare the courses we made before to the courses in the database.
         // This should NOT work if we entered anything funky that could mess things up db side...
         students.forEach(student -> {
             for (StudentDAO sD : expectedStudents) {
                 if (sD.studentID.equals(student.studentID))
                     actualStudents.add(sD);
             }
         });

         // test passes if the courses were successfully entered into the db
         Assertions.assertEquals(expectedStudents.size(), actualStudents.size(), "Not all students were retrieved.");
     }

     @Test
     public void viewSpecificStudentTest() {
         targetUrl = "students/"+ student1.studentID +"/";

         WebTarget target = client.target(baseUrl + targetUrl);
         Response response = target.request().get();
         System.out.println("response: " + response.readEntity(String.class));

         // read each of the objects from the db as jsons!
         Object o = jsonb.fromJson(response.readEntity(String.class), Object.class);
         HashMap mapO = (HashMap) o;

         StudentDAO studentFromDB = new StudentDAO(
                 (String) mapO.get("student_id")
         );

         // test passes if we found the course we're looking for
         Assertions.assertEquals(studentFromDB.studentID, student1.studentID, "Student not found.");
     }
}
