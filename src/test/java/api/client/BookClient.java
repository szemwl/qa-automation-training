package api.client;

import api.model.Book;
import api.spec.RequestSpec;
import api.spec.ResponseSpec;
import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;

public class BookClient {

    private static final String BOOKS = "/api/v1/Books";

    public Book createBook(Book book) {

        return given()
                .spec(RequestSpec.requestSpec())
                .body(book)
                .when()
                .post(BOOKS)
                .then()
                .spec(ResponseSpec.ok200())
                .extract()
                .response()
                .as(Book.class);
    }

    public Book getBookById(int bookId) {

        return given()
                .spec(RequestSpec.requestSpec())
                .when()
                .get(BOOKS + "/{id}", bookId)
                .then()
                .spec(ResponseSpec.ok200())
                .extract()
                .as(Book.class);
    }

    public List<Book> getAllBooks() {

        return given()
                .spec(RequestSpec.requestSpec())
                .when()
                .get(BOOKS)
                .then()
                .spec(ResponseSpec.ok200())
                .extract()
                .jsonPath()
                .getList("", Book.class);
    }

    public void deleteBook(int bookId) {

        given()
                .spec(RequestSpec.requestSpec())
                .when()
                .delete(BOOKS + "/{id}", bookId)
                .then()
                .spec(ResponseSpec.ok200());
    }

    public Response getBookResponse(int bookId) {
        return given()
                .spec(RequestSpec.requestSpec())
                .when()
                .get(BOOKS + "/{id}", bookId);
    }

    public Response createBookResponse(Book book) {

        return given()
                .spec(RequestSpec.requestSpec())
                .body(book)
                .when()
                .post(BOOKS);
    }
}
