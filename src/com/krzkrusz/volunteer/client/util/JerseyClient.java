package com.krzkrusz.volunteer.client.util;

import com.krzkrusz.volunteer.client.model.ComputationResult;
import com.krzkrusz.volunteer.client.model.FunctionDTO;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class JerseyClient {

    private String baseUri;
    private String RESULT_REST_URI;
    private String GOLDBACH_REST_URI;
    private String FLOPS_REST_URI;
    private String SORT_REST_URI;

    Client client = ClientBuilder.newClient();

    public JerseyClient() throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream("src/main/resources/config.properties"));
        for (Enumeration<?> e = props.propertyNames(); e.hasMoreElements(); ) {
            String name = (String) e.nextElement();
            String value = props.getProperty(name);
            // now you have name and value
            if (name.startsWith("address")) {
                baseUri = value;
                RESULT_REST_URI = baseUri + "/result";
                GOLDBACH_REST_URI = baseUri + "/goldbach";
                FLOPS_REST_URI = baseUri + "/flops";
                SORT_REST_URI = baseUri + "/sort";
            }
        }
    }


    public FunctionDTO getGoldbachFunction() {
        return client
                .target(GOLDBACH_REST_URI)
                .request(MediaType.APPLICATION_JSON)
                .get(FunctionDTO.class);
    }

    public FunctionDTO getFlopsFunction() {
        return client
                .target(FLOPS_REST_URI)
                .request(MediaType.APPLICATION_JSON)
                .get(FunctionDTO.class);
    }

    public FunctionDTO getSortFunction() {
        return client
                .target(SORT_REST_URI)
                .request(MediaType.APPLICATION_JSON)
                .get(FunctionDTO.class);
    }

    public Response sendComputationResult(ComputationResult result) {
        return client
                .target(RESULT_REST_URI)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(result, MediaType.APPLICATION_JSON));
    }
}
