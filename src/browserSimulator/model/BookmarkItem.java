package browserSimulator.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a bookmark entry in the browser.
 * Stores URL, title, and the time when it was saved.
 */
public class BookmarkItem {
    private String url;
    private String title;
    private LocalDateTime savedAt;

    // ========== Constructors ==========

    public BookmarkItem(String url, String title) {
        this.url = url;
        this.title = title;
        this.savedAt = LocalDateTime.now();
    }

    public BookmarkItem(String url, String title, LocalDateTime savedAt) {
        this.url = url;
        this.title = title;
        this.savedAt = savedAt;
    }

    // ========== Getters and Setters ==========

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }

    // ========== Helper Methods ==========

    /**
     * Creates a BookmarkItem from a Page object.
     * Convenience method for converting Page to BookmarkItem.
     */
    public static BookmarkItem fromPage(Page page) {
        return new BookmarkItem(page.getUrl(), page.getTitle());
    }

    /**
     * Converts this BookmarkItem back to a Page object.
     * Useful when need to display bookmark in history-like format.
     */
    public Page toPage() {
        return new Page(url, title, "", savedAt);
    }

    // ========== Override Methods ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookmarkItem that = (BookmarkItem) o;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s", savedAt.toLocalDate(), title, url);
    }
}