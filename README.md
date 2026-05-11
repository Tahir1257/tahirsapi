# *1. Overview*

This project implements a Smart Campus REST API using JAX-RS and Grizzly to manage rooms, sensors, and sensor readings.

# *2. How to Run*

*Simple steps:*

1. Open project in IntelliJ
2. Run Main.java
3. Open Postman
4. Use: http://localhost:8080/api/v1/


# *3. API Endpoints*

*List like:*

GET /rooms
POST /rooms
GET /rooms/{id}
DELETE /rooms/{id}

GET /sensors
POST /sensors
GET /sensors/{id}

GET /sensors/{id}/readings
POST /sensors/{id}/readings

# *4. Sample CURL (MANDATORY)*

## 1. GET all rooms

curl -X GET http://localhost:8080/api/v1/rooms

## 2. Create a room
curl -X POST http://localhost:8080/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"id":"LIB-301","name":"Library","capacity":100}'
## 3. Create a sensor
curl -X POST http://localhost:8080/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id":"TEMP-1","type":"temperature","roomId":"LIB-301"}'
## 4. Get sensors (with filtering)
curl -X GET "http://localhost:8080/api/v1/sensors?type=temperature"
## 5. Add sensor reading
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-1/readings \
-H "Content-Type: application/json" \
-d '{"id":"R1","timestamp":1710000000,"value":25.5}'

## 6. Get readings
curl -X GET http://localhost:8080/api/v1/sensors/TEMP-1/readings

# 5. Answers to Questions

## Question:

When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.

### Answer:

Returning only room IDs significantly reduces the size of the response payload, which improves network efficiency and reduces bandwidth consumption. This approach is especially beneficial when dealing with large datasets or mobile clients with limited connectivity. However, the client must make additional API requests to retrieve complete room details, increasing client-side complexity and the number of network calls.

Returning full room objects provides all required information in a single response, simplifying client-side processing and reducing the need for extra requests. The downside is that larger JSON responses consume more bandwidth and may negatively impact performance when many room objects are returned simultaneously.

Therefore, returning IDs is more efficient for lightweight references, while returning full objects is more convenient for clients that immediately require complete room information.

---

## Question:

Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.

### Answer:

Yes, the DELETE operation is idempotent in this implementation. An idempotent operation means that performing the same request multiple times produces the same final state on the server.

When the client sends a DELETE request for a room for the first time, the room is removed successfully from the system. If the client accidentally sends the exact same DELETE request again, the room no longer exists, so the server simply returns an error response such as HTTP 404 Not Found.

Although the responses differ between the first and subsequent requests, the important aspect is that the server state remains unchanged after the initial deletion. No additional modifications occur, which satisfies the definition of idempotency in RESTful systems.

---

## Question:

We explicitly use the @Consumes(MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

### Answer:

The @Consumes(MediaType.APPLICATION_JSON) annotation specifies that the endpoint only accepts requests with the application/json content type. If a client attempts to send data using a different format such as text/plain or application/xml, JAX-RS detects that the request content type does not match the supported media type.

As a result, the framework automatically rejects the request and typically returns an HTTP 415 Unsupported Media Type response. The request method itself is not executed because JAX-RS cannot deserialize the incoming data into the expected Java object format.

This mechanism improves API reliability and security by enforcing strict input formatting rules and preventing invalid or unexpected payload types from being processed.

---

## Question:

You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/v1/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?

### Answer:

Using @QueryParam is generally considered superior for filtering collections because query parameters are specifically designed for optional filtering, searching, sorting, and pagination operations.

For example:

text
/api/v1/sensors?type=CO2


clearly communicates that the client is requesting the sensors collection while applying a filter condition.

In contrast, embedding the filter inside the URL path:

text
/api/v1/sensors/type/CO2


makes the filter appear as a separate resource hierarchy rather than a flexible query operation.

Query parameters are more scalable because multiple filters can easily be combined:

text
/api/v1/sensors?type=CO2&status=active


This keeps the API cleaner, more readable, and more aligned with RESTful design conventions. It also simplifies server-side routing logic compared to defining numerous path-based filter endpoints.

---

## Question:

Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?

### Answer:

The Sub-Resource Locator pattern improves API modularity and maintainability by delegating nested resource handling to separate dedicated classes. Instead of placing all endpoint logic inside one large controller, each resource manages only its own responsibilities.

For example, sensor-related operations can remain inside a SensorResource class, while reading-related operations are delegated to a separate SensorReadingResource class.

This separation offers several architectural advantages:

* Improved code organization
* Better readability
* Easier debugging and maintenance
* Reduced controller complexity
* Increased scalability for large APIs
* Better team collaboration since developers can work on separate resources independently

Without sub-resource locators, a single controller handling deeply nested routes such as:

text
sensors/{id}/readings/{rid}


would become excessively large and difficult to maintain. The sub-resource approach follows the principle of separation of concerns and produces a cleaner, more extensible API architecture.

---

## Question:

Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

### Answer:

HTTP 422 Unprocessable Entity is considered more semantically accurate because the request itself is syntactically valid, but the server cannot process it due to invalid semantic content within the payload.

For example, if a client submits a valid JSON request referencing a room ID that does not exist, the JSON structure and endpoint are correct, meaning the request was understood successfully. However, the referenced entity inside the payload is invalid.

Using HTTP 404 Not Found could incorrectly imply that the endpoint itself does not exist, whereas HTTP 422 more accurately communicates that the problem lies with the submitted data rather than the URL or resource path.

This distinction improves API clarity and helps clients better understand validation-related failures.

---

## Question:

From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

### Answer:

Exposing internal Java stack traces to external users creates serious cybersecurity risks because stack traces may reveal sensitive implementation details about the application and server environment.

An attacker could gather information such as:

* Internal package and class names
* Frameworks and library versions
* Server structure and file paths
* Database technology details
* API architecture information
* Method names and execution flow
* Potentially vulnerable components

This information can assist attackers in identifying known vulnerabilities, crafting targeted exploits, or performing reconnaissance against the system.

For example, revealing exact framework versions may allow attackers to search for publicly known security vulnerabilities affecting those versions.

For this reason, production APIs should avoid exposing raw stack traces and instead return sanitized error responses with generic messages while logging detailed exceptions internally on the server side only.
