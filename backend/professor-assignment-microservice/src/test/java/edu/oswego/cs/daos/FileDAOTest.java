package edu.oswego.cs.daos;

//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;

import com.ibm.websphere.jaxrs20.multipart.AttachmentBuilder;
import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import org.junit.jupiter.api.BeforeAll;

import javax.ws.rs.client.Client;

public class FileDAOTest {

    private static String port;
    private static String baseUrl;
    private static String targetUrl;

    private Client client;

    @BeforeAll
    public static void oneTimeSetup() {
        port = "13131";
        baseUrl = "http://localhost:" + port + "manage/professor/";
    }

}