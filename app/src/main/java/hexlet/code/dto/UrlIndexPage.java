package hexlet.code.dto;

import hexlet.code.model.Url;
import lombok.Getter;

import java.util.List;

@Getter
public class UrlIndexPage extends BasePage {
    List<Url> urls;

    public UrlIndexPage(List<Url> urls) {
        this.urls = urls;
    }
}
