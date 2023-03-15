Feature: Transaction status
  As a customer
  I want to consult the status of the transaction
  So that I can see the states of my transactions

  Scenario: Create transactions stored in our system and date is before today
    Given A transaction that is stored in our system and date is before today
    When I check the status from 'CLIENT' channel And the transaction date is before today
    Then The system returns the status 'SETTLED' And the amount substracting the fee
    When I check the status from 'ATM' channel And the transaction date is before today
    Then The system returns the status 'SETTLED' And the amount substracting the fee
    When I check the status from 'INTERNAL' channel And the transaction date is before today
    Then The system returns the status 'SETTLED' And the amount And the fee

  Scenario: Create transactions stored in our system and date is equals to today
    Given A transaction that is stored in our system and date is equals to today
    When I check the status from 'CLIENT' channel And the transaction date is equals to today
    Then The system returns the status 'PENDING' And the amount substracting the fee
    When I check the status from 'ATM' channel And the transaction date is equals to today
    Then The system returns the status 'PENDING' And the amount substracting the fee
    When I check the status from 'INTERNAL' channel And the transaction date is equals to today
    Then The system returns the status 'PENDING' And the amount And the fee

  Scenario: Create transactions stored in our system and date is greater today
    Given A transaction that is stored in our system and date is greater today
    When I check the status from 'CLIENT' channel And the transaction date is greater than today
    Then The system returns the status 'FUTURE' And the amount substracting the fee
    When I check the status from 'ATM' channel And the transaction date is greater than today
    Then The system returns the status 'PENDING' And the amount substracting the fee
    When I check the status from 'INTERNAL' channel And the transaction date is greater than today
    Then The system returns the status 'FUTURE' And the amount And the fee