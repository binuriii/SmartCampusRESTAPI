# Smart Campus REST API (JAX-RS)

## Overview
This project is a RESTful web service built using JAX-RS (Jakarta RESTful Web Services). It simulates a Smart Campus system that manages:
- Rooms
- Sensors
- Sensor Readings
  
The API uses in-memory storage (HashMap / ArrayList) and follows core REST principles:
- Resource-based architecture
- Proper HTTP methods (GET, POST, DELETE)
- Sub-resource routing (Sensor → Readings)
- Query parameter filtering
- Centralized exception handling
- JSON request/response format

## Technology Stack
- Java
- JAX-RS (Jersey)
- Maven
- Apache Tomcat / GlassFish
- JSON (Jackson)
- In-memory data (no database)
  
### Base URL
`http://localhost:8080/SmartCampusRESTAPI/api/v1`

---

## API Design Summary

### Resources

#### Resource Paths
- `/` = API discovery endpoint
- `/rooms` = Manage rooms
- `/sensors` = Manage sensors
- `/sensors/{id}/readings` = Manage sensor readings

#### Relationships
- A Room can have multiple Sensors
- A Sensor belongs to one Room
- A Sensor can have multiple Readings

---

## Build & Run Instructions

### 1. Prerequisites
Make sure you have:
- Java JDK 11 or higher
- Maven 3+
- Apache Tomcat or GlassFish
- IDE (NetBeans/Eclipse/IntelliJ)

### 2. Clone Repository
```bash
git clone [https://github.com/binuriii/SmartCampusRESTAPI.git](https://github.com/binuriii/SmartCampusRESTAPI.git)
cd SmartCampusRESTAPI
```
### 3. Build the Project
```
Bash
mvn clean install
```
### 4. Deploy the Application

Using NetBeans:
```
Open the project in NetBeans.

Right-click the project folder.

Select Run.

Choose your configured server (Tomcat/GlassFish).
```
### 5. Verify Server

Open your browser or Postman and navigate to:
```
http://localhost:8080/SmartCampusRESTAPI/api/v1/
```
You should see the API discovery JSON response.

### Sample cURL Commands
```
1. API Discovery

Bash
curl -X GET http://localhost:8080/SmartCampusRESTAPI/api/v1/

2. Get All Rooms

Bash
curl -X GET http://localhost:8080/SmartCampusRESTAPI/api/v1/rooms

3. Create a Room

Bash
curl -X POST http://localhost:8080/SmartCampusRESTAPI/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{
  "name": "Lab A",
  "capacity": 40
}'

4. Create a Sensor

Bash
curl -X POST http://localhost:8080/SmartCampusRESTAPI/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{
  "type": "CO2",
  "status": "WORKING",
  "roomId": "REPLACE_WITH_ROOM_ID"
}'

5. Get Sensors (with filter)

Bash
curl -X GET "http://localhost:8080/SmartCampusRESTAPI/api/v1/sensors?type=CO2"

6. Get Sensor Readings

Bash
curl -X GET http://localhost:8080/SmartCampusRESTAPI/api/v1/sensors/{sensorId}/readings

7. Add Sensor Reading

Bash
curl -X POST http://localhost:8080/SmartCampusRESTAPI/api/v1/sensors/{sensorId}/readings \
-H "Content-Type: application/json" \
-d '{
  "value": 420.5
}'

8. Delete a Room

Bash
curl -X DELETE http://localhost:8080/SmartCampusRESTAPI/api/v1/rooms/{roomId}
```

### Business Rules
- A room cannot be deleted if it contains active sensors.

- A sensor must be linked to a valid, existing room ID.

- Sensors with a status of MAINTENANCE cannot accept new readings.

- Adding a reading automatically updates the parent sensor's currentValue (Side-Effect).

### Exception Handling
Custom exception mappers intercept errors and return structured JSON responses, preventing internal stack trace leaks:

- 400 Bad Request: Invalid JSON input format.

- 403 Forbidden: Sensor is unavailable (e.g., in maintenance).

