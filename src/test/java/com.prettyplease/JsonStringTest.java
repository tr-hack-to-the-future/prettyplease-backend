package com.prettyplease;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prettyplease.model.RequestStatus;
import com.prettyplease.model.tables.Sponsor;
import org.json.JSONStringer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonStringTest {

    @Test
    public void createJsonString() {

        String jsonString = new JSONStringer()
                .array()
                .object()
                .key("sponsorId")
                .value("ABCD-1242324")
                .endObject()
                .endArray()
                .toString();

        System.out.println(jsonString);

    }

    @Test
    public void createJacksonString() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> r = new HashMap<>();
        r.put("sponsorId", "MHSKDJ*23223s");
        System.out.println(objectMapper.writeValueAsString(r));
    }

    @Test
    public void createObject() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String sponsorJson = "{\n" +
                "    \"sponsorId\" : \"SPON19\",\n" +
                "    \"name\": \"Postman's Horse Farm Organisation\",\n" +
                "    \"description\": \"Etiam vel nisi lacinia, luctus turpis et, rutrum ipsum. \",\n" +
                "    \"imageUrl\": \"./assets/images/abstract-logo1.jpg\",\n" +
                "    \"webUrl\": \"test post web url\"\n" +
                "}";
        Sponsor s = mapper.readValue(sponsorJson, Sponsor.class);
        System.out.println(s.toString());
    }


    @Test
    public void checkHttpStatus() {
        System.out.println(HttpStatus.OK);
    }

    @Test
    public void checkRequestStatus() {
        System.out.println(RequestStatus.ACCEPTED);
        assertEquals("ACCEPTED", RequestStatus.ACCEPTED.toString());
    }
}
