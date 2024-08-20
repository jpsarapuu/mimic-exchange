## Mimic Exchange

###

##### To run application: (will include compose.yaml due to spring-boot-docker-compose dependency)

* "mvn spring-boot:run"

##### Swagger URL:

* http://localhost:8080/swagger-ui.html

##### To run tests:

* "mvn verify"

##### To regenerate client (for API tests) from OpenAPI spec

* "mvn spring-boot:run"
* "mvn generate-sources -Popenapi-generator"

##### Todo regarding monitoring:

* collect common metrics such as response time, CPU and memory usage
* set up metric for serialization anomalies (Postgres error code 40001) to be aware of possible
  false-positives
