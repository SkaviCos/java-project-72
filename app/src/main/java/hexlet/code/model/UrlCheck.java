package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class UrlCheck {
    @Setter
    private long id;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private long urlId;
    @Setter
    private LocalDateTime createdAt;

    public UrlCheck(int statusCode, String title, String h1, String description, long urlId) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.urlId = urlId;
    }
}
