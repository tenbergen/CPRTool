package edu.oswego.cs.rest.daos;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@AllArgsConstructor
public class FileDAO {
    private String filename;
    private String courseID;
    private byte[] file;
    private int assignmentID;
    private String teamName;

    /**
     * Takes form-data from a POST request, converts it to an inputStream, and return the FileDOA containing
     * the files' information including the inputStream
     *
     * @param fileName   form-data String representation of file name
     * @param courseID   String
     * @param attachment form-data
     * @return FileDAO Instance
     * @throws IOException File Corruption Exception or contains profanity
     * @throws TikaException File Corruption Exception
     * @throws SAXException File Corruption Exception
     */
    public static FileDAO fileFactory(String fileName, String courseID, IAttachment attachment, int assignmentID, String teamName) throws IOException, TikaException, SAXException {
        InputStream inputStream = new BufferedInputStream(attachment.getDataHandler().getInputStream());
        byte[] fileData = inputStream.readAllBytes();
        contentFilter(new ByteArrayInputStream(Base64.getDecoder().decode(new String(fileData))), fileName);
        inputStream.mark(inputStream.available());
        return new FileDAO(fileName, courseID, fileData, assignmentID, teamName);
    }

    /**
     * Checks the file for profanity
     *
     * @param stream InputStream
     * @throws IOException File Corruption Exception or contains profanity
     * @throws TikaException File Corruption Exception
     * @throws SAXException File Corruption Exception
     */
    public static void contentFilter(InputStream stream, String fileName) throws TikaException, IOException, SAXException {
        Pattern pattern = Pattern.compile("(?<=\\b)(a+r*ss+(ho+l)?e*s*|ba+ll+(sa+ck)?s*|ba+sta+rds*|bi+tch(e+s+|i+ng+)?|bu+ll+shi+t|bu+tt+(fu+ck)?s*|co+ck(blo+ck|su+cke+r)?s*|who+re+s*|cu+nts*|(go+d)?da+m[mn]+(it)?|di+ck(he+a+d|fo+rbra+i+n)?s*|fa+g+(o+t)?s*|(mo+the+r)?fu+ck(e+(rs|d)?|ing|off+)?s*|ja+ck(a+ss+|off+)|ni+gg+(e+r|a+)s*|shi+ts*)(?=\\b)", Pattern.CASE_INSENSITIVE);
        BodyContentHandler ch = new BodyContentHandler(-1);
        AutoDetectParser parser = new AutoDetectParser();
        parser.parse(stream, ch, new Metadata());
        Matcher matcher = pattern.matcher(ch.toString());
        boolean found = matcher.find(); // Don't have to worry about catching all matches as just one is enough to reject the file

        if (found) {
            throw new IOException("File contains profanity");
        }
    }
}
