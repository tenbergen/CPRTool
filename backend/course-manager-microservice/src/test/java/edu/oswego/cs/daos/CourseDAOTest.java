package edu.oswego.cs.daos;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

public class CourseDAOTest {
//
//    @Test
//    public void courseDAOTest() {
//        String courseName = "Software-Design";
//        int courseSection = 800;
//        CourseDAO courseDAO = new CourseDAO(courseName, courseSection);
//        Assertions.assertEquals("Software-Design", courseDAO.getCourseName());
//        Assertions.assertEquals(800, courseDAO.getCourseSection());
//    }
//
//    @Test
//    public void jsonToCourseDAOTest() {
//        Jsonb jsonb = JsonbBuilder.create();
//        String courseName = "Software-Design";
//        int courseSection = 800;
//        CourseDAO courseDAO = new CourseDAO(courseName, courseSection );
//        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(courseDAO), MediaType.APPLICATION_JSON_TYPE);
//
//        Assertions.assertEquals(courseDAO.toString(), courseDAOEntity.getEntity(), "CourseDAO was not parsed correctly by jsonb.");
//    }

}
