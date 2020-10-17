# Pretty Please backend functions

This repo creates the backend API to connect the AWS RDS database with the Pretty Please front-end React application.  

## Requirements
- Java 8
- Maven
- Serverless Framework 
- An AWS account (Lambda, RDS)

## Setup
Download or clone this repo.

## Configuration
- AWS keys set up for your Serverless installation 
- Create a config.dev.json file at the project level containing the following name/value pairs:
```json
{
  "DB_HOST" : <database host name>,
  "DB_NAME" : <database name>,
  "DB_USER" : <database user>,
  "DB_PASSWORD" : <database password>
} 
```

## Deployment
To deploy the functions run:
```shell script
serverless deploy 
```


## Lambda Functions

#### Function 
sponsors
#### Handler: 
com.prettyplease.Handler

#### Events
```yaml
type: http
path: sponsor/{sponsorId}
method: get
```


### Serverless Framework cheat sheet
serverless version - displays the version number of serverless framework
serverless deploy - deploys from the current folder
serverless log -f <function name> - shows logs
serverless remove - removes everything