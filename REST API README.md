# Pretty Please REST API

This API is organised around REST and is the back for the Pretty Please charity marketplace application developed as part of the eight week [Tech Returners](https://www.techreturners.com/) Your Journey into Tech course. The API returns JSON-encoded responses, and uses standard HTTP response codes, authentication, and verbs.


## ENDPOINTS
The API exposes the following endpoints:

### Base Endpoint 
https://ae9g7g3iyl.execute-api.eu-west-2.amazonaws.com/dev/


### Sponsors
Sponsors are the organisation or individuals who are looking to find donate funds to help charity projects. A sponsor will have a name, an image or logo, and a description of their organisation's work and values, to help a charity find the right sponsorship partner.

```
GET   sponsors/{sponsorId}
```
Responds with JSON containing all sponsors  
```
GET   sponsors/{sponsorId}
POST  sponsors 
```
##### POST JSON
POST /sponsors will create a new sponsor with a JSON payload in the following format: 
```json
{
    "sponsorId" : "ABC123",
    "name": "Sponsor Name",
    "description": "Sponsor description",
    "imageUrl": "URL to the image file/logo of the sponsor",
    "webUrl": "URL to the sponsor website"
}
```

### Charities

These are organisations or individuals that are seeking to raise money to pay for charitable ventures. A charity will have a name and description of their activities, along with an image or logo. 
```
GET   charities/{charityId}
POST  charities 
```
##### POST JSON
POST /charities will create a new charity with a JSON payload in the following format: 
```json
{
    "charityId" : "ABC123",
    "name": "Charity Name",
    "description": "Charity description",
    "imageUrl": "URL to the image file/logo of the sponsor",
    "webUrl": "URL to the charity website"
}
```



### Requests
A request contains the details of a charity's request to raise funds. This will usually consist of an amount, a duration of the agreement (single event or multiple-year partnership) and an incentive for the sponsor.  
A GET for a request will usually return details of the charity that raised the request.
```
GET   sponsorrequests/{requestId}
GET   charityrequests/{charityId}
GET   requests/{requestId}
PUT   requests/{requestId} 
POST  requests
```
##### POST JSON
POST /requests will create a new fund request with a JSON payload in the following format: 
```json
    {
        "charityId": "ABC123",
        "eventDescription": "Description of the fundraising project or event",
        "incentive": "A description of the incentive offered to the sponsors",
        "amountRequested": 9000,
        "amountAgreed": 9000,
        "isSingleEvent": false,
        "durationInYears": 2,
        "agreedDurationInYears": 2,
        "requestStatus": "OPEN",
        "requestDate": 1603065600000,
        "dueDate": null
    }
```

### Offers
Offers identify when a sponsor applies to sponsor a funding request. This holds details of the offer, which may differ from the original funding request. For example, if the sponsor would like to negotiate an extended sponsorship duration in exchange for greater funding.
A GET will return the full details of the offer, including sponsor, request and charity information.
```
GET   charityoffers/{charityId}
GET   sponsoroffers/{sponsorId}
PUT   offers/{offerId}
POST  offers
-- 
```
##### POST JSON
POST /offers will create a new sponsor offer with a JSON payload in the following format: 
```json
    {
        "sponsorId": "DEF456",
        "requestId": "FRA456",
        "offerStatus": "PENDING",
        "offerAmount": 9000,
        "isSingleEvent": false,
        "offerDurationInYears": 2
    }
```

### Combined Data


HTTP Status Codes
```
200 - OK          Everything worked as expected.
400 - Bad Request The request was unacceptable, possibly due to missing a required parameter or a problem configuring the database.
409 - Conflict    The request conflicts with another request (perhaps due to using the same primary key value).
```