- 404 Not Found: Resource does not exist.

- 409 Conflict: Attempted to delete a room that is not empty.

- 422 Unprocessable Entity: Invalid linked resource (e.g., fake Room ID).

- 500 Internal Server Error: Catch-all for unhandled server exceptions.

### Logging
A global ContainerRequestFilter and ContainerResponseFilter automatically log:

- Incoming requests (HTTP Method + URI)

- Outgoing responses (Final Status Code)

# Project Report - Answers

## Part 1: Service Architecture and Setup

### 1. JAX-RS Resource Lifecycle and Synchronisation in Memory
The JAX-RS Runtime by default creates resource classes on each request, so each incoming HTTP request is processed by creating a new instance of a resource class (for example, our `SensorRoomResource`). The individual instances cannot share state because they are created for each request.

In the implementation of the service, data has been stored using static data structures (e.g., `public static final Map`) within `MockDatabase.java`. As these data structures are static, their contents are globally accessible via any request thread. Presently, standard `java.util.HashMap` and `java.util.ArrayList` are used to store data, which makes them non-thread-safe and results in potential risks of race conditions and corruption of data if two or more clients perform `POST` or `DELETE` operations at the same time. 

To mitigate the possibility of losing data in a production environment in the absence of a true database, synchronization must be placed around the static data structures.

### 2. HATEOAS and Hypermedia Advantages
Hypermedia (links and navigation) contained in response to requests is one of the most important characteristics of an advanced RESTful API design (**HATEOAS** - Hypermedia As The Engine Of Application State) as it allows the API to be self-discoverable. Clients no longer need to hard-code the URLs of all endpoints; the server dynamically supplies appropriate links for valid state transitions as a function of the current context.

Client developers benefit significantly relative to static documentation when an API is built with this level of support:
* **Decoupling:** If the back-end development team ever modifies the route to an API endpoint, this client application will not fail as it will continue to read the correct routes dynamically from response payloads.
* **State Management:** The API serves as the official, live documentation of valid states that may be requested. If a client attempts to perform an action and the server currently prohibits it, the server simply will not return the links necessary for performing that action, so the developer can avoid coding complex validations on the front-end.

---

## Part 2: Room Management

### 1. ID vs. Full Room Object Return Implications
When returning a large number of rooms (thousands), there are advantages and disadvantages to consider:

* **Return Only IDs:** This uses less initial network capacity; therefore, the first call will have a lower loading time and less payload (lightweight, small JSON file). However, it can negatively affect client processing because of the **“N+1 Request problem”**; the client would need to do an initial request to obtain the IDs for the rooms, followed by *N* number of asynchronous requests for the details of each individual room, ultimately increasing latency.
* **Return Full Room Object:** While this resolves the N+1 issue and the client could render the user interface immediately without waiting for many additional requests, it puts a high demand on the network. If there are thousands of full room objects (many of which may have nested sensor ID arrays), it substantially increases the size of the JSON file parse needed for a client-side device and may create additional memory constraints on lower-end devices.

### 2. Idempotency of the DELETE Operation
Yes, the `DELETE /{roomID}` operation in `SensorRoomResources` is strictly idempotent. In REST architecture, idempotency means that making multiple identical requests has the same effect on the server’s state as making a single request. 

If a client mistakenly sends the exact same `DELETE` request multiple times:
1.  **First request:** The server finds the room, removes it from the rooms map, and returns a `200 OK` Success response.
2.  **Subsequent requests:** The server looks for the room, cannot find it (`if (room == null)`), and throws a `ResourceNotFoundException`, resulting in a `404 Not Found` response.

Even though the status code changes, the state of the server (the room is deleted) remains identical after the first request, satisfying the idempotency requirement.

---

## Part 3: Sensor Operations and Linking

### 1. @Consumes Annotations Ensure Media Types are Rejected
`@Consumes(MediaType.APPLICATION_JSON)` acts as the gateway filter to ensure an incoming message matches the `Accept` header specified in a request by the client. When a mismatch occurs, the JAX-RS Runtime will intercept the message before processing it through any Java methods and will return a `415 Unsupported Media Type` error to the client.

