package browserSimulator.service;

import browserSimulator.model.BookmarkItem;
import browserSimulator.model.Page;

import java.util.*;

/**
 * Manages bookmarks using a HashSet for O(1) operations.
 * Supports Binary Search on sorted list for fast lookup.
 */
public class BookmarkManager {
    private HashSet<BookmarkItem> bookmarks;
    private List<BookmarkItem> sortedList;
    private boolean isSorted;

    public BookmarkManager() {
        this.bookmarks = new HashSet<>();
        this.sortedList = new ArrayList<>();
        this.isSorted = false;
    }

    // ========== Add Operations ==========

    /**
     * Add a bookmark from a Page object.
     * Convenience method for UI.
     */
    public boolean addBookmark(Page page) {
        return addBookmark(BookmarkItem.fromPage(page));
    }

    /**
     * Add a BookmarkItem directly.
     * Prevents duplicates automatically via HashSet.
     */
    public boolean addBookmark(BookmarkItem item) {
        if (bookmarks.contains(item)) {
            System.out.println("Page already bookmarked: " + item.getUrl());
            return false;
        }

        bookmarks.add(item);
        sortedList.add(item);
        isSorted = false;
        System.out.println("Bookmark added: " + item.getUrl());
        return true;
    }

    /**
     * Add bookmark by URL and title directly.
     */
    public boolean addBookmark(String url, String title) {
        return addBookmark(new BookmarkItem(url, title));
    }

    // ========== Remove Operations ==========

    /**
     * Remove a bookmark by URL string.
     */
    public boolean removeBookmark(String url) {
        BookmarkItem target = findByUrl(url);
        if (target == null) {
            System.out.println("Bookmark not found: " + url);
            return false;
        }
        return removeBookmark(target);
    }

    /**
     * Remove a BookmarkItem directly.
     */
    public boolean removeBookmark(BookmarkItem item) {
        if (!bookmarks.contains(item)) {
            System.out.println("Bookmark not found: " + item.getUrl());
            return false;
        }

        bookmarks.remove(item);
        sortedList.remove(item);
        isSorted = false;
        System.out.println("Bookmark removed: " + item.getUrl());
        return true;
    }

    // ========== Search Operations ==========

    /**
     * Search for a bookmark by exact URL using Binary Search O(log N).
     * Requires sorted list.
     */
    public BookmarkItem searchBookmark(String targetUrl) {
        // Ensure list is sorted
        if (!isSorted) {
            Collections.sort(sortedList, Comparator.comparing(BookmarkItem::getUrl));
            isSorted = true;
        }

        int low = 0;
        int high = sortedList.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            int comparison = sortedList.get(mid).getUrl().compareToIgnoreCase(targetUrl);

            if (comparison == 0) {
                return sortedList.get(mid);
            } else if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return null;
    }

    /**
     * Search bookmarks by keyword (contains match) using Linear Scan.
     * Useful for partial/fuzzy search.
     */
    public List<BookmarkItem> searchBookmarkByKeyword(String keyword) {
        List<BookmarkItem> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (BookmarkItem item : bookmarks) {
            if (item.getUrl().toLowerCase().contains(lowerKeyword) ||
                (item.getTitle() != null && item.getTitle().toLowerCase().contains(lowerKeyword))) {
                results.add(item);
            }
        }
        return results;
    }

    /**
     * Find a BookmarkItem by URL (Linear Scan).
     * Used internally for removal by URL.
     */
    private BookmarkItem findByUrl(String url) {
        for (BookmarkItem item : bookmarks) {
            if (item.getUrl().equalsIgnoreCase(url)) {
                return item;
            }
        }
        return null;
    }

    // ========== Check Operations ==========

    public boolean isBookmarked(String url) {
        return findByUrl(url) != null;
    }

    public boolean isBookmarked(Page page) {
        return isBookmarked(page.getUrl());
    }

    // ========== Getter Operations ==========

    public Set<BookmarkItem> getAllBookmarks() {
        return new HashSet<>(bookmarks);
    }

    public List<BookmarkItem> getSortedBookmarks() {
        if (!isSorted) {
            Collections.sort(sortedList, Comparator.comparing(BookmarkItem::getUrl));
            isSorted = true;
        }
        return new ArrayList<>(sortedList);
    }

    public int getBookmarkCount() {
        return bookmarks.size();
    }

    // ========== Display Operations ==========

    public void displayBookmarks() {
        if (bookmarks.isEmpty()) {
            System.out.println("No bookmarks.");
            return;
        }

        System.out.println("=== Bookmarks ===");
        int i = 1;
        for (BookmarkItem item : bookmarks) {
            System.out.printf("%d. %s - %s (Saved: %s)%n",
                    i++, item.getTitle(), item.getUrl(), item.getSavedAt().toLocalDate());
        }
    }

    // ========== Conversion Operations ==========

    /**
     * Get all bookmarks as Page objects.
     * Useful for SearchEngine or FileHandler that works with Page.
     */
    public List<Page> getAllBookmarksAsPages() {
        List<Page> pages = new ArrayList<>();
        for (BookmarkItem item : bookmarks) {
            pages.add(item.toPage());
        }
        return pages;
    }

    /**
     * Search bookmark by keyword and return as Page objects.
     */
    public List<Page> searchBookmarksAsPages(String keyword) {
        List<Page> pages = new ArrayList<>();
        for (BookmarkItem item : searchBookmarkByKeyword(keyword)) {
            pages.add(item.toPage());
        }
        return pages;
    }

    // ========== Clear ==========

    public void clearBookmarks() {
        bookmarks.clear();
        sortedList.clear();
        isSorted = false;
        System.out.println("All bookmarks cleared.");
    }
}