# LinkedIn saved-jobs 

## Requirements
- Java 17
- Maven 3.x (or use maven wrapper)

## How to Run
```shell
./mvnw clean install
./mvnw spring-boot:run
```

## Run Tests
```shell
# run unit tests
mvn test 
# run integration tests
mvn verify
```

### API Docs
- Swagger UI [here](http://localhost:8080/swagger-ui/index.html)
- Open API spec [here](http://localhost:8080/api-docs)


# Limitations/Notes
- I used browser dev tools to figure out the API endpoint. As I understood, this is not an endpoint that is supposed to be used by others and could not find any documentation about this. 
- I was not able to figure the filter to get all (`APPLIED`, `IN_PROGRESS`, `ARCHIVED`, `SAVED`) jobs. Intuitive filter is `value:List(APPLIED,IN_PROGRESS,ARCHIVED,SAVED)`. But this is returning empty content in `included` section, though it returns correct `totalResultCount`. 
- `voyagerSearchDashClusters.xxx` query parameter was used in the API. As to my understanding this is used to route the query to a cluster. I have included default cluster in `application.yaml` and allowed the API to specify a cluster optionally. The endpoint does not ask for users LinkedIn URL. 
- I have not used any pagination, instead returned full count (as I understood the question). Large number of results could cause timeouts. 