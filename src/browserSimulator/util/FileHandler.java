package browserSimulator.util;

import browserSimulator.model.BookmarkItem;
import browserSimulator.model.Page;
import browserSimulator.service.BookmarkManager;
import browserSimulator.service.HistoryManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileHandler {
    private static final String HISTORY_DIR = "history/";
    private static final String CHUNK_PREFIX = "history_chunk_";
    private static final String CHUNK_SUFFIX = ".txt";
    private static final String HISTORY_DATA_FILE = HISTORY_DIR + "history.txt";
    private static final String BOOKMARKS_DATA_FILE = HISTORY_DIR + "bookmarks.txt";

    private static void ensureDirectory() {
        File dir = new File(HISTORY_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void saveChunk(int chunkIndex, List<Page> pages) {
        ensureDirectory();

        String filename = HISTORY_DIR + CHUNK_PREFIX + chunkIndex + CHUNK_SUFFIX;
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Page page : pages) {
                writer.printf("%s|%s|%s|%s%n",
                    page.getUrl(),
                    page.getTitle() != null ? page.getTitle() : "",
                    page.getContent() != null ? page.getContent() : "",
                    page.getTimestamp() != null ? page.getTimestamp().toString() : ""
                );
            }
            System.out.println("Saved chunk " + chunkIndex + " with " + pages.size() + " entries.");
        } catch (IOException e) {
            System.err.println("Error saving chunk " + chunkIndex + ": " + e.getMessage());
        }
    }

    public static List<Page> loadChunk(int chunkIndex) {
        List<Page> pages = new ArrayList<>();
        String filename = HISTORY_DIR + CHUNK_PREFIX + chunkIndex + CHUNK_SUFFIX;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int firstPipe = line.indexOf('|');
                int secondPipe = line.indexOf('|', firstPipe + 1);
                int thirdPipe = line.indexOf('|', secondPipe + 1);

                // Đảm bảo tìm thấy đủ 3 dấu gạch đứng
                if (firstPipe != -1 && secondPipe != -1 && thirdPipe != -1) {
                    try {
                        String url = line.substring(0, firstPipe);
                        String title = line.substring(firstPipe + 1, secondPipe);
                        String content = line.substring(secondPipe + 1, thirdPipe);
                        String timeStr = line.substring(thirdPipe + 1);

                        Page page = new Page(url, title, content, LocalDateTime.parse(timeStr));
                        pages.add(page);
                    } catch (Exception e) {
                        System.err.println("Skipping invalid entry in chunk " + chunkIndex + ": " + line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Chunk " + chunkIndex + " not found.");
        } catch (IOException e) {
            System.err.println("Error loading chunk " + chunkIndex + ": " + e.getMessage());
        }
        return pages;
    }

    public static int getLatestChunkIndex() {
        File dir = new File(HISTORY_DIR);
        if (!dir.exists()) {
            return -1;
        }

        File[] files = dir.listFiles((d, name) -> name.startsWith(CHUNK_PREFIX) && name.endsWith(CHUNK_SUFFIX));
        if (files == null || files.length == 0) {
            return -1;
        }

        int maxIndex = -1;
        for (File file : files) {
            String name = file.getName();
            String indexStr = name.replace(CHUNK_PREFIX, "").replace(CHUNK_SUFFIX, "");
            try {
                int index = Integer.parseInt(indexStr);
                if (index > maxIndex) {
                    maxIndex = index;
                }
            } catch (NumberFormatException e) {
                // Ignore invalid chunk names.
            }
        }
        return maxIndex;
    }

    public static int getTotalEntries() {
        File dir = new File(HISTORY_DIR);
        if (!dir.exists()) {
            return 0;
        }

        int total = 0;
        File[] files = dir.listFiles((d, name) -> name.startsWith(CHUNK_PREFIX) && name.endsWith(CHUNK_SUFFIX));
        if (files == null) {
            return 0;
        }

        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                while (reader.readLine() != null) {
                    total++;
                }
            } catch (IOException e) {
                System.err.println("Error counting entries in " + file.getName());
            }
        }
        return total;
    }

    public static List<Integer> getAllChunkIndices() {
        List<Integer> indices = new ArrayList<>();
        File dir = new File(HISTORY_DIR);
        if (!dir.exists()) {
            return indices;
        }

        File[] files = dir.listFiles((d, name) -> name.startsWith(CHUNK_PREFIX) && name.endsWith(CHUNK_SUFFIX));
        if (files == null) {
            return indices;
        }

        for (File file : files) {
            String name = file.getName();
            String indexStr = name.replace(CHUNK_PREFIX, "").replace(CHUNK_SUFFIX, "");
            try {
                indices.add(Integer.parseInt(indexStr));
            } catch (NumberFormatException e) {
                // Ignore invalid chunk names.
            }
        }
        Collections.sort(indices);
        return indices;
    }

    public static void saveHistory(HistoryManager historyManager) {
        ensureDirectory();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_DATA_FILE))) {
            if (historyManager == null) {
                return;
            }
            for (Page page : historyManager.getAllHistory()) {
                writer.write(serializePage(page));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving history: " + e.getMessage());
        }
    }

    public static void clearAllChunks() {
        File dir = new File(HISTORY_DIR);
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
            dir.delete();
        }
        System.out.println("All chunk files deleted.");
    }

    public static List<Page> loadHistory() {
        List<Page> pages = new ArrayList<>();
        File file = new File(HISTORY_DATA_FILE);
        if (!file.exists()) {
            return pages;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Page page = deserializePage(line);
                if (page != null) {
                    pages.add(page);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading history: " + e.getMessage());
        }
        return pages;
    }

    public static void saveBookmarks(BookmarkManager bookmarkManager) {
        ensureDirectory();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKMARKS_DATA_FILE))) {
            if (bookmarkManager == null) {
                return;
            }
            for (BookmarkItem item : bookmarkManager.getSortedBookmarks()) {
                writer.write(serializeBookmark(item));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving bookmarks: " + e.getMessage());
        }
    }

    public static List<BookmarkItem> loadBookmarks() {
        List<BookmarkItem> bookmarks = new ArrayList<>();
        File file = new File(BOOKMARKS_DATA_FILE);
        if (!file.exists()) {
            return bookmarks;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                BookmarkItem item = deserializeBookmark(line);
                if (item != null) {
                    bookmarks.add(item);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading bookmarks: " + e.getMessage());
        }
        return bookmarks;
    }

    private static String serializePage(Page page) {
        if (page == null) {
            return "";
        }
        String timestamp = page.getTimestamp() != null ? page.getTimestamp().toString() : "";
        
        // Dùng StringBuilder nối chuỗi
        StringBuilder sb = new StringBuilder();
        sb.append(page.getUrl()).append("|")
        .append(page.getTitle() != null ? page.getTitle() : "").append("|")
        .append(page.getContent() != null ? page.getContent() : "").append("|")
        .append(timestamp);
        
        return sb.toString();
    }

    private static Page deserializePage(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] parts = line.split("\\|", 4);
        if (parts.length < 4) {
            return null;
        }

        try {
            return new Page(parts[0], parts[1], parts[2], LocalDateTime.parse(parts[3]));
        } catch (Exception e) {
            return null;
        }
    }

    private static String serializeBookmark(BookmarkItem item) {
        if (item == null) {
            return "";
        }
        String savedAt = item.getSavedAt() != null ? item.getSavedAt().toString() : "";
        return item.getUrl() + "|" + (item.getTitle() != null ? item.getTitle() : "") + "|" + savedAt;
    }

    private static BookmarkItem deserializeBookmark(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] parts = line.split("\\|", 3);
        if (parts.length < 3) {
            return null;
        }

        try {
            return new BookmarkItem(parts[0], parts[1], LocalDateTime.parse(parts[2]));
        } catch (Exception e) {
            return null;
        }
    }
}