This document contains homework tasks for `Spring-boot`.

Task 1 - Hello-world application
==========================

**Cost**: 20 points.

* Using https://start.spring.io create a Spring-boot app.
* Create CommandLineRunner and output 'hello world'.
* Start your application.
* Check that spring context is up and there is 'hello world' message in console.

**DONE!** Check output in console.
```
2024-05-08T01:35:35.388-05:00  INFO 12699 --- [task3] [  restartedMain] c.e.jmp.spring.task3.Task3Application    : Started Task3Application in 3.391 seconds (process running for 3.647)
Hello World's from Task3 Application!
2024-05-08T01:37:17.746-05:00  INFO 12699 --- [task3] [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
```

Task 2 - CRUD REST application
==========================

**Cost**: 20 points.

* Create app that should support create, read, update and delete operations for some entity
* Use Spring Data module
* Don't use Spring Data REST starter

**DONE!** Check http://localhost:8080 on a web browser.

Task 3 - CRUD application: security
==========================

**Cost**: 20 points.

* Implement authentication and authorization mechanism
* OAuth2 should be used
* JWT Token should be used

**DONE!** Run the application with `spring.profiles.active=prod` and use credentials: (`admin` and `pass`).

Notice the `Login with OAuth 2.0` section on the login page. *Only works with my personal email for the moment, reach out for demo.*

Make a POST to http://localhost:8080/oauth/token with Postman. Use Basic Auth with `admin` and `pass`.

Then use the resulting JWT `access_token` in the JSON response to access http://localhost:8080/api/books/1. Use `Bearer Token` 
in the Authorization header.

Task 4 (Optional) - CRUD application: externalized configuration
==========================

* Should support different environments - local, dev, stg, prod
* Spring profiles
* Each environment - different db properties

**DONE!** Check `application-{env}.properties` and `application.properties` files. Run with different profiles:

- `-Dspring.profiles.active=dev` for no security and full logging in console.
- `-Dspring.profiles.active=test` for Unit Testing.
- `-Dspring.profiles.active=stg` for generating sql create script for db. Also used for integration tests.
- `-Dspring.profiles.active=prod` for OAuth2 and JWT.

Task 5 - CRUD application: data migrating
==========================

**Cost**: 20 points.

* Add tool for migrating data
* Flyway or Liquibase

**DONE!** Check `flyway` migration scripts in `src/main/resources/db/migration`.

Task 6 (Optional) - CRUD application: actuator
==========================

* Enable actuator
* Implement a few custom health indicators
* Implement a few  custom metrics using Prometheus

**DONE!** Check http://localhost:8080/actuator/health/bookstore
```
{
"status": "UP",
    "details": {
        "authors": 2,
        "publishedBooks": 2
    }
}
```
See
```
com.epam.jmp.spring.task3.actuator.PublishedBooksHealthCheck
``` 
for Custom Health indicator.

Check http://localhost:8080/actuator/prometheus and find custom metric:
```
# HELP bookstore_reviews_added_total Total number of reviews added
# TYPE bookstore_reviews_added_total counter
bookstore_reviews_added_total 1.0
```

Run: `docker run -p 9090:9090 --mount type=bind,source=$(pwd)/src/main/resources/prometheus/prometheus.yml,target=/etc/prometheus/prometheus.yml,readonly prom/prometheus`

And check http://localhost:9090/graph?g0.range_input=1h&g0.expr=bookstore_reviews_added_total&g0.tab=1 (wait for a minute to see the metric).

Also check Authenticated access to the endpoint:
```
o.s.security.web.FilterChainProxy        : Secured GET /actuator/prometheus
o.s.s.w.header.writers.HstsHeaderWriter  : Not injecting HSTS header since it did not match request to [Is Secure]
o.s.s.w.a.AnonymousAuthenticationFilter  : Did not set SecurityContextHolder since already authenticated JwtAuthenticationToken 
    [Principal=org.springframework.security.oauth2.jwt.Jwt@91a19171, Credentials=[PROTECTED], Authenticated=true, Details=WebAuthenticationDetails [RemoteIpAddress=127.0.0.1, SessionId=null], 
    Granted Authorities=[SCOPE_ROLE_USER]]
```

Task 7 - CRUD application: testing
==========================

**Cost**: 20 points.

* In memory db must be used for testing purpose
* Implement repository testing
* Implement unit tests
* Implement tests for RestController using mock mvc
* Implement integration tests

**DONE!** Run `mvn test` and check results.

Check Unit and Integration tests in:
```
src/test/java/com/epam/jmp/spring/task3
```