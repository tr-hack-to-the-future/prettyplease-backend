# Pretty Please REST API

The Pretty Please API is organised around REST. Our API returns JSON-encoded responses, and uses standard HTTP response codes, authentication, and verbs.


## ENDPOINTS
### Sponsors
Sponsors are the organisation or individuals who are looking to find donate funds to help charity projects. A sponsor will have a name, an image or logo, and a description of their organisation's work and values, to help a charity find the right sponsorship partner.
```
GET   sponsors/{sponsorId}
POST  sponsors 
```

### Charities

These are organisations or individuals that are seeking to raise money to pay for charitable ventures. A charity will have a name and description of their activities, along with an image or logo. 
```
GET   charities/{charityId}
POST  charities 
```

### Requests
A request contains the details of a charity's request to raise funds. This will usually consist of an amount, a duration of the agreement (single event or multiple-year partnership) and an incentive for the sponsor.  
A GET for a request will usually return details of the charity that raised the request.
```
GET   sponsorrequests/{requestId} 
GET   requests
POST  requests
PUT   requests/{requestId} 
```

### Offers
Offers identify when a sponsor applies to sponsor a funding request. This holds details of the offer, which may differ from the original funding request. For example, if the sponsor would like to negotiate an extended sponsorship duration in exchange for greater funding.
A GET will return the full details of the offer, including sponsor, request and charity information.
```
GET   charityoffers/{charityId}
GET   sponsoroffers/{sponsorId}
POST  offers
PUT   offers/{offerid}
-- 
```


### Combined Data


HTTP Status Codes
```
200 - OK          Everything worked as expected.
400 - Bad Request The request was unacceptable, possibly due to missing a required parameter or a problem configuring the database.
409 - Conflict    The request conflicts with another request (perhaps due to using the same primary key value).
```
