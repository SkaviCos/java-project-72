package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.dto.BasePage;
import hexlet.code.model.Url;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.javalin.rendering.template.TemplateUtil.model;

@Slf4j
public class App {
    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }

    public static Javalin getApp() throws Exception {
        var hikaryConfig = new HikariConfig();
        String jdbcUrl = System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
        hikaryConfig.setJdbcUrl(jdbcUrl);
        var dataSource = new HikariDataSource(hikaryConfig);

        var schemaFileName = System.getenv().getOrDefault("SCHEMA_FILE_NAME", "schema.sql");
        var url = App.class.getClassLoader().getResourceAsStream(schemaFileName);
        var sql = new BufferedReader(new InputStreamReader(url))
                .lines().collect(Collectors.joining("\n"));

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.dataSource = dataSource;

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        app.get(NamedRoutes.rootPath(), ctx -> {
            BasePage page = new BasePage();
            page.setFlash(ctx.consumeSessionAttribute("flash"));
            ctx.render("index.jte", model("page", page));
        });

        app.post(NamedRoutes.urlsPath(), ctx -> {
            String rawUrl = ctx.formParam("url");
            Url newUrl = null;
            try {
                URI uri = new URI(rawUrl);
                URL parsedUrl = uri.toURL();
                newUrl = new Url(parsedUrl.getProtocol() + "://" + parsedUrl.getHost() + ":" + parsedUrl.getPort());
            } catch (RuntimeException e) {
                ctx.sessionAttribute("flash", "Некорректный URL");
                ctx.redirect(NamedRoutes.rootPath());
                return;
            }

            Optional<Url> storedUrl = UrlRepository.find(newUrl.getName());
            if (storedUrl.isPresent()) {
                ctx.sessionAttribute("flash", "Страница уже существует");
            } else {
                UrlRepository.save(newUrl);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
            }
            ctx.redirect(NamedRoutes.urlsPath());
        });

        return app;
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }

    public static void main(String[] args) {
        try {
            Javalin app = getApp();
            app.start(getPort());
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}