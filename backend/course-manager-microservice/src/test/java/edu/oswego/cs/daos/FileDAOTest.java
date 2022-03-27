package edu.oswego.cs.daos;

//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;

// QA wonders if these are needed. THey caused errors on our end
//import com.ibm.websphere.jaxrs20.multipart.AttachmentBuilder;
//import com.ibm.websphere.jaxrs20.multipart.IAttachment;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileDAOTest {

    private static String port;
    private static String baseUrl;
    private static String targetUrl;

    private Client client;

    @BeforeAll
    public static void oneTimeSetup() {
        port = "13127";
        baseUrl = "http://localhost:" + port + "manage/professor/";
    }

}
