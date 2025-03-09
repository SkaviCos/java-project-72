package hexlet.code.controller;

import hexlet.code.dto.UrlPage;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.util.NamedRoutes;
import hexlet.code.dto.UrlIndexPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static io.javalin.rendering.template.TemplateUtil.model;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class UrlController {
    public static void create(Context ctx) throws SQLException {
        URL parsedUrl;
        String rawUrl = ctx.formParam("url");

        try {
            URI uri = new URI(rawUrl);
            parsedUrl = uri.toURL();
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        String newUrlName = parsedUrl.getProtocol() + "://" + parsedUrl.getHost();
        if (parsedUrl.getPort() > 0) {
            newUrlName += ":" + parsedUrl.getPort();
        }
        Url newUrl = new Url(newUrlName);
        Optional<Url> storedUrl = UrlRepository.find(newUrl.getName());
        if (storedUrl.isPresent()) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.redirect(NamedRoutes.urlsPath());
        } else {
            UrlRepository.save(newUrl);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("successFlag", "true");
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }

    public static void index(Context ctx) throws SQLException {
        List<Url> urls = UrlRepository.getEntities();
        UrlIndexPage page = new UrlIndexPage(urls);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        if (ctx.consumeSessionAttribute("successFlag") == "true") {
            page.setSuccessFlag(true);
        }
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Entity with id = " + id + " not found"));
        List<UrlCheck> urlChecks = UrlCheckRepository.getAllUrlChecks(id);

        UrlPage page = new UrlPage(url, urlChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        if (ctx.consumeSessionAttribute("successFlag") == "true") {
            page.setSuccessFlag(true);
        }

        ctx.render("urls/show.jte", model("page", page));
    }

    public static void checkUrl(Context ctx) throws SQLException {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Entity with id = \" + id + \" not found"));

        try {
            HttpResponse<String> response = Unirest.get(url.getName()).asString();
            Document doc = Jsoup.parse(response.getBody());
            int statusCode = response.getStatus();
            String title = doc.title();
            Element h1Element = doc.selectFirst("h1");
            String h1 = h1Element == null ? "" : h1Element.text();
            Element descElement = doc.selectFirst("meta[name=description]");
            String desc = descElement == null ? "" : descElement.attr("content");
            UrlCheckRepository.save(new UrlCheck(statusCode, title, h1, desc, id));
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
        } catch (Exception e) {
            ctx.sessionAttribute("flash", e.getMessage());
        }


        Unirest.shutDown();
        ctx.redirect(NamedRoutes.urlPath(id.toString()));
    }
}
