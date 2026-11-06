package browserSimulator.service;

import browserSimulator.model.Page;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class CacheManager {
    private static final int MAX_CACHE_SIZE = 100;
    private final Map<String, Page> cache;

    public CacheManager() {
        this.cache = new LinkedHashMap<>();
    }

    // ========== Cache Operations ==========

    public Page getFromCache(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }

        if (cache.containsKey(url)) {
            System.out.println("Cache hit: " + url);
            return cache.get(url);
        }
        System.out.println("Cache miss: " + url);
        return null;
    }

    public void addToCache(Page page) {
        if (page == null || page.getUrl() == null || page.getUrl().trim().isEmpty()) {
            return;
        }

        if (cache.size() >= MAX_CACHE_SIZE) {
            evictLeastRecentlyUsed();
        }

        cache.put(page.getUrl(), page);
        System.out.println("Added to cache: " + page.getUrl());
    }

    public void evictLeastRecentlyUsed() {
        if (cache.isEmpty()) return;

        String keyToRemove = cache.keySet().iterator().next();
        cache.remove(keyToRemove);
        System.out.println("Evicted from cache: " + keyToRemove);
    }

    public void clearCache() {
        cache.clear();
        System.out.println("Cache cleared.");
    }

    public int getCacheSize() {
        return cache.size();
    }

    public boolean isCached(String url) {
        return cache.containsKey(url);
    }

    public Set<String> getCachedUrls() {
        return new HashSet<>(cache.keySet());
    }

    // ========== Display ==========

    public void displayCacheStatus() {
        System.out.println("=== Cache Status ===");
        System.out.println("Size: " + cache.size() + "/" + MAX_CACHE_SIZE);
        if (!cache.isEmpty()) {
            System.out.println("Cached URLs:");
            for (String url : cache.keySet()) {
                System.out.println("  - " + url);
            }
        }
    }
}
