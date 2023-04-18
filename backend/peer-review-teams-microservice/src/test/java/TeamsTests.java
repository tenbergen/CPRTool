import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.StudentDAO;
import edu.oswego.cs.daos.TeamDAO;
import edu.oswego.cs.database.CourseInterface;
import edu.oswego.cs.database.TeamInterface;
import edu.oswego.cs.requests.TeamParam;

    public class TeamsTests {
        @Test
        /**
         * Test case for createTeamProffessor
         * Creates test parameters by:
         * Create a test courese, add student to test course,Create team parameter with studentID and teamName,Create security context
         * Calls createTeamProffessor with created test paramaters
         * Verify team created and team member inserted
         */
        public void testTeamCreation() {
            CourseDAO course = new CourseDAO("CSC578", "Software Engineering", "800", "12345", "Fall", "2024");
            CourseInterface courseInterface = new CourseInterface();
            courseInterface.addCourse(null, course);
            String email = "timmyTest@oswego.edu";
            StudentDAO studentDAO = new StudentDAO(email, course.abbreviation, course.courseName, course.courseSection,
                    course.crn, course.semester, course.year);
            courseInterface.addStudent(null, studentDAO, course.courseID);
            TeamParam teamParam = new TeamParam();
            teamParam.setCourseID(course.courseID);
            teamParam.setTeamName("Test Team");
            String studentID = email;
            SecurityContext securityContext = mock(SecurityContext.class);
            TeamInterface teamInterface = new TeamInterface();
            teamInterface.createTeamProffessor(securityContext, teamParam, studentID);
            TeamDAO insertedTeam = teamInterface.getTeamsByCourseID(course.courseID).get(0);
            assertEquals(teamParam.getTeamName(), insertedTeam.getTeamName());
            assertEquals(teamParam.getCourseID(), insertedTeam.getCourseID());
            assertEquals(1, insertedTeam.getTeamMembers().size());
            assertTrue(insertedTeam.getTeamMembers().contains(studentID));
        }
    }
