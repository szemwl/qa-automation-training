package api.tests;

import api.model.Book;
import api.spec.ResponseSpec;
import data.TestDataGenerator;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Тесты API для книг")
public class BookTests extends BaseTest {

    @Test
    @DisplayName("Создание книги")
    void testCreateBook() {

        Book book = TestDataGenerator.defaultBook();

        Book createdBook = bookClient.createBook(book);

        assertNotNull(createdBook.getId());
        assertEquals(book.getTitle(), createdBook.getTitle());
    }

    @Test
    @DisplayName("Получение книги по id")
    void testGetBookById() {

        Book book = TestDataGenerator.defaultBook();

        Book createdBook = bookClient.createBook(book);

        Book receivedBook = bookClient.getBookById(createdBook.getId());

        assertEquals(createdBook.getId(), receivedBook.getId());
        assertEquals(createdBook.getTitle(), receivedBook.getTitle());
    }

    @Test
    @DisplayName("Получение списка всех книг")
    void testGetAllBooks() {

        List<Book> booksArray = bookClient.getAllBooks();

        assertNotNull(booksArray);
        assertFalse(booksArray.isEmpty());
    }

    @Test
    @DisplayName("Удаление книги")
    void testDeleteBook() {

        Book book = TestDataGenerator.defaultBook();

        Book createdBook = bookClient.createBook(book);

        assertNotNull(createdBook.getId());

        bookClient.deleteBook(createdBook.getId());
    }

    @Test
    @DisplayName("Проверка получения несуществующей книги")
    void testNegativeGetNonExistingBook() {

        int nonExistingId = 999_999_999;

        Response response = bookClient.getBookResponse(nonExistingId);

        assertEquals(404, response.getStatusCode());
    }

    @ParameterizedTest(name = "Создание книги с набором данных: {0}")
    @MethodSource("data.TestDataGenerator#generateBooks")
    @DisplayName("Создание книги с разными тестовыми данными")
    void testCreateBookParameterized(Book book) {

        Book createdBook = bookClient.createBook(book);

        assertNotNull(createdBook.getId());
    }

    @Test
    @DisplayName("Проверка схемы ответа книги")
    void testBookSchema() {

        Book book = TestDataGenerator.defaultBook();

        Response response = bookClient.createBookResponse(book);

        response.then()
                .spec(ResponseSpec.ok200())
                .body(matchesJsonSchemaInClasspath("schema/book-schema.json"));
    }
}
