package com.example.gswtest.steps;

import com.example.gswtest.model.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;


public class SearchStepDefinitions {

    private static final String SEARCH_PATH = "/api/test/searchTransaction";
    private Mono<String> stringSearchResponse;
    private final InputStream searchRequest = SearchStepDefinitions.class.
            getResourceAsStream("/input_schemas/searchTransactionRequest.json");
    private final InputStream searchResponse = SearchStepDefinitions.class.
            getResourceAsStream("/input_schemas/searchTransactionResponse.json");

    private final String jsonSearchRequest;

    {
        assert searchRequest != null;
        jsonSearchRequest = new Scanner(searchRequest, StandardCharsets.UTF_8).useDelimiter("\\Z").next();
    }

    private final String jsonSearchResponse;

    {
        assert searchResponse != null;
        jsonSearchResponse = new Scanner(searchResponse, StandardCharsets.UTF_8).useDelimiter("\\Z").next();
    }

    private final WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080").build();

    @When("Searching for transactions without providing any parameters")
    public void searchingForTransactionsWithoutProvidingAnyParameters() {
       this.stringSearchResponse = webClient.post().uri(SEARCH_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("{}"))
                .retrieve()
                .bodyToMono(String.class);

    }

    @Then("It should show the latest {int} transactions made")
    public void itShouldShowTheLatestTransactionsMade(int transactions) {
        StepVerifier.create(stringSearchResponse)
                .expectNextMatches(response -> response.contains("\"reference\":\"IbanTest\"") && response.contains("\"reference\":\"AlternativeIban\""))
                .verifyComplete();
    }

    @When("Searching for transactions providing Iban and sort asc")
    public void searchingForTransactionsProvidingIbanAndSort() {
        this.stringSearchResponse = webClient.post().uri(SEARCH_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(jsonSearchRequest))
                .retrieve()
                .bodyToMono(String.class);
    }

    @Then("It should show transactions made to that Iban and sorted ascendant")
    public void itShouldShowTransactionsMadeToThatIbanAndSortedAscendant() {
        StepVerifier.create(stringSearchResponse)
                .expectNextMatches(response -> response.contains("\"iban\":\"ES9820385778983000760236\"")
                        && response.contains("\"reference\":\"IbanTest\"")
                        && response.contains("\"reference\":\"IbanTestExpensive\"")
                        && !response.contains("\"reference\":\"AlternativeIban\""))
                .verifyComplete();
        StepVerifier.create(stringSearchResponse.map(SearchStepDefinitions::ascendingIndexes))
                .expectNext(true)
                .verifyComplete();
    }

    @When("Searching for transactions providing Iban and sort desc")
    public void searchingForTransactionsProvidingIbanAndSortDesc() {
        this.stringSearchResponse = webClient.post().uri(SEARCH_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(jsonSearchRequest.replace("asc", "desc")))
                .retrieve()
                .bodyToMono(String.class);
    }

    @Then("It should show transactions made to that Iban and sorted descendant")
    public void itShouldShowTransactionsMadeToThatIbanAndSortedDescendant() {
        StepVerifier.create(stringSearchResponse)
                .expectNextMatches(response -> response.contains("\"iban\":\"ES9820385778983000760236\"")
                        && response.contains("\"reference\":\"IbanTest\"")
                        && response.contains("\"reference\":\"IbanTestExpensive\"")
                        && !response.contains("\"reference\":\"AlternativeIban\""))
                .verifyComplete();
        StepVerifier.create(stringSearchResponse.map(SearchStepDefinitions::ascendingIndexes))
                .expectNext(false)
                .verifyComplete();
    }

    @When("Searching for transactions providing just Iban")
    public void searchingForTransactionsProvidingJustIban() {
        this.stringSearchResponse = webClient.post().uri(SEARCH_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(jsonSearchRequest.replace("sort:asc", "")))
                .retrieve()
                .bodyToMono(String.class);
    }

    private static Boolean ascendingIndexes(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Transaction> transactions;

        try {
            transactions = objectMapper.readValue(response, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return transactions.get(0).getReference().equals("IbanTest")
                && transactions.get(1).getReference().equals("IbanTestExpensive");
    }
}
