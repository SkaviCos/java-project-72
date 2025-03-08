package hexlet.code.controller;

import hexlet.code.NamedRoutes;
import hexlet.code.dto.UrlIndexPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;

import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlController {
    public static void create(Context ctx) {
        String rawUrl = ctx.formParam("url");
        Url newUrl = null;
        try {
            URI uri = new URI(rawUrl);
            URL parsedUrl = uri.toURL();
            newUrl = new Url(parsedUrl.getProtocol() + "://" + parsedUrl.getHost() + ":" + parsedUrl.getPort());
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        try {
            Optional<Url> storedUrl = UrlRepository.find(newUrl.getName());
            if (storedUrl.isPresent()) {
                ctx.sessionAttribute("flash", "Страница уже существует");
            } else {
                UrlRepository.save(newUrl);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
            }
        } catch (SQLException e) {
            ctx.sessionAttribute("flash", "Ошибка при обращении к базе данных: " + e.getMessage());
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        ctx.redirect(NamedRoutes.urlsPath());
    }

    public static void index(Context ctx) {
        List<Url> urls;
        try {
            urls = UrlRepository.getEntities();
        }
        catch (SQLException e) {
            ctx.sessionAttribute("flash", "Ошибка при обращении к базе данных: " + e.getMessage());
            ctx.redirect(NamedRoutes.urlsPath());
            return;
        }

        UrlIndexPage page = new UrlIndexPage(urls);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        ctx.render("urls/index.jte", model("page", page));
    }
}
