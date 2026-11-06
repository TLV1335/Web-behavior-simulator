package browserSimulator.model;

import java.time.LocalDateTime;

public class Page {
    private String url;
    private String title;
    private String content;
    private LocalDateTime timestamp;

    // Constructors
    public Page(String url, String title, String content) {
        this.url = url;
        this.title = title;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public Page(String url, String title, String content, LocalDateTime timestamp) {
        this.url = url;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return url != null && url.equals(page.url);
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s", timestamp, title, url);
    }
}