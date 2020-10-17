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


## Lambda Functions

#### Function 
sponsors
#### Handler: 
com.prettyplease.GetSponsorHandler

#### REST API
```yaml
type: http
path: sponsor/{sponsorId}
method: get
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
