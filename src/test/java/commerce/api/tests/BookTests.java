package commerce.api.tests;

import commerce.api.model.Book;
import commerce.api.spec.ResponseSpec;
import commerce.data.TestDataGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Feature("Управление книгами")
@Tag("api")
@DisplayName("Тесты API для книг")
public class BookTests extends BaseTest {

    @Test
    @Story("Создание книги")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверяет, что книга успешно создаётся и в ответе возвращается её id и title")
    @DisplayName("Создание книги")
    void testCreateBook() {

        Book book = TestDataGenerator.defaultBook();

        Book createdBook = bookClient.createBook(book);

        assertNotNull(createdBook.getId());
        assertEquals(book.getTitle(), createdBook.getTitle());
    }

    @Test
    @Story("Получение книги по id")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверяет, что книгу по её id")
    @DisplayName("Получение книги по id")
    void testGetBookById() {

        Book book = TestDataGenerator.defaultBook();

        Book createdBook = bookClient.createBook(book);

        Book receivedBook = bookClient.getBookById(createdBook.getId());

        assertEquals(createdBook.getId(), receivedBook.getId());
        assertEquals(createdBook.getTitle(), receivedBook.getTitle());
    }

    @Test
    @Story("Получение списка книг")
    @Severity(SeverityLevel.NORMAL)
    @Description("Проверяет, что API возвращает непустой список книг")
    @DisplayName("Получение списка всех книг")
    void testGetAllBooks() {

        List<Book> booksArray = bookClient.getAllBooks();

        assertNotNull(booksArray);
        assertFalse(booksArray.isEmpty());
    }

    @Test
    @Story("Удаление книги")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверяет, что ранее созданную книгу можно удалить")
    @DisplayName("Удаление книги")
    void testDeleteBook() {

        Book book = TestDataGenerator.defaultBook();

        Book createdBook = bookClient.createBook(book);

        assertNotNull(createdBook.getId());

        bookClient.deleteBook(createdBook.getId());
    }

    @Test
    @Story("Получение несуществующей книги")
    @Severity(SeverityLevel.NORMAL)
    @Description("Проверяет, что при запросе несуществующей книги по API возвращает 404")
    @DisplayName("Проверка получения несуществующей книги")
    void testNegativeGetNonExistingBook() {

        int nonExistingId = 999_999_999;

        Response response = bookClient.getBookResponse(nonExistingId);

        assertEquals(404, response.getStatusCode());
    }

    @ParameterizedTest(name = "Создание книги с набором данных: {0}")
    @MethodSource("commerce.data.TestDataGenerator#generateBooks")
    @Story("Создание книги с разными данными")
    @Severity(SeverityLevel.NORMAL)
    @Description("Проверяет создание книги на нескольких наборах тестовых данных")
    @DisplayName("Создание книги с разными тестовыми данными")
    void testCreateBookParameterized(Book book) {

        Book createdBook = bookClient.createBook(book);

        assertNotNull(createdBook.getId());
    }

    @Test
    @Story("Валидация JSON schema")
    @Severity(SeverityLevel.NORMAL)
    @Description("Проверяет, что ответ на осздание книги соответствует JSON-схеме")
    @DisplayName("Проверка схемы ответа книги")
    void testBookSchema() {

        Book book = TestDataGenerator.defaultBook();

        Response response = bookClient.createBookResponse(book);

        response.then()
                .spec(ResponseSpec.ok200())
                .body(matchesJsonSchemaInClasspath("schema/book-schema.json"));
    }
}
