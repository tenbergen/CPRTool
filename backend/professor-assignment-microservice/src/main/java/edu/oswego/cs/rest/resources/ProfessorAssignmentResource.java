package edu.oswego.cs.rest.resources;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import edu.oswego.cs.rest.daos.FileDAO;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("professor")
public class ProfessorAssignmentResource {

    public ProfessorAssignmentResource() {
    }

    /**
     * File is uploaded as form-data and passed back as a List<IAttachment>
     * The attachment is processed in FileDao.FileFactory, which reads and
     * reconstructs the file through inputStream and outputStream respectively
     *
     * @param attachments type List<IAttachment>: file(s) passed back as form-data
     * @return Response
     * */
    @POST
    @Produces({MediaType.MULTIPART_FORM_DATA, "application/pdf", MediaType.TEXT_PLAIN})
    @Path("/courses/course/assignments/upload")
    public Response postFormData(List<IAttachment> attachments) throws IOException{

        InputStream stream = null;
        for (IAttachment attachment : attachments) {
            if (attachment == null) {continue;}
            String fileName = attachment.getDataHandler().getName();

            if (fileName == null) {
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
            } else {
                FileDAO.FileFactory(fileName,attachment);
//                DB.getFileDao(FileDAO.FileFactory(fileName,attachment));
            }
            if (stream != null) {
                stream.close();
            }
        }
        return Response.status(Response.Status.OK).build();
    }
}
