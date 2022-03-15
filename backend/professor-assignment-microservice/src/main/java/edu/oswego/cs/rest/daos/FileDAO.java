package edu.oswego.cs.rest.daos;
import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;

@Getter
@AllArgsConstructor
public class FileDAO {
    private String filename;
    private String course;
    private InputStream file;
//    v this is how UPLOAD_FOLDER WILL END UP LOOKING LIKE v
//  private static final String UPLOAD_FOLDER = DB.findHwFolder(course, filename);

    /**
     * Takes form-data from a POST request for a csv file and reconstructs the content within the file
     * @param fileName form-data String representation of file name
     * @param attachment form-data
     * @return FileDAO Instance
     * @throws IOException File Corruption Exception
     */
    public static FileDAO FileFactory(String fileName, IAttachment attachment) throws IOException {
        String courseName = fileName.split("\\.")[0];
        InputStream inputStream = attachment.getDataHandler().getInputStream();
        System.out.println("fileName: " + fileName + "courseName: " + courseName);
        return new FileDAO(fileName, courseName, inputStream);
    }
    /**
    * Writes the inputStream to a file.
    * */
    public void writeFile (String filePath) throws IOException {
        OutputStream outputStream = new FileOutputStream(filePath);
        outputStream.write(file.readAllBytes());
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
}
