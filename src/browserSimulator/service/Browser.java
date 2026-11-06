package browserSimulator.service;

import browserSimulator.model.BookmarkItem;
import browserSimulator.model.DownloadTask;
import browserSimulator.model.Page;
import browserSimulator.model.Tab;
import browserSimulator.util.FileHandler;
import browserSimulator.util.InputValidator;

import java.util.List;
import java.util.Scanner;

/**
 * Main controller/facade class for the browser simulator.
 * Coordinates all managers and provides a high-level API for the UI.
 */
public class Browser {
    private TabManager tabManager;
    private BookmarkManager bookmarkManager;
    private HistoryManager historyManager;
    private CacheManager cacheManager;
    private DownloadManager downloadManager;
    private SearchEngine searchEngine;

    public Browser() {
        this.historyManager = new HistoryManager();
        this.bookmarkManager = new BookmarkManager();
        this.cacheManager = new CacheManager();
        this.downloadManager = new DownloadManager();
        this.tabManager = new TabManager(historyManager);
        this.searchEngine = new SearchEngine(historyManager, bookmarkManager);

        tabManager.createTab();
        loadData();
    }

    public void clearHistory() {
        historyManager.clearHistory();
        System.out.println("History cleared.");
    }

    public void clearAllHistory(){
        historyManager.clearAllChunks();
        System.out.println("Done");
    }

    public void visitPage(String url) {
        url = InputValidator.normalizeUrl(url);

        Tab activeTab = tabManager.getActiveTab();
        if (activeTab == null) {
            activeTab = tabManager.createTab();
        }

        Page cached = cacheManager.getFromCache(url);
        if (cached != null) {
            activeTab.getNavigationManager().visitNewPage(cached);
            return;
        }

        String title = "Page: " + url;
        String content = "Content of " + url;
        Page page = new Page(url, title, content);

        cacheManager.addToCache(page);
        activeTab.getNavigationManager().visitNewPage(page);
    }

    public void goBack() {
        Tab activeTab = tabManager.getActiveTab();
        if (activeTab == null) {
            System.out.println("No active tab.");
            return;
        }
        activeTab.getNavigationManager().goBack();
    }

    public void goForward() {
        Tab activeTab = tabManager.getActiveTab();
        if (activeTab == null) {
            System.out.println("No active tab.");
            return;
        }
        activeTab.getNavigationManager().goForward();
    }

    public void showHistory() {
        historyManager.displayTimeline();
    }

    public void addBookmark(Page page) {
        bookmarkManager.addBookmark(page);
    }

    public void addBookmarkByUrl(String url) {
        Page page = historyManager.searchByPrefix(url).stream()
                .filter(p -> p.getUrl() != null && p.getUrl().equalsIgnoreCase(url))
                .findFirst()
                .orElse(null);

        if (page == null) {
            page = new Page(url, "Page: " + url, "");
        }
        bookmarkManager.addBookmark(page);
    }

    public void removeBookmarkByUrl(String url) {
        bookmarkManager.removeBookmark(url);
    }

    public void showBookmarks() {
        bookmarkManager.displayBookmarks();
    }

    public void createNewTab() {
        tabManager.createTab();
    }

    public void switchTab(int index) {
        tabManager.switchTab(index);
    }

    public void closeTab(int index) {
        tabManager.closeTab(index);
    }

    public void showTabs() {
        tabManager.displayTabs();
    }

    public void showStorageInfo() {
        historyManager.displayChunkInfo();
    }

    public void search(String keyword, Scanner scanner) {
        if (!InputValidator.isValidSearchKeyword(keyword)) {
            System.out.println("Invalid search keyword.");
            return;
        }

        List<Page> results = searchEngine.searchHistory(keyword);
        if (!results.isEmpty()) {
            System.out.println("=== Fast Search Results (Hot Data) ===");
            for (Page page : results) {
                System.out.println("  - " + page);
            }
            return;
        }

        System.out.println("No results found in hot data.");
        System.out.print("Do you want to search in cold storage (y/n)? ");

        try {
            String choice = scanner.nextLine().trim().toLowerCase();
            if (choice.equals("y") || choice.equals("yes")) {
                List<Page> deepResults = searchEngine.deepSearch(keyword);
                System.out.println("=== Deep Search Results (Cold Storage) ===");
                for (Page page : deepResults) {
                    System.out.println("  - " + page);
                }
            } else {
                System.out.println("Search cancelled.");
            }
        }finally{}
    }

    public void downloadFile(String url) {
        url = InputValidator.normalizeUrl(url);
        DownloadTask task = new DownloadTask(url, "downloads/" + url.replaceAll("[^a-zA-Z0-9]", "_"));
        downloadManager.enqueueDownload(task);
    }

    public void showDownloads() {
        downloadManager.displayQueueStatus();
    }

    public void processDownloads() {
        downloadManager.processAllDownloads();
    }

    public void showCache() {
        cacheManager.displayCacheStatus();
    }

    public void clearCache() {
        cacheManager.clearCache();
    }

    public void saveData() {
        FileHandler.saveHistory(historyManager);
        FileHandler.saveBookmarks(bookmarkManager);
    }

    public void loadData() {
        if (historyManager.getHistorySize() == 0) {
            List<Page> historyPages = FileHandler.loadHistory();
            for (Page page : historyPages) {
                historyManager.addHistory(page);
            }
        }

        List<BookmarkItem> bookmarkItems = FileHandler.loadBookmarks();
        for (BookmarkItem item : bookmarkItems) {
            bookmarkManager.addBookmark(item);
        }
    }

    public void showStatus() {
        System.out.println("=== Browser Status ===");
        System.out.println("Tabs: " + tabManager.getTabCount());
        System.out.println("History: " + historyManager.getHistorySize() + " entries");
        System.out.println("Bookmarks: " + bookmarkManager.getBookmarkCount() + " entries");
        System.out.println("Cache: " + cacheManager.getCacheSize() + " pages");
        System.out.println("Downloads: " + downloadManager.getQueueSize() + " pending");
    }
}