Feature: Create Transaction
  As a customer
  I want to create a transaction given a reference(optional), iban, date(optional), amount, fee(optional), description(optional)
  So that I can manage the transactions

  Scenario: Create transactions with all the parameters
    When Creating a transaction providing all the parameters
    Then It should create it and return a 200 status
    When Creating a transaction that will make the account balance go below 0
    Then It should not create it and return a 400 status

  Scenario: Create transactions with a missing parameter
    When Creating a transaction without reference
    Then It should create it with a random uuid and return a 200 status
    When Creating a transaction without date
    Then It should create it and return a 200 status
    When Creating a transaction without fee
    Then It should create it and return a 200 status
    When Creating a transaction without description
    Then It should create it and return a 200 status