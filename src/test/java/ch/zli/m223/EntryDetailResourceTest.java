package ch.zli.m223;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

@QuarkusTest
public class EntryDetailResourceTest {

    @Test
    public void testCategoriesTagsAndEntryDetails() {
        int categoryId = given()
          .contentType("application/json")
          .body("{\"title\":\"Projektarbeit\"}")
          .when().post("/categories")
          .then().statusCode(200).body("title", is("Projektarbeit"))
          .extract().path("id");

        int tagId = given()
          .contentType("application/json")
          .body("{\"title\":\"M223\"}")
          .when().post("/tags")
          .then().statusCode(200)
          .extract().path("id");

        given()
          .contentType("application/json")
          .body("{\"title\":\"Schule\"}")
          .when().put("/categories/" + categoryId)
          .then().statusCode(200).body("title", is("Schule"));

        int entryId = given()
          .contentType("application/json")
          .body("{\"checkIn\":\"2026-10-01T08:00:00\",\"checkOut\":\"2026-10-01T12:00:00\",\"categoryId\":"
              + categoryId + ",\"tagIds\":[" + tagId + "]}")
          .when().post("/entries")
          .then().statusCode(200)
          .body("categoryId", is(categoryId))
          .body("tagIds[0]", is(tagId))
          .extract().path("id");

        // Unbekannte IDs -> 404
        given()
          .contentType("application/json")
          .body("{\"checkIn\":\"2026-10-01T08:00:00\",\"checkOut\":\"2026-10-01T12:00:00\",\"tagIds\":[9999]}")
          .when().post("/entries")
          .then().statusCode(404);

        given()
          .when().get("/entry-details/" + entryId)
          .then().statusCode(200)
          .body("category.title", is("Schule"))
          .body("tags[0].id", is(tagId))
          .body("tags[0].title", is("M223"));

        given().when().get("/entry-details/9999").then().statusCode(404);

        // Löschen referenzierter Kategorie/Tags entfernt die Beziehungen
        given().when().delete("/categories/" + categoryId).then().statusCode(204);
        given().when().delete("/tags/" + tagId).then().statusCode(204);

        given()
          .when().get("/entry-details/" + entryId)
          .then().statusCode(200)
          .body("category", nullValue())
          .body("tags.size()", is(0));

        given().when().delete("/entries/" + entryId).then().statusCode(204);
    }
}
