package com.example.gswtest.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
public class CreateStepDefinitions {

    private static final String CREATE_PATH = "/api/test/createTransaction";
    Pattern UUID_REGEX =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    private  final InputStream createRequest = SearchStepDefinitions.class.
            getResourceAsStream("/input_schemas/createTransactionRequest.json");

    private final String jsonCreateRequest;

    {
        assert createRequest != null;
        jsonCreateRequest = new Scanner(createRequest, StandardCharsets.UTF_8).useDelimiter("\\Z").next();
    }

    private final WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080").build();
    private Mono<ClientResponse> createTransactionClientResponse;
    private Mono<String> createTransactionStringResponse;

    @When("Creating a transaction providing all the parameters")
    public void creating_a_transaction_providing_all_the_parameters() {
        createTransactionClientResponse = getCreateTransactionClientResponse(jsonCreateRequest);

    }
    @Then("It should create it and return a {int} status")
    public void it_should_create_it_and_return_a_status(Integer int1) {
        StepVerifier.create(createTransactionClientResponse)
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @When("Creating a transaction without reference")
    public void creatingATransactionWithoutReference() {
        WebClient.RequestHeadersSpec<?> client = webClient.post().uri(CREATE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(jsonCreateRequest.replace("\"reference\":\"createTransactionTest\",", "")));
        createTransactionClientResponse = client
                .exchangeToMono(clientResponse -> {
                    if(clientResponse.statusCode().is2xxSuccessful()) {
                        return Mono.just(clientResponse);
                    }

                    return clientResponse.createException().flatMap(Mono::error);
                });
        createTransactionStringResponse = client.retrieve().bodyToMono(String.class);
    }

    @Then("It should create it with a random uuid and return a {int} status")
    public void itShouldCreateItWithARandomUuidAndReturnAStatus(int arg0) {
        StepVerifier.create(createTransactionClientResponse)
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
        StepVerifier.create(createTransactionStringResponse.map(clientResponse -> UUID_REGEX.matcher(clientResponse).matches()))
                .expectNext(true)
                .verifyComplete();
    }

    @When("Creating a transaction without date")
    public void creatingATransactionWithoutDate() {
        createTransactionClientResponse = getCreateTransactionClientResponse(jsonCreateRequest.replace("\"date\":\"2023-03-14T10:42:12.000+00:00\",", ""));
    }

    @When("Creating a transaction without fee")
    public void creatingATransactionWithoutFee() {
        createTransactionClientResponse = getCreateTransactionClientResponse(jsonCreateRequest.replace("\"fee\":1.00,", ""));
    }
    @When("Creating a transaction without description")
    public void creatingATransactionWithoutDescription() {
        createTransactionClientResponse = getCreateTransactionClientResponse(jsonCreateRequest.replace(",\"description\":\"Restaurant payment\"", ""));
    }

    @When("Creating a transaction that will make the account balance go below {int}")
    public void creatingATransactionThatWillMakeTheAccountBalanceGoBelow(int arg0) {
        createTransactionClientResponse = getCreateTransactionClientResponse(jsonCreateRequest
                .replace("ES1111111111111111111111", "ES3333333333333333333333")
                .replace("createTransactionTest", "AccountBalanceBelowZero")
                .replace("\"amount\":17.00", "\"amount\":-17.00"));
    }

    @Then("It should not create it and return a {int} status")
    public void itShouldNotCreateItAndReturnAStatus(int arg0) {
        StepVerifier.create(createTransactionClientResponse)
                .expectErrorSatisfies(throwable -> assertThat(throwable).satisfies(ex -> {
                    WebClientResponseException responseEx = (WebClientResponseException) ex;
                    assertTrue(responseEx.getStatusCode().is4xxClientError());
                })
                )
                .verify();
    }

    private Mono<ClientResponse> getCreateTransactionClientResponse(String jsonCreateRequest) {
        return webClient.post().uri(CREATE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(jsonCreateRequest))
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return Mono.just(clientResponse);
                    }

                    return clientResponse.createException().flatMap(Mono::error);
                });
    }

    public void createATransactionWithDateEqualsToday(){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        log.info(jsonCreateRequest
                .replace("createTransactionTest", "TransactionEqualsToday")
                .replace("ES1111111111111111111111", "ES2222222222222222222222")
                .replace("2023-03-14T10:42:12.000+00:00", dateFormat.format(date))
                .replace("\"amount\":17.00","\"amount\":12.00"));
        Mono<ClientResponse> clientResponseMono = webClient.post().uri(CREATE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(jsonCreateRequest
                        .replace("createTransactionTest", "TransactionEqualsToday")
                        .replace("ES1111111111111111111111", "ES2222222222222222222222")
                        .replace("2023-03-14T10:42:12.000+00:00", dateFormat.format(date))
                        .replace("\"amount\":17.00","\"amount\":12.00")))
                .exchangeToMono(clientResponse -> {
                    if(clientResponse.statusCode().is2xxSuccessful()) {
                        return Mono.just(clientResponse);
                    }

                    return clientResponse.createException().flatMap(Mono::error);
                });
        StepVerifier.create(clientResponseMono)
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}
