# Moneytransfer h/w submission
Design and implement a RESTful API (including data model and the backing implementation)
for money transfers between accounts.

## Techstack
- Sparkjava
- Gson
- Slf4j
- Junit
- Mockito
- AssertJ
- Rest-Assured

## Usage
./mvnw exec:java

## Method table
| HTTP METHOD | PATH | USAGE |
| -----------| ------ | ------ |
| GET | /accounts| get all accounts | 
| GET | /accounts/:accountId | get account by accountId | 
| POST | /accounts | create a new account
| DELETE | /account/:accountId | delete account by accountId | 
| GET | /transactions | get all transactions|
| GET | /transactions/:id | get transaction by transactionId |
| GET | /transactions?accountId=:accountid | get all transactions where accountId was involved |
| POST| /transactions | create transaction. Will return transaction object with success/not success status |