This prevents the backend of the application from attempting to parse invalid formats into the Sensor POJO, and thus avoids any internal server errors for mapping failure exceptions.

### 2. Using @QueryParam vs. Path Parameter to Filter
Using `@QueryParam` to implement a type filter is considered preferable to using the path portion of the URL to search for collections because it adheres to common REST resource semantics.

* The purpose of the **path parameter** is to define the unique structural identity of a resource (e.g., `/sensors/{id}`). 
* When **filtering** (which does not create new resources), it modifies the general view of the existing `/sensors` collection.

Query parameters are always optional. Therefore, you can implement a single method to serve both entire collections and filtered collections. Conversely, if you were to use a path-based method, you would have to create many separate endpoint routes in order to accommodate all possible combinations of filter parameters. Using a query-based method is highly scalable.

---

## Part 4: Deep Nesting with Sub-Resources

### 1. Architectural Advantages of the Sub-Resource Locator Pattern
The Sub-Resource Locator Pattern helps to eliminate large monolithic controller classes that would otherwise create unnecessary complexity and redundancy by delegating processing.

1.  **Separation of Concerns:** Mapping `/{sensorId}/readings` to obtain a `SensorReadingResource` instance allows `SensorResource` to only be responsible for the core logic of sensors, while logic related to historical readings can be separated out into its own class.
2.  **State Encapsulation:** In order for the `SensorReadingResource` to properly encapsulate the state of the Sensor being passed in as a constructor parameter (`sensorId`), the `@PathParam("sensorId")` annotation does not need to be repeated in the signatures of nested `@GET` and `@POST` methods on the `SensorReadingResource` class. Annotating `@PathParam("sensorId")` on each individual method signature reduces complexity and improves readability as the API increases in size.

---

## Part 5: Advanced Error Handling, Exception Mapping, Logging

### 1. HTTP 422 vs 404 Semantic Accuracy
If you try to `POST` a new sensor and the `roomId` does not exist, the system throws a `LinkedResourceNotFoundException`, which is then mapped to **HTTP 422 (Unprocessable Entity)**.

In this case, HTTP 422 is a much more semantically accurate status code than standard HTTP 404 (Not Found):
* A **404** implies that the actual endpoint of the application (where you would send the `POST` request) doesn't exist.
* By sending a **422**, the server is letting you know it received and correctly understood both the payload content type and the JSON payload format; however, it cannot process the semantics of the content within the payload (the foreign key relationship to the non-existent room).

### 2. Cybersecurity Risks of Internal Java Stack Trace Exposing
Exposing the internal Java stack trace of an API externally is one of the highest security risks for that API. The `GlobalExceptionMapper` is correctly intercepting all unhandled throwable exceptions to return a standard `500 Internal Server Error` response to protect against revealing the internal stack, which could expose:
* **Framework versions:** Allowing attackers to cross-reference CVEs (Common Vulnerabilities and Exposures) and conduct targeted exploits against the application.
* **Internal class and package names:** Providing insight into the architectural structure of the backend application.
* **Database driver types or SQL syntax errors:** Providing attackers with hints on how they could perform a SQL injection or manipulate data within the application.

### 3. Advantage of JAX-RS Filters for Logging
By using JAX-RS filters (`ContainerRequestFilter` and `ContainerResponseFilter`) to log cross-cutting concerns, you can significantly reduce the amount of duplicated logging code compared to explicitly using `Logger.info()` in all of your resource methods.

* **Centralized Logging Configuration:** With a single instance of the Logging Filter intercepting all requests and responses at the framework level, you remove all code duplication.
* **Maintainability:** If the log format needs to be modified (e.g., adding a user IP), you only need to modify one configuration file in a centralized location rather than modifying all endpoints of the controller. Additionally, you remove the risk of forgetting to add logging calls on new endpoints.
