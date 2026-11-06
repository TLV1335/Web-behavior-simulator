package browserSimulator.experiment;

import browserSimulator.model.Page;
import browserSimulator.service.HistoryManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Performance Experiment – Sửa lỗi chunking & đo đúng worst-case
 * 
 * Các thay đổi:
 * 1. Tắt chunking TRƯỚC khi insert dữ liệu (dùng constructor mới trong HistoryManager)
 * 2. Không load chunk cũ từ disk
 * 3. Chọn prefix test vừa phải (không quá nhiều kết quả)
 * 4. Warmup đúng cách
 */
public class PerformanceExperiment {

    private static final int[] DATASET_SIZES = {1000, 5000, 10000, 50000, 100000};
    private static final int TRIALS = 100;
    private static final int WARMUP_TRIALS = 200;

    private static List<Page> allPages;
    private static List<String> allUrls;
    private static String testPrefix;
    private static String nonexistentUrl;
    private static String nonexistentKeyword;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  PERFORMANCE EXPERIMENT REPORT");
        System.out.println("  Data Structures & Algorithms - SU2026");
        System.out.println("  (Fixed Chunking – Clean Data)");
        System.out.println("========================================\n");

        // 1. Tạo dữ liệu mẫu
        allPages = generateTestData(100000);
        allUrls = allPages.stream().map(Page::getUrl).collect(Collectors.toList());

        // 2. Chọn prefix test (vừa phải: khoảng 20-50 URL khớp)
        //    Lấy prefix của URL ở vị trí 5000 (không quá phổ biến)
        testPrefix = allUrls.get(5000).substring(0, Math.min(15, allUrls.get(5000).length()));
        // Đếm số URL khớp để thông báo
        long matchCount = allUrls.stream().filter(u -> u.startsWith(testPrefix)).count();
        System.out.println("  Test prefix: " + testPrefix + " (matches ~" + matchCount + " URLs)");

        nonexistentUrl = "https://nonexistent.url/page/999999";
        nonexistentKeyword = "nonexistentkeyword";
        System.out.println("  Non-existent URL/keyword for worst-case tests.\n");

        // 3. Chạy thí nghiệm với từng kích thước
        for (int size : DATASET_SIZES) {
            System.out.println("\n=== DATASET SIZE: " + size + " records ===");
            runSearchExperiment(size);
        }

        System.out.println("\n========================================");
        System.out.println("  EXPERIMENT COMPLETED");
        System.out.println("========================================");
    }

    // ========== Tạo dữ liệu mẫu ==========
    private static List<Page> generateTestData(int count) {
        List<Page> pages = new ArrayList<>();
        String[] domains = {"google", "github", "youtube", "facebook", "amazon",
                "stackoverflow", "wikipedia", "twitter", "instagram", "linkedin",
                "netflix", "spotify", "microsoft", "apple", "oracle",
                "java", "python", "cplusplus", "javascript", "typescript"};

        for (int i = 0; i < count; i++) {
            String domain = domains[i % domains.length];
            String url = "https://" + domain + i + ".com/page/" + i;
            String title = "Page " + i + " - " + domain;
            String content = "Content of " + domain + " page " + i;
            LocalDateTime timestamp = LocalDateTime.now().minusMinutes(i);
            pages.add(new Page(url, title, content, timestamp));
        }
        return pages;
    }

    // ========== Chạy thí nghiệm ==========
    private static void runSearchExperiment(int size) {
        List<Page> data = allPages.subList(0, size);
        List<String> urls = allUrls.subList(0, size);
        HistoryManager hm = new HistoryManager(false);
        hm.setChunkingEnabled(false);

        for (Page page : data) {
            hm.addHistory(page);
        }

        // --- HashSet ---
        HashSet<String> hashSet = new HashSet<>(urls);

        // --- Sorted List cho Binary Search ---
        List<Page> sortedList = new ArrayList<>(data);
        sortedList.sort(Comparator.comparing(Page::getUrl, String.CASE_INSENSITIVE_ORDER));

        // --- Đo thời gian ---
        long trieTime = testTrieSearch(hm);
        long linearTime = testLinearSearch(urls);
        long binaryTime = testBinarySearch(sortedList);
        long hashTime = testHashSetLookup(hashSet);

        printSearchSummary(size, trieTime, linearTime, binaryTime, hashTime);
    }

    // ========== Trie Search (prefix vừa phải) ==========
    private static long testTrieSearch(HistoryManager hm) {
        // Warmup
        for (int i = 0; i < WARMUP_TRIALS; i++) {
            hm.searchByPrefix(testPrefix);
        }
        long total = 0;
        for (int i = 0; i < TRIALS; i++) {
            long start = System.nanoTime();
            hm.searchByPrefix(testPrefix);
            long end = System.nanoTime();
            total += (end - start);
        }
        return total / TRIALS;
    }

    // ========== Linear Search (keyword không tồn tại) ==========
    private static long testLinearSearch(List<String> urls) {
        // Warmup
        for (int i = 0; i < WARMUP_TRIALS; i++) {
            for (String url : urls) {
                if (url.toLowerCase().contains(nonexistentKeyword)) break;
            }
        }
        long total = 0;
        for (int i = 0; i < TRIALS; i++) {
            long start = System.nanoTime();
            for (String url : urls) {
                if (url.toLowerCase().contains(nonexistentKeyword)) break;
            }
            long end = System.nanoTime();
            total += (end - start);
        }
        return total / TRIALS;
    }

    // ========== Binary Search (URL không tồn tại) ==========
    private static long testBinarySearch(List<Page> sortedList) {
        // Warmup
        for (int i = 0; i < WARMUP_TRIALS; i++) {
            binarySearch(sortedList, nonexistentUrl);
        }
        long total = 0;
        for (int i = 0; i < TRIALS; i++) {
            long start = System.nanoTime();
            binarySearch(sortedList, nonexistentUrl);
            long end = System.nanoTime();
            total += (end - start);
        }
        return total / TRIALS;
    }

    private static Page binarySearch(List<Page> list, String target) {
        int low = 0, high = list.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = list.get(mid).getUrl().compareToIgnoreCase(target);
            if (cmp == 0) return list.get(mid);
            if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return null;
    }

    // ========== HashSet Lookup (URL không tồn tại) ==========
    private static long testHashSetLookup(HashSet<String> hashSet) {
        // Warmup
        for (int i = 0; i < WARMUP_TRIALS; i++) {
            hashSet.contains(nonexistentUrl);
        }
        long total = 0;
        for (int i = 0; i < TRIALS; i++) {
            long start = System.nanoTime();
            hashSet.contains(nonexistentUrl);
            long end = System.nanoTime();
            total += (end - start);
        }
        return total / TRIALS;
    }

    // ========== In kết quả ==========
    private static void printSearchSummary(int size, long trie, long linear, long binary, long hash) {
        System.out.println("--- Results for " + size + " records ---");
        System.out.printf("  Trie Prefix Search  : %10d ns%n", trie);
        System.out.printf("  Linear Search       : %10d ns%n", linear);
        System.out.printf("  Binary Search (URL) : %10d ns%n", binary);
        System.out.printf("  HashSet Lookup      : %10d ns%n", hash);

        if (linear > 0) {
            System.out.printf("  (Trie speedup: %.2f x vs Linear)%n", (double) linear / trie);
            System.out.printf("  (Hash speedup: %.2f x vs Linear)%n", (double) linear / hash);
        }
        System.out.println("  (All searches are worst-case: nonexistent keys or moderate prefix)");
    }
}