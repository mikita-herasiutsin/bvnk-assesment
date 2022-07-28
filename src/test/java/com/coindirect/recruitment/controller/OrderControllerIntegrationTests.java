package com.coindirect.recruitment.controller;

import com.coindirect.recruitment.model.BookingDbo;
import com.coindirect.recruitment.repository.BookingRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.databind.node.JsonNodeType.STRING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerIntegrationTests {

    @Autowired
    private BookingRepository repository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestRestTemplate restTemplate;

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void shouldCreateNewBooking() throws JsonProcessingException {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put("Content-Type", List.of("application/json"));

        ResponseEntity<String> createResponse = restTemplate.postForEntity(
            "/api/orders/create",
            new HttpEntity<>(
                "{"
                    + "\"name\": \"name\","
                    + "\"row\": \"11\","
                    + "\"column\": \"9\""
                    + "}",
                headers
            ),
            String.class
        );

        assertThat(createResponse.getStatusCode(), is(OK));

        String bodyCreate = createResponse.getBody();
        assertThat(bodyCreate, notNullValue());

        JsonNode jsonNodeCreate = objectMapper.readTree(bodyCreate);
        JsonNode bookingId = jsonNodeCreate.get("bookingId");

        assertThat(bookingId.getNodeType(), is(STRING));
        assertThat(bookingId, notNullValue());
        assertThat(jsonNodeCreate.get("name").textValue(), is("name"));
        assertThat(jsonNodeCreate.get("row").textValue(), is("11"));
        assertThat(jsonNodeCreate.get("column").textValue(), is("9"));

        // check if booking was really saved

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
            "/api/orders/getByBookingId/" + bookingId.asText(),
            String.class
        );

        assertThat(getResponse.getStatusCode(), is(OK));

        String bodyGet = createResponse.getBody();
        assertThat(bodyGet, notNullValue());

        JsonNode jsonNodeGet = objectMapper.readTree(bodyGet);

        assertThat(bookingId.getNodeType(), is(STRING));
        assertThat(bookingId, notNullValue());
        assertThat(jsonNodeGet.get("name").textValue(), is("name"));
        assertThat(jsonNodeGet.get("row").textValue(), is("11"));
        assertThat(jsonNodeGet.get("column").textValue(), is("9"));
    }

    @Test
    public void shouldNotCreateNewBookingIfItAlreadyExists() throws JsonProcessingException {
        repository.save(new BookingDbo(null, "11", "9", "name"));

        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put("Content-Type", List.of("application/json"));

        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/orders/create",
            new HttpEntity<>(
                "{"
                    + "\"name\": \"name\","
                    + "\"row\": \"11\","
                    + "\"column\": \"9\""
                    + "}",
                headers
            ),
            String.class
        );

        assertThat(response.getStatusCode(), is(OK));

        String body = response.getBody();
        assertThat(body, notNullValue());

        JsonNode jsonNode = objectMapper.readTree(body);
        assertThat(jsonNode.get("message").textValue(), is("The position is already booked"));
    }

    @Test
    public void shouldReturnTrueIfBookingIsAvailableForProvidedRowAndColumn() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/orders/isAvailable/row1/column2",
            String.class
        );

        assertThat(response.getStatusCode(), is(OK));

        String body = response.getBody();
        assertThat(body, notNullValue());

        JsonNode jsonNode = objectMapper.readTree(body);
        assertThat(jsonNode.get("available").asBoolean(), is(true));
    }

    @Test
    public void shouldReturnFalseIfBookingIsNotAvailableForProvidedRowAndColumn() throws JsonProcessingException {
        repository.save(new BookingDbo(null, "2", "4", "name"));

        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/orders/isAvailable/2/4",
            String.class
        );

        assertThat(response.getStatusCode(), is(OK));

        String body = response.getBody();
        assertThat(body, notNullValue());

        JsonNode jsonNode = objectMapper.readTree(body);
        assertThat(jsonNode.get("available").asBoolean(), is(false));
    }

    @Test
    public void shouldReturnNotFoundIfNoSuchBookingForProvidedRowAndColumn() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/orders/getByPosition/row1/column2",
            String.class
        );

        assertThat(response.getStatusCode(), is(BAD_REQUEST));
    }

    @Test
    public void shouldReturnBookingForProvidedRowAndColumn() throws JsonProcessingException {
        BookingDbo saved = repository.save(new BookingDbo(null, "2", "4", "name"));

        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/orders/getByPosition/2/4",
            String.class
        );

        assertThat(response.getStatusCode(), is(OK));

        String body = response.getBody();
        assertThat(body, notNullValue());

        JsonNode jsonNode = objectMapper.readTree(body);

        assertThat(jsonNode.get("bookingId").getNodeType(), is(STRING));
        assertThat(jsonNode.get("bookingId").textValue(), is(saved.getId().toString()));
        assertThat(jsonNode.get("name").textValue(), is(saved.getName()));
        assertThat(jsonNode.get("row").textValue(), is(saved.getRow()));
        assertThat(jsonNode.get("column").textValue(), is(saved.getColumn()));
    }

    @Test
    public void shouldReturnNotFoundIfNoSuchBookingForProvidedBookingId() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/orders/getByBookingId/" + UUID.randomUUID(),
            String.class
        );
        assertThat(response.getStatusCode(), is(BAD_REQUEST));
    }

    @Test
    public void shouldReturnBookingForProvidedBookingId() throws JsonProcessingException {
        BookingDbo saved = repository.save(new BookingDbo(null, "2", "4", "name"));

        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/orders/getByBookingId/" + saved.getId(),
            String.class
        );

        assertThat(response.getStatusCode(), is(OK));

        String body = response.getBody();
        assertThat(body, notNullValue());

        JsonNode jsonNode = objectMapper.readTree(body);

        assertThat(jsonNode.get("bookingId").getNodeType(), is(STRING));
        assertThat(jsonNode.get("bookingId").textValue(), is(saved.getId().toString()));
        assertThat(jsonNode.get("name").textValue(), is(saved.getName()));
        assertThat(jsonNode.get("row").textValue(), is(saved.getRow()));
        assertThat(jsonNode.get("column").textValue(), is(saved.getColumn()));
    }

}
