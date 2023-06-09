INSERT INTO BANK_ACCOUNT (iban, balance) VALUES ('ES9820385778983000760236', 1500);
INSERT INTO BANK_ACCOUNT (iban, balance) VALUES ('ES1111111111111111111111', 13);
INSERT INTO BANK_ACCOUNT (iban, balance) VALUES ('ES2222222222222222222222', 1300);
INSERT INTO BANK_ACCOUNT (iban, balance) VALUES ('ES3333333333333333333333', 5);
INSERT INTO TRANSACTION (reference, IBAN, date, amount, fee, description) VALUES ('IbanTest', 'ES9820385778983000760236', '2019-08-21 12:42:12', 12, 1, 'Test description');
INSERT INTO TRANSACTION (reference, IBAN, date, amount, fee, description) VALUES ('IbanTestExpensive', 'ES9820385778983000760236', '2019-08-21 12:42:12', 15, 1, 'Test description');
INSERT INTO TRANSACTION (reference, IBAN, date, amount, fee, description) VALUES ('AlternativeIban', 'ES1111111111111111111111', '2024-08-21 12:42:12', 10, 1, 'Test description');
INSERT INTO TRANSACTION (reference, IBAN, date, amount, fee, description) VALUES ('TransactionBeforeToday', 'ES2222222222222222222222', '2000-08-21 12:42:12', 12, 1, 'Test description');
INSERT INTO TRANSACTION (reference, IBAN, date, amount, fee, description) VALUES ('TransactionAfterToday', 'ES2222222222222222222222', '2999-08-21 12:42:12', 12, 1, 'Test description');
INSERT INTO TRANSACTION (reference, IBAN, date, amount, fee, description) VALUES ('AccountBalanceBelowZero', 'ES3333333333333333333333', '2019-08-21 12:42:12', 12, 1, 'Test description');
