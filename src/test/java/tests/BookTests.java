package tests;

import api.model.Book;
import api.spec.RequestSpec;
import api.spec.ResponseSpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BookTests extends BaseTest {

    @Test
    void testCreateBook() {

        Book book = Book
                .builder()
                .id(1)
                .title("Book 1")
                .description("Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n")
                .pageCount(100)
                .excerpt("Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n" +
                        "Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n" +
                        "Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n" +
                        "Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n" +
                        "Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n")
                .publishDate("2026-03-03T10:38:24.3862865+00:00")
                .build();

        Book createdBook = bookClient.createBook(book);

        assertNotNull(createdBook.getId());
        assertEquals(book.getTitle(), createdBook.getTitle());
    }

    @Test
    void testGetBookById() {

        Book book = Book
                .builder()
                .id(1)
                .title("Book 1")
                .description("Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n")
                .pageCount(100)
                .excerpt("Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n" +
                        "Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n" +
                        "Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n" +
                        "Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n" +
                        "Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n")
                .publishDate("2026-03-03T10:38:24.3862865+00:00")
                .build();

        Book recivedBook = bookClient.getBookById(book.getId());

        assertEquals(book.getId(), recivedBook.getId());
        assertNotNull(recivedBook.getTitle());
    }

    @Test
    void testGetAllBooks() {

        List<Book> booksArray = bookClient.getAllBooks();

        assertNotNull(booksArray);
        assertFalse(booksArray.isEmpty());
    }

    @Test
    void testDeleteBook() {

        Book book = Book
                .builder()
                .id(1)
                .title("Book 1")
                .description("Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n")
                .pageCount(100)
                .excerpt("Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n" +
                        "Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n" +
                        "Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n" +
                        "Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n" +
                        "Lorem lorem lorem. Lorem lorem lorem. Lorem lorem lorem.\n")
                .publishDate("2026-03-03T10:38:24.3862865+00:00")
                .build();

        Book createdBook = bookClient.createBook(book);

        assertNotNull(createdBook.getId());

        bookClient.deleteBook(createdBook.getId());
    }

    @Test
    void testNegativeGetNonExistingBook() {

        int nonExistingId = 999_999_999;

        int statusCode = bookClient.getBookStatusCode(nonExistingId);

        assertEquals(404, statusCode);
    }

    @ParameterizedTest
    @MethodSource("data.TestDataGenerator#generateBooks")
    void testCreateBookParameterized(Book book) {

        Book createdBook = bookClient.createBook(book);

        assertNotNull(createdBook.getId());
    }

    @Test
    void testBookSchema() {

        Book book = Book.builder()
                .id(5555)
                .title("Schema Test")
                .description("Desc")
                .pageCount(123)
                .excerpt("Ex")
                .publishDate("2024-01-01T00:00:00")
                .build();

        given()
                .spec(RequestSpec.requestSpec())
                .body(book)
                .when()
                .post("/api/v1/Books")
                .then()
                .spec(ResponseSpec.ok200())
                .body(matchesJsonSchemaInClasspath("schema/book-schema.json"));
    }
}
