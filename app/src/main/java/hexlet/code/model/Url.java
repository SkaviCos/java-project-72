package hexlet.code.model;

import java.time.LocalDateTime;

public final class Url {
    private long id;
    private String name;
    private LocalDateTime createdAt;
    private int lastStatusCode;
    private LocalDateTime lastCheckAt;

    public Url(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getLastStatusCode() {
        return lastStatusCode;
    }

    public void setLastStatusCode(int lastStatusCode) {
        this.lastStatusCode = lastStatusCode;
    }

    public LocalDateTime getLastCheckAt() {
        return lastCheckAt;
    }

    public void setLastCheckAt(LocalDateTime lastCheckAt) {
        this.lastCheckAt = lastCheckAt;
    }
}
