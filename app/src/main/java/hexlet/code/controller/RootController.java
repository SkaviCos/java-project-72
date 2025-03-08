package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import io.javalin.http.Context;

import static io.javalin.rendering.template.TemplateUtil.model;

public class RootController {
    public static void index(Context ctx) {
        BasePage page = new BasePage();
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        if (ctx.consumeSessionAttribute("successFlag") == "true") {
            page.setSuccessFlag(true);
        }
        ctx.render("index.jte", model("page", page));
    }
}
