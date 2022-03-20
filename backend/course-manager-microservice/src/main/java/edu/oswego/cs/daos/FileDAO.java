package edu.oswego.cs.daos;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class FileDAO {

    private String filename;
    private List<String> csvLines;

    /**
     * Takes form-data from a POST request for a csv file and reconstructs the content within the file
     *
     * @param attachments form-data
     * @return FileDAO Instance
     * @throws Exception File Corruption Exception
     */
    public static FileDAO FileFactory(List<IAttachment> attachments) throws Exception {
        ArrayList<String> csvLines = new ArrayList<>();

        for (IAttachment attachment : attachments) {
            if (attachment == null) continue;
            String fileName = attachment.getDataHandler().getName();

            if (fileName != null) {
                InputStream stream = attachment.getDataHandler().getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line = "";
                try {
                    while ((line = reader.readLine()) != null)
                        if (!line.isEmpty()) csvLines.add(line);
                    if (csvLines.size() == 0) continue;
                    reader.close();
                    return new FileDAO(fileName, csvLines);
                } catch (IOException ignored) {
                }
            }
        }
        throw new Exception();
    }

}
