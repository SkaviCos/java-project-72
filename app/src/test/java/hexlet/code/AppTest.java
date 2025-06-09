package hexlet.code;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTest {
    private static final String TEST_TITLE = "Проверка title";
    private static final String TEST_DESC = "Проверка description";
    private static final String TEST_H_1 = "Проверка h1";
    static Javalin app;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final PrintStream standardOut = System.out;
    private static MockWebServer mockServer;
    private static String mockUrl;

    @BeforeAll
    public static final void setUpAll() throws Exception {
        mockServer = new MockWebServer();
        MockResponse mockResponse = new MockResponse();
        String testHtmlPageBody = Files.readString(Paths.get("src/test/resources/testPage.html"));
        mockResponse.setBody(testHtmlPageBody);
        mockServer.enqueue(mockResponse);
        mockServer.start();
        mockUrl = mockServer.url("/").toString();
    }

    @BeforeEach
    public final void setUpEach() throws Exception {
        app = App.getApp();
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    public final void tearDown() {
        System.setOut(standardOut);
    }

    @AfterAll
    public static void closeDataSource() throws IOException {
        BaseRepository.dataSource.close();
        mockServer.shutdown();
    }

    @Test
    @Order(1)
    public final void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("Бесплатно проверяйте сайты на SEO пригодность"));
        });
    }

    @Test
    @Order(2)
    public final void createUrl() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            int recordsCount = UrlRepository.getEntities().size();

            String requestBody = "url=" + mockUrl;
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains(mockUrl.substring(0, mockUrl.length() - 1)));

            assertEquals(++recordsCount, UrlRepository.getEntities().size());

            Long id = UrlRepository.find(mockUrl.substring(0, mockUrl.length() - 1)).get().getId();

            response = client.get(NamedRoutes.urlPath(id.toString()));
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains(mockUrl.substring(0, mockUrl.length() - 1)));
        });
    }

    @Test
    @Order(3)
    public final void testNotFoundUrlPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath("999"));
            assertEquals(404, response.code());
        });
    }

    @Disabled("Временно выключил")
    @Test
    @Order(4)
    public final void testUrlCheck() {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "url=" + mockUrl;
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            String responseBodyString = response.body().string();
            assertEquals(200, response.code());
            assertTrue(responseBodyString.contains(mockUrl.substring(0, mockUrl.length() - 1)));

            Long id = UrlRepository.find(mockUrl.substring(0, mockUrl.length() - 1)).get().getId();

            response = client.post(NamedRoutes.postCheckPath(id.toString()));
            responseBodyString = response.body().string();
            assertEquals(200, response.code());
            assertTrue(responseBodyString.contains(TEST_TITLE));
            assertTrue(responseBodyString.contains(TEST_DESC));
            assertTrue(responseBodyString.contains(TEST_H_1));

            UrlCheck urlCheck = UrlCheckRepository.getLastUrlCheck(id).get();
            assertEquals(id, urlCheck.getUrlId());
            assertEquals(TEST_TITLE, urlCheck.getTitle());
            assertEquals(TEST_DESC, urlCheck.getDescription());
            assertEquals(TEST_H_1, urlCheck.getH1());
        });
    }
}
