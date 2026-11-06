package browserSimulator.service;

import browserSimulator.data.TrieNode;
import browserSimulator.model.Page;
import browserSimulator.util.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static final int MAX_HOT_DATA = 10000;
    private boolean chunkingEnabled = true;
    private List<Page> timeline;
    private TrieNode trieRoot;
    private int currentChunkIndex;
    private int totalEntries;

    public HistoryManager() {
        this.timeline = new ArrayList<>();
        this.trieRoot = new TrieNode();
        this.currentChunkIndex = 0;
        this.totalEntries = 0;
        loadLatestChunk();
    }

    // Trong HistoryManager.java
    public HistoryManager(boolean loadChunk) {
        this.timeline = new ArrayList<>();
        this.trieRoot = new TrieNode();
        this.currentChunkIndex = 0;
        this.totalEntries = 0;
        this.chunkingEnabled = true;
        if (loadChunk) {
            loadLatestChunk();
        }
    }

    public void setChunkingEnabled(boolean enabled) {
        this.chunkingEnabled = enabled;
    }
    

    private void loadLatestChunk() {
        int latestChunk = FileHandler.getLatestChunkIndex();
        if (latestChunk >= 0) {
            this.currentChunkIndex = latestChunk;
            List<Page> pages = FileHandler.loadChunk(currentChunkIndex);
            for (Page page : pages) {
                timeline.add(page);
                insertToTrie(page);
            }
            totalEntries = FileHandler.getTotalEntries();
            System.out.println("Loaded chunk " + currentChunkIndex + " with " + pages.size() + " entries.");
        }
    }

    public void addHistory(Page page) {
        if (page == null) return;

        // 1. Nếu đã đạt giới hạn → flush TRƯỚC khi thêm
        if (chunkingEnabled && timeline.size() >= MAX_HOT_DATA) {
            flushToDisk();
        }

        // 2. Sau đó mới thêm trang mới
        timeline.add(page);
        insertToTrie(page);
        totalEntries++;
    }

    public boolean deleteHistory(Page page) {
        if (page == null || page.getUrl() == null) {
            return false;
        }

        // 1. Xóa khỏi timeline
        boolean removed = timeline.remove(page);
        if (!removed) {
            System.out.println("Page not found in history.");
            return false;
        }

        // 2. Xóa khỏi Trie (cần có deleteFromTrie)
        deleteFromTrie(page);
        totalEntries--;

        System.out.println("Deleted from history: " + page.getUrl());
        return true;
    }

    private void deleteFromTrie(Page page) {
        // Gọi removeEntry trên tất cả các node trên đường đi
        // (Không xóa node vật lý, chỉ xóa tham chiếu Page)
        TrieNode current = trieRoot;
        String url = page.getUrl();
        for (char c : url.toCharArray()) {
            TrieNode child = current.getChild(c);
            if (child == null) break;
            child.removeEntry(page);
            current = child;
        }
    }


    private void flushToDisk() {
        FileHandler.saveChunk(currentChunkIndex, timeline);
        timeline.clear();
        trieRoot = new TrieNode();
        currentChunkIndex++;

        System.out.println("Flushed " + MAX_HOT_DATA + " entries to chunk " + (currentChunkIndex - 1));
        System.out.println("New chunk " + currentChunkIndex + " created.");
    }

    public List<Page> searchByPrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty() || trieRoot == null) {
            return new ArrayList<>();
        }

        TrieNode current = trieRoot;
        for (char c : prefix.toCharArray()) {
            TrieNode child = current.getChild(c);
            if (child == null) {
                return new ArrayList<>();
            }
            current = child;
        }
        return getAllEntriesFromNode(current);
    }

    public List<Page> deepSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Page> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (Page page : timeline) {
            if (page != null && page.getUrl() != null && (page.getUrl().toLowerCase().contains(lowerKeyword) ||
                (page.getTitle() != null && page.getTitle().toLowerCase().contains(lowerKeyword)))) {
                results.add(page);
            }
        }

        for (int i = 0; i < currentChunkIndex; i++) {
            List<Page> chunkPages = FileHandler.loadChunk(i);
            for (Page page : chunkPages) {
                if (page != null && page.getUrl() != null && (page.getUrl().toLowerCase().contains(lowerKeyword) ||
                    (page.getTitle() != null && page.getTitle().toLowerCase().contains(lowerKeyword)))) {
                    results.add(page);
                }
            }
        }

        return results;
    }

    public void loadChunkToRam(int chunkIndex) {
        timeline.clear();
        trieRoot = new TrieNode();

        List<Page> pages = FileHandler.loadChunk(chunkIndex);
        for (Page page : pages) {
            timeline.add(page);
            insertToTrie(page);
        }

        currentChunkIndex = chunkIndex;
        System.out.println("Loaded chunk " + chunkIndex + " into RAM.");
    }

    private List<Page> getAllEntriesFromNode(TrieNode node) {
        List<Page> result = new ArrayList<>(node.getEntries());
        for (TrieNode child : node.getChildren().values()) {
            result.addAll(getAllEntriesFromNode(child));
        }
        return result;
    }

    public List<Page> getAllHistory() {
        return new ArrayList<>(timeline);
    }

    public int getHistorySize() {
        return timeline.size();
    }

    public int getTotalEntries() {
        return totalEntries;
    }

    public int getCurrentChunkIndex() {
        return currentChunkIndex;
    }

    public void displayTimeline() {
        if (timeline.isEmpty()) {
            System.out.println("History is empty.");
            return;
        }
        System.out.println("=== Browsing History (Hot Data - " + timeline.size() + " entries) ===");
        for (int i = 0; i < timeline.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, timeline.get(i));
        }
    }

    public void displayChunkInfo() {
        System.out.println("=== Storage Info ===");
        System.out.println("Total entries: " + totalEntries);
        System.out.println("Hot data (RAM): " + timeline.size() + " entries");
        System.out.println("Cold data (Disk): " + (totalEntries - timeline.size()) + " entries");
        System.out.println("Current chunk index: " + currentChunkIndex);
    }

    private void insertToTrie(Page page) {
        if (page == null || page.getUrl() == null) {
            return;
        }

        String url = page.getUrl();
        TrieNode current = trieRoot;
        for (char c : url.toCharArray()) {
            TrieNode child = current.getChild(c);
            if (child == null) {
                child = new TrieNode();
                current.addChild(c, child);
            }
            current = child;
            current.addEntry(page);
        }
        current.setEndOfWord(true);
    }

    //Clear history
    public void clearHistory() {
        timeline.clear();
        trieRoot = new TrieNode();
        totalEntries = 0;
        // Không xóa các file chunk, chỉ xóa RAM và reset chunk index
        // Nếu muốn xóa cả file, gọi cleanupHistoryDir() riêng.
        System.out.println("History cleared from RAM. Chunks on disk remain.");
    }

    public void clearAllChunks (){
        FileHandler.clearAllChunks();
        System.out.println("History were clear on disk");
    }
}