# Pretty Please backend functions

This repo creates the backend API for connection to an AWS RDS database, to be consumed by the Pretty Please front-end React application, available [here](https://github.com/tr-hack-to-the-future/prettyplease-frontend).  

The hosted version of the application is available here: https://tr-hack-to-the-future.github.io/prettyplease-frontend/ 

## Technology
This project used the following:
- Java 8
- Maven
- Serverless Framework 
- Terraform
- AWS Lambda and API Gateway
- AWS RDS (MySQL 5.7)

## Setup
Download or clone this repo. The codebase uses Maven for dependency management and [Serverless Framework](https://www.serverless.com/) for deploying  to AWS Lambda.

To build the application:
```shell script
mvn clean install 
``` 
To deploy the Lambda functions to AWS:
```shell script
serverless deploy
```


## Configuration
- AWS keys set up for your Serverless installation to be referenced from the project [serverless.yml](./serverless.yml) file.  
- Create a **config.dev.json** file at the project level containing the following name/value pairs:
```json
{
  "DB_HOST" : "<database_host_name>",
  "DB_NAME" : "<database_name>",
  "DB_USER" : "<database_user>",
  "DB_PASSWORD" : "<database_password>"
} 
```

## Deployment
To deploy the Lambda functions run:
```shell script
serverless deploy 
```
This will provision the required API Gateway and upload the JAR file to AWS Lambda. 

## REST API
The definition of the REST API can be found [here](https://github.com/tr-hack-to-the-future/prettyplease-backend/blob/main/REST%20API%20README.md).


## Lambda HTTP Functions
```yaml
function: getsponsor
handler: com.prettyplease.SponsorHandler
path: sponsor/{sponsorId}
method: get

# GET
# sponsor table
  getsponsor:
    handler: com.prettyplease.SponsorHandler
    events:
      - http:
          path: sponsors/{sponsorId}
          method: get

# charity table
  getcharity:
    handler: com.prettyplease.CharityHandler
    events:
      - http:
          path: charities/{charityId}
          method: get

# get detailed request by requestId
  getsponsorrequest:
    handler: com.prettyplease.CharityRequestHandler
    events:
      - http:
          path: requests/{requestId}
          method: get

# list of detailed requests
  listdetailedrequests:
    handler: com.prettyplease.CharityRequestHandler
    events:
      - http:
          path: requests
          method: get

# all requests, excluding those sponsored by the selected sponsorId
  listopenrequests:
    handler: com.prettyplease.SponsorRequestHandler
    events:
      - http:
          path: sponsorrequests/{sponsorId}
          method: get

# requests by charityId
  listcharityrequests:
    handler: com.prettyplease.CharityGetRequestHandler
    events:
      - http:
          path: charityrequests/{charityId}
          method: get

  # list of detailed offers by charityid
  listcharityoffers:
    handler: com.prettyplease.CharityOfferHandler
    events:
      - http:
          path: charityoffers/{charityId}
          method: get

# list of detailed offers by sponsorid
  listsponsoroffers:
    handler: com.prettyplease.SponsorOfferHandler
    events:
      - http:
          path: sponsoroffers/{sponsorId}
          method: get

# POST
  createsponsor:
    handler: com.prettyplease.SponsorHandler
    events:
      - http:
          path: sponsors
          method: post

  createcharity:
    handler: com.prettyplease.CharityHandler
    events:
      - http:
          path: charities
          method: post

  createrequest:
    handler: com.prettyplease.RequestHandler
    events:
      - http:
          path: requests
          method: post

  createoffer:
    handler: com.prettyplease.OfferHandler
    events:
      - http:
          path: offers
          method: post

# PUT
  updaterequest:
    handler: com.prettyplease.RequestHandler
    events:
      - http:
          path: request/{requestId}
          method: put

  updateoffer:
    handler: com.prettyplease.OfferHandler
    events:
      - http:
          path: offers/{offerId}
          method: put
```


### Serverless Framework cheat sheet
Display the version number of serverless framework
```yaml
serverless version
```
Deploy from the current folder:
```yaml
serverless deploy 
```
check log file for a function (e.g. function name <em>getsponsor</em>)
```yaml
serverless logs -f getsponsor
```
Remove everything:
```yaml
serverless remove
```
