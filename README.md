# Pretty Please backend functions

This repo creates the backend API to connect the AWS RDS database with the Pretty Please front-end React application.  

## Requirements
- Java 8
- Maven
- Serverless Framework 
- AWS Lambda, RDS (MySQL 5.7)

## Setup
Download or clone this repo.

## Configuration
- AWS keys set up for your Serverless installation 
- Create a config.dev.json file at the project level containing the following name/value pairs:
```json
{
  "DB_HOST" : "<database_host_name>",
  "DB_NAME" : "<database_name>",
  "DB_USER" : "<database_user>",
  "DB_PASSWORD" : "<database_password>"
} 
```

## Deployment
To deploy the functions run:
```shell script
serverless deploy 
```

## REST API
The definition of the REST API can be found [here](./REST API README.md).


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

# list of detailed requests, excluding sponsored by the sponsorId 
  listopenrequests:
    handler: com.prettyplease.SponsorRequestHandler
    events:
      - http:
          path: sponsorrequests/{sponsorId}
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
  acceptoffer:
    handler: com.prettyplease.OfferHandler
    events:
      - http:
          path: offers/{requestId}
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
