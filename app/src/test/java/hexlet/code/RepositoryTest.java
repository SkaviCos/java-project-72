package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryTest {
    private static Url url;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final PrintStream standardOut = System.out;
    private static final int CREATED_CHECKS_COUNT = 5;

    @BeforeAll
    public static void prepareDataBase() throws Exception {
        App.getApp();
    }

    @BeforeEach
    public final void setUp() {
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    public final void tearDown() {
        System.setOut(standardOut);
    }

    @AfterAll
    public static void closeDataSource() {
        BaseRepository.dataSource.close();
    }

    @Test
    @Order(1)
    public void createRecord() throws SQLException {
        int recordsCount = UrlRepository.getEntities().size();

        url = new Url("https://ru.hexlet.io/courses/java-web/lessons/flash/theory_unit");
        UrlRepository.save(url);

        assertEquals(++recordsCount, UrlRepository.getEntities().size());
    }

    @Test
    @Order(2)
    public void findRecordById() throws SQLException {
        Url storedUrl = UrlRepository.find(url.getId()).get();
        assertEquals(url.getId(), storedUrl.getId());
        assertEquals(url.getName(), storedUrl.getName());
        assertEquals(url.getCreatedAt().getDate(), storedUrl.getCreatedAt().getDate());
        assertEquals(url.getCreatedAt().getTime(), storedUrl.getCreatedAt().getTime());
    }

    @Test
    @Order(3)
    public void findRecordByName() throws SQLException {
        Url storedUrl = UrlRepository.find(url.getName()).get();
        assertEquals(url.getId(), storedUrl.getId());
        assertEquals(url.getName(), storedUrl.getName());
        assertEquals(url.getCreatedAt().getDate(), storedUrl.getCreatedAt().getDate());
        assertEquals(url.getCreatedAt().getTime(), storedUrl.getCreatedAt().getTime());
    }

    @Test
    @Order(4)
    public void createChecks() throws SQLException {
        int recordsCount = UrlCheckRepository.getAllUrlChecks(url.getId()).size();
        for (int i = 1; i <= CREATED_CHECKS_COUNT; i++) {
            UrlCheck urlCheck = new UrlCheck(200, "title text " + i, "h1 text" + i, "description text", url.getId());
            UrlCheckRepository.save(urlCheck);
        }
        recordsCount += CREATED_CHECKS_COUNT;

        List<UrlCheck> urlChecks = UrlCheckRepository.getAllUrlChecks(url.getId());
        assertEquals(recordsCount, urlChecks.size());

        assertEquals("h1 text" + CREATED_CHECKS_COUNT, urlChecks.get(0).getH1());

        UrlCheck lastCheck = UrlCheckRepository.getLastUrlCheck(url.getId()).get();
        assertEquals(urlChecks.get(0).getId(), lastCheck.getId());
    }
}