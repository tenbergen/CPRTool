package edu.oswego.cs.daos;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileDAO {

    public static String assignment_path = "assignments/";
    public static String peer_review_path = "peer-reviews/";
    public static String peer_review_submission_path = "peer-review-submissions/";

    public String fileName, courseID, srcTeamName, destTeamName;
    public int assignmentID;
    public InputStream inputStream;

    public FileDAO(String fileName, String courseID, String srcTeamName, String destTeamName, int assignmentID,  InputStream inputStream) {
        this.fileName = fileName;
        this.courseID = courseID;
        this.srcTeamName = srcTeamName;
        this.destTeamName = destTeamName;
        this.assignmentID = assignmentID;
        this.inputStream = inputStream;
    }

    public static void zipPeerReview(Map<String, List<String>> teamAssignments, String courseID, int assignmentID) throws IOException  {
        List<File> files = Arrays.asList(new File(assignment_path + courseID + "/" + assignmentID).listFiles());
        List<String> fileNames = files.stream().map(File::getName).collect(Collectors.toList());

        teamAssignments.keySet().forEach( (teamName) -> {
            String path = "peer-reviews/" + courseID+"/"+assignmentID+"/";
            if (! new File(path).exists()) {
                new File(path).mkdirs();
            }
            String zipfilename = path+ "for-"+teamName+".zip";

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(zipfilename);
                ZipOutputStream zipOutputStream  = new ZipOutputStream(fileOutputStream);
                for (String assigned : teamAssignments.get(teamName)) {

                    String filename = fileNames.stream().filter(fn -> fn.contains(assigned)).findFirst().get();
                    zipOutputStream.putNextEntry(new ZipEntry("for-" + teamName+ "/"+filename));
                    byte[] fileBytes = Files.readAllBytes(Paths.get(assignment_path+courseID+"/"+assignmentID+"/"+filename));
                    zipOutputStream.write(fileBytes, 0, fileBytes.length);
                    zipOutputStream.closeEntry();
                }
                zipOutputStream.close();
            } catch (IOException e) { e.printStackTrace(); }
        });

    }

    public static FileDAO fileFactory(
            String courseID,
            String srcTeamName, String destTeamName,
            int assignmentID, IAttachment attachment) throws IOException {
        InputStream inputStream = attachment.getDataHandler().getInputStream();
        String fileName = "peer-review-from-" + srcTeamName + "-to-" + destTeamName;
        return new FileDAO(fileName, courseID, srcTeamName, destTeamName, assignmentID, inputStream);
    }

}
