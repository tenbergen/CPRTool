package edu.oswego.cs.daos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileDAO {

    private static String assignment_path = "assignments/";

    public static void zipPeerReview(Map<String, List<String>> teamAssignments, String courseID, int assignmentID) throws IOException  {
        List<File> files = Arrays.asList(new File(assignment_path + courseID + "/" + assignmentID).listFiles());
        List<String> fileNames = files.stream().map(File::getName).collect(Collectors.toList());

        teamAssignments.keySet().forEach( (teamName) -> {
            String zipfilename = "peer-reviews/for-"+teamName+".zip";
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
            } catch (IOException e) {  }
        });

    }

}
