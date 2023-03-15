package com.example.gswtest.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class StatusStepDefinitions {
    private static final String STATUS_PATH = "/api/test/findStatus";
    private final InputStream statusRequest = SearchStepDefinitions.class.
            getResourceAsStream("/input_schemas/transactionStatusRequest.json");

    private final String jsonStatusRequest;

    {
        assert statusRequest != null;
        jsonStatusRequest = new Scanner(statusRequest, StandardCharsets.UTF_8).useDelimiter("\\Z").next();
    }

    private final WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080").build();
    private Mono<String> statusTransactionStringResponse;
    private String transaction;

    @Given("A transaction that is stored in our system and date is before today")
    public void aTransactionThatIsStoredInOurSystemAndDateIsBeforeToday() {
        this.transaction = "TransactionBeforeToday";
    }

    @Given("A transaction that is stored in our system and date is greater today")
    public void aTransactionThatIsStoredInOurSystemAndDateIsGreaterToday() {
        this.transaction = "TransactionAfterToday";
    }

    @Given("A transaction that is stored in our system and date is equals to today")
    public void aTransactionThatIsStoredInOurSystemAndDateIsEqualsToToday() {
        CreateStepDefinitions stepDefinitions = new CreateStepDefinitions();
        stepDefinitions.createATransactionWithDateEqualsToday();
        this.transaction= "TransactionEqualsToday";
    }

    @Then("The system returns the status {string} And the amount substracting the fee")
    public void theSystemReturnsTheStatusSETTLEDAndTheAmountSubstractingTheFee(String status) {
        StepVerifier.create(statusTransactionStringResponse)
                .expectNextMatches(response -> response.contains("\"reference\":\"" + this.transaction + "\"")
                        && response.contains("\"status\":\"" + status + "\"")
                        && response.contains("\"amount\":11.0"))
                .verifyComplete();
    }

    @When("I check the status from {string} channel And the transaction date is before today")
    public void iCheckTheStatusFromINTERNALChannelAndTheTransactionDateIsBeforeToday(String status) {
        this.statusTransactionStringResponse = getStatusTransactionStringResponse(jsonStatusRequest
                .replace("CLIENT", status)
                .replace("test", this.transaction));
    }

    @Then("The system returns the status {string} And the amount And the fee")
    public void theSystemReturnsTheStatusSETTLEDAndTheAmountAndTheFee(String status) {
        StepVerifier.create(statusTransactionStringResponse)
                .expectNextMatches(response -> response.contains("\"reference\":\"" + this.transaction + "\"")
                        && response.contains("\"status\":\"" + status + "\"")
                        && response.contains("\"amount\":12.0")
                        && response.contains("\"fee\":1"))
                .verifyComplete();
    }

    private Mono<String> getStatusTransactionStringResponse(String jsonStatusRequest) {
        return webClient.post().uri(STATUS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(jsonStatusRequest))
                .retrieve()
                .bodyToMono(String.class);
    }



    @When("I check the status from {string} channel And the transaction date is equals to today")
    public void iCheckTheStatusFromINTERNALChannelAndTheTransactionDateIsEqualsToToday(String channel) {
        this.statusTransactionStringResponse = getStatusTransactionStringResponse(jsonStatusRequest
                .replace("CLIENT", channel)
                .replace("test", this.transaction));
    }


    @When("I check the status from {string} channel And the transaction date is greater than today")
    public void iCheckTheStatusFromCLIENTAndTheTransactionDateIsGreaterThanToday(String channel) {
        this.statusTransactionStringResponse = getStatusTransactionStringResponse(jsonStatusRequest
                .replace("CLIENT", channel)
                .replace("test", this.transaction));
    }

}
