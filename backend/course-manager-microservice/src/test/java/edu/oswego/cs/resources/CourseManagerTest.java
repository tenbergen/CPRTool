package edu.oswego.cs.resources;

import edu.oswego.cs.daos.CourseDAO;
import org.junit.jupiter.api.*;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.Client;

class CourseMangerTest {
//    private static String port;
//    private static String baseUrl;
//    private static String targetUrl;
//
//    private Client client;
//    private static final Jsonb jsonb = JsonbBuilder.create();
//
//    @BeforeAll
//    public static void oneTimeSetup() {
//        port = "13127";
//        baseUrl = "http://localhost:" + port + "/professor/";
//    }
//
//    @BeforeEach
//    public void setup() {
//        client = ClientBuilder.newClient();
//    }
//
//    @AfterEach
//    public void teardown() {
//        client.close();
//    }
//
//    @Test
//    public void testCreateCourse() {
//        String courseName = "Software-Design";
//        int courseSection = 800;
//        CourseDAO expectedCourse = new CourseDAO(courseName, courseSection);
//
//        targetUrl = "/courses/create/";
//        WebTarget target = client.target(baseUrl + targetUrl);
//
//        String response = target.request(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .post(Entity.entity(jsonb.toJson(expectedCourse), MediaType.APPLICATION_JSON), String.class);
//
//            Assertions.assertEquals(expectedCourse.toString(), response,"Course was not created properly.");
//    }
//
////    @Test
////    public void testAddStudent(){
////        int ID = 123480;
////        String email = "user@oswego.edu";
////        String name = "UserFirst";
////
////        //UserDAO expectedUser = new UserDAO(ID,email,name);
////        targetUrl = "/courses/course/student/add/";
////        WebTarget target = client.target(baseUrl + targetUrl);
////
////        String response = target.request(MediaType.APPLICATION_JSON)
////                .accept(MediaType.APPLICATION_JSON)
////                .post(Entity.entity(jsonb.toJson(expectedUser), MediaType.APPLICATION_JSON), String.class);
////        Assertions.assertEquals(expectedUser.toString(), response,"Student was not added properly.");
////
////    }
//
//    @Test
//    public void testDeleteStudent(){
//      /*  int ID = 123;
//        String email = "testemail@oswego.edu";
//        String name = "testname";
//
//        UserDAO user1 = new UserDAO(ID,email,name);
//
//        int ID1 = 123;
//        String email1 = "testemail@oswego.edu";
//        String name1 = "testname";
//
//        UserDAO user2 = new UserDAO(ID,email,name);
//
//        ArrayList<UserDAO> course = new ArrayList<UserDAO>();
//        course.add(user1);
//        course.add(user2);*/
//    }

}