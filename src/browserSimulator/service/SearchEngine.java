package browserSimulator.service;

import browserSimulator.model.BookmarkItem;
import browserSimulator.model.Page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SearchEngine {
    private HistoryManager historyManager;
    private BookmarkManager bookmarkManager;

    public SearchEngine() {
        this.historyManager = null;
        this.bookmarkManager = null;
    }

    public SearchEngine(HistoryManager historyManager, BookmarkManager bookmarkManager) {
        this.historyManager = historyManager;
        this.bookmarkManager = bookmarkManager;
    }

    public void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public void setBookmarkManager(BookmarkManager bookmarkManager) {
        this.bookmarkManager = bookmarkManager;
    }

    public List<Page> searchHistory(String keyword) {
        if (historyManager == null) {
            System.out.println("HistoryManager not set.");
            return new ArrayList<>();
        }

        List<Page> results = historyManager.searchByPrefix(keyword);
        List<Page> titleResults = searchHistoryByTitle(keyword);
        for (Page page : titleResults) {
            if (!results.contains(page)) {
                results.add(page);
            }
        }

        return results;
    }

    public List<Page> searchHistoryByTitle(String keyword) {
        if (historyManager == null || keyword == null || keyword.trim().isEmpty()) return new ArrayList<>();

        List<Page> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Page page : historyManager.getAllHistory()) {
            if (page != null && page.getTitle() != null && page.getTitle().toLowerCase().contains(lowerKeyword)) {
                results.add(page);
            }
        }
        return results;
    }

    public List<Page> searchHistoryLinear(String keyword) {
        if (historyManager == null || keyword == null || keyword.trim().isEmpty()) return new ArrayList<>();

        List<Page> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (Page page : historyManager.getAllHistory()) {
            if (page != null && page.getUrl() != null && (page.getUrl().toLowerCase().contains(lowerKeyword) ||
                (page.getTitle() != null && page.getTitle().toLowerCase().contains(lowerKeyword)))) {
                results.add(page);
            }
        }
        return results;
    }

    public Page searchBookmark(String url) {
        if (bookmarkManager == null) {
            System.out.println("BookmarkManager not set.");
            return null;
        }
        BookmarkItem item = bookmarkManager.searchBookmark(url);
        return item != null ? item.toPage() : null;
    }

    public List<Page> searchBookmarks(String keyword) {
        if (bookmarkManager == null || keyword == null || keyword.trim().isEmpty()) return new ArrayList<>();
        return bookmarkManager.searchBookmarksAsPages(keyword);
    }

    public Map<String, List<Page>> searchAll(String keyword) {
        Map<String, List<Page>> results = new HashMap<>();
        results.put("history", searchHistory(keyword));
        results.put("bookmarks", searchBookmarks(keyword));
        return results;
    }

    public List<Page> sortHistoryByDate() {
        if (historyManager == null) return new ArrayList<>();

        List<Page> list = historyManager.getAllHistory();
        return mergeSortByDate(list);
    }

    private List<Page> mergeSortByDate(List<Page> list) {
        if (list.size() <= 1) {
            return list;
        }

        int mid = list.size() / 2;
        List<Page> left = mergeSortByDate(list.subList(0, mid));
        List<Page> right = mergeSortByDate(list.subList(mid, list.size()));

        return mergeSortedByDate(left, right);
    }

    private List<Page> mergeSortedByDate(List<Page> left, List<Page> right) {
        List<Page> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            if (left.get(i).getTimestamp().isBefore(right.get(j).getTimestamp())) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }

        while (i < left.size()) result.add(left.get(i++));
        while (j < right.size()) result.add(right.get(j++));

        return result;
    }

    public List<Page> sortBookmarksByUrl() {
        if (bookmarkManager == null) return new ArrayList<>();

        List<Page> list = bookmarkManager.getAllBookmarksAsPages();
        Collections.sort(list, Comparator.comparing(Page::getUrl));
        return list;
    }

    public void displaySearchResults(List<Page> results, String keyword) {
        if (results.isEmpty()) {
            System.out.println("No matches found for: " + keyword);
            return;
        }

        System.out.println("=== Search Results for: " + keyword + " ===");
        System.out.println("Found " + results.size() + " matches:");
        for (Page page : results) {
            System.out.println("  - " + page);
        }
    }

    public void displayCombinedResults(Map<String, List<Page>> results, String keyword) {
        System.out.println("=== Combined Search Results for: " + keyword + " ===");

        for (Map.Entry<String, List<Page>> entry : results.entrySet()) {
            System.out.println("[" + entry.getKey() + "] " + entry.getValue().size() + " matches:");
            for (Page page : entry.getValue()) {
                System.out.println("  - " + page);
            }
            System.out.println();
        }
    }

    public List<Page> deepSearch(String keyword) {
        if (historyManager == null) {
            System.out.println("HistoryManager not set.");
            return new ArrayList<>();
        }

        System.out.println("Performing deep search across all history chunks...");
        return historyManager.deepSearch(keyword);
    }

    public List<Page> searchWithFallback(String keyword) {
        List<Page> results = searchHistory(keyword);

        if (!results.isEmpty()) {
            System.out.println("Found " + results.size() + " matches in hot data.");
            return results;
        }

        System.out.println("No results found in hot data.");
        System.out.print("Do you want to search in cold storage? (y/n): ");

        try (Scanner scanner = new Scanner(System.in)) {
            String choice = scanner.nextLine().trim().toLowerCase();
            if (choice.equals("y") || choice.equals("yes")) {
                return deepSearch(keyword);
            }
        }

        System.out.println("Deep search cancelled.");
        return new ArrayList<>();
    }
}