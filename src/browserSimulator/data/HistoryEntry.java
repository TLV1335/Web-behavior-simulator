package browserSimulator.data;

import browserSimulator.model.Page;
import java.time.LocalDateTime;

public class HistoryEntry {
    private Page page;
    private LocalDateTime accessTime;

    public HistoryEntry(Page page) {
        this.page = page;
        this.accessTime = LocalDateTime.now();
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public LocalDateTime getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(LocalDateTime accessTime) {
        this.accessTime = accessTime;
    }

    @Override
    public String toString() {
        return String.format("[%s] Accessed: %s", accessTime, page.getUrl());
    }
}