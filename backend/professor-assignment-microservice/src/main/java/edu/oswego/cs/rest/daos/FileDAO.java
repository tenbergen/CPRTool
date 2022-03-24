package edu.oswego.cs.rest.daos;
import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.io.*;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public class FileDAO {
    private String filename;
    private String courseID;
    private InputStream file;
    private int assignmentID;
//    v this is how UPLOAD_FOLDER WILL END UP LOOKING LIKE v
//  private static final String UPLOAD_FOLDER = DB.findHwFolder(course, filename);

    /**
     * Takes form-data from a POST request, converts it to an inputStream, and return the FileDOA containing
     * the files' information including the inputStream
     *
     * @param fileName form-data String representation of file name
     * @param courseID String
     * @param attachment form-data
     * @return FileDAO Instance
     * @throws IOException File Corruption Exception
     */
    public static FileDAO fileFactory(String fileName, String courseID, IAttachment attachment, int assignmentID) throws IOException {
//        String courseName = fileName.split("\\.")[0];
        InputStream inputStream = attachment.getDataHandler().getInputStream();
        System.out.println("fileName: " + fileName + "courseID: " + courseID);
        return new FileDAO(fileName, courseID, inputStream, assignmentID);
    }

    /**
    * Writes the inputStream to a file.
    * */
    public void writeFile (String filePath) throws IOException {
        OutputStream outputStream = new FileOutputStream(filePath);
        outputStream.write(file.readAllBytes());
        outputStream.close();
    }

    /**
     * Retrieves the relative location of the root Directory
     *
     * @return String directory location the hw files should be saved to
     * */
    public static String getProjectRootDir(){
        String path = (System.getProperty("user.dir").contains("\\")) ? System.getProperty("user.dir").replace("\\", "/") : System.getProperty("user.dir");
        String[] slicedPath = path.split("/");
        String targetDir = "professor-assignment-microservice";
        int i;
        StringBuilder relativePathPrefix = new StringBuilder();
        System.out.println(Arrays.toString(slicedPath));
        for (i = slicedPath.length - 1; ! slicedPath[i].equals(targetDir); i--) {
            relativePathPrefix.append("../");
        }
        if (System.getProperty("user.dir").contains("\\")) {
            relativePathPrefix = new StringBuilder(relativePathPrefix.toString().replace("/", "\\"));
        }
        return relativePathPrefix.toString();
    }
    public static void nullFiles(InputStream stream){
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Non-file attachment value: " + sb.toString());
    }
}
