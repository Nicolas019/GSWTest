Feature: Search Transactions
  As an internal customer
  I want to search for transactions by iban and be able to sort them by amount ascending or descending
  So that I can manage the transactions
  (Assumption) Only Internal customers will be able to access this functionality as public information might be disclosed

  Scenario: Search with out any parameters
    When Searching for transactions without providing any parameters
    Then It should show the latest 50 transactions made

  Scenario: Search with iban and sort parameters
    When Searching for transactions providing Iban and sort asc
    Then It should show transactions made to that Iban and sorted ascendant
    When Searching for transactions providing Iban and sort desc
    Then It should show transactions made to that Iban and sorted descendant

  Scenario: Search with iban parameter
    When Searching for transactions providing just Iban
    Then It should show transactions made to that Iban and sorted ascendant
