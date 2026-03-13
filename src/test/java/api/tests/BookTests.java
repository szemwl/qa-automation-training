package api.tests;

import api.model.Book;
import api.spec.ResponseSpec;
import data.TestDataGenerator;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BookTests extends BaseTest {

    @Test
    void testCreateBook() {

        Book book = TestDataGenerator.defaultBook();

        Book createdBook = bookClient.createBook(book);

        assertNotNull(createdBook.getId());
        assertEquals(book.getTitle(), createdBook.getTitle());
    }

    @Test
    void testGetBookById() {

        Book book = TestDataGenerator.defaultBook();

        Book createdBook = bookClient.createBook(book);

        Book receivedBook = bookClient.getBookById(createdBook.getId());

        assertEquals(createdBook.getId(), receivedBook.getId());
        assertEquals(createdBook.getTitle(), receivedBook.getTitle());
    }

    @Test
    void testGetAllBooks() {

        List<Book> booksArray = bookClient.getAllBooks();

        assertNotNull(booksArray);
        assertFalse(booksArray.isEmpty());
    }

    @Test
    void testDeleteBook() {

        Book book = TestDataGenerator.defaultBook();

        Book createdBook = bookClient.createBook(book);

        assertNotNull(createdBook.getId());

        bookClient.deleteBook(createdBook.getId());
    }

    @Test
    void testNegativeGetNonExistingBook() {

        int nonExistingId = 999_999_999;

        Response response = bookClient.getBookResponse(nonExistingId);

        assertEquals(404, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("data.TestDataGenerator#generateBooks")
    void testCreateBookParameterized(Book book) {

        Book createdBook = bookClient.createBook(book);

        assertNotNull(createdBook.getId());
    }

    @Test
    void testBookSchema() {

        Book book = TestDataGenerator.defaultBook();

        Response response = bookClient.createBookResponse(book);

        response.then()
                .spec(ResponseSpec.ok200())
                .body(matchesJsonSchemaInClasspath("schema/book-schema.json"));
    }
}
