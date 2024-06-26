package com.example.rest_assured;


import com.example.rest_assured.entity.User;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserControllerTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        given()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/api/users")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo("John Doe"))
                .body("email", equalTo("john.doe@example.com"));
    }

    @Test
    public void testGetUser() {
        // Create a user first
        User user = new User();
        user.setName("Jane Doe");
        user.setEmail("jane.doe@example.com");

        User createdUser = given()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/api/users")
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);

        // Retrieve the created user by ID
        given()
                .when()
                .get("/api/users/" + createdUser.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(createdUser.getId().intValue()))
                .body("name", equalTo("Jane Doe"))
                .body("email", equalTo("jane.doe@example.com"));
    }

    @Test
    public void testUpdateUser() {
        // Create a user first
        User user = new User();
        user.setName("Jim Doe");
        user.setEmail("jim.doe@example.com");

        User createdUser = given()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/api/users")
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);

        // Update the created user
        createdUser.setName("Jim Updated");
        createdUser.setEmail("jim.updated@example.com");

        given()
                .contentType("application/json")
                .body(createdUser)
                .when()
                .put("/api/users/" + createdUser.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(createdUser.getId().intValue()))
                .body("name", equalTo("Jim Updated"))
                .body("email", equalTo("jim.updated@example.com"));
    }

    @Test
    public void testDeleteUser() {
        // Create a user first
        User user = new User();
        user.setName("Jake Doe");
        user.setEmail("jake.doe@example.com");

        User createdUser = given()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/api/users")
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);

        // Delete the created user
        given()
                .when()
                .delete("/api/users/" + createdUser.getId())
                .then()
                .statusCode(204);

        // Verify the user is deleted
        given()
                .when()
                .get("/api/users/" + createdUser.getId())
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetAllUsers() {
        // Create two users
        User user1 = new User();
        user1.setName("User One");
        user1.setEmail("user.one@example.com");

        User user2 = new User();
        user2.setName("User Two");
        user2.setEmail("user.two@example.com");

        given()
                .contentType("application/json")
                .body(user1)
                .when()
                .post("/api/users")
                .then()
                .statusCode(200);

        given()
                .contentType("application/json")
                .body(user2)
                .when()
                .post("/api/users")
                .then()
                .statusCode(200);

        // Retrieve all users
        given()
                .when()
                .get("/api/users")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2));
    }
}
