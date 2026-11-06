package browserSimulator;

import browserSimulator.model.Page;
import browserSimulator.service.HistoryManager;

import java.time.LocalDateTime;

public class DataImporter {

    private static final int TOTAL_RECORDS = 100000;
    private static final String[] DOMAINS = {
        // 100 tên miền (lấy từ danh sách phổ biến)
        "google", "facebook", "amazon", "microsoft", "apple", "netflix", "youtube", "twitter", "instagram",
        "linkedin", "whatsapp", "wechat", "tiktok", "tencent", "alibaba", "baidu", "xiaomi", "huawei", "samsung",
        "sony", "lg", "panasonic", "canon", "nikon", "fujitsu", "toshiba", "sharp", "dell", "hp", "ibm", "oracle",
        "sap", "salesforce", "adobe", "vmware", "cisco", "intel", "amd", "nvidia", "qualcomm", "mediatek",
        "philips", "siemens", "bosch", "schneider", "abb", "honeywell", "emerson", "rockwell", "simatic",
        "tsmc", "umc", "globalfoundries", "stmicro", "nxp", "infineon", "renesas", "analog", "maxim", "microchip",
        "xilinx", "altera", "lattice", "arm", "risc-v", "arduino", "raspberry", "banana", "orange", "pine64",
        "beaglebone", "odroid", "nvidia-jetson", "intel-edison", "mediatek-helio", "samsung-exynos",
        "apple-a", "qualcomm-snapdragon", "huawei-kirin", "xiaomi-surge", "google-tensor", "amd-ryzen",
        "intel-core", "nvidia-geforce", "microsoft-azure", "amazon-web", "google-cloud", "ibm-cloud",
        "oracle-cloud", "salesforce-cloud", "sap-cloud", "adobe-cloud", "vmware-cloud", "cisco-cloud",
        "huawei-cloud", "tencent-cloud", "alibaba-cloud", "baidu-cloud", "xilinx-cloud", "nvidia-cloud",
        "intel-cloud", "amd-cloud", "qualcomm-cloud", "mediatek-cloud", "samsung-cloud", "apple-cloud"
    };

    private static final String[] TLDs = {
        // 100 đuôi mở rộng khác nhau
        ".com", ".org", ".net", ".edu", ".gov", ".mil", ".int", ".io", ".co", ".us",
        ".uk", ".de", ".fr", ".jp", ".cn", ".ru", ".br", ".in", ".au", ".ca",
        ".kr", ".it", ".es", ".mx", ".nl", ".se", ".pl", ".za", ".eg", ".sa",
        ".ae", ".sg", ".my", ".ph", ".vn", ".th", ".id", ".pk", ".bd", ".ng",
        ".ke", ".gh", ".tn", ".ma", ".dz", ".ci", ".cm", ".zm", ".zw", ".na",
        ".info", ".biz", ".name", ".pro", ".mobi", ".asia", ".tv", ".fm", ".am", ".fm",
        ".io", ".sh", ".ac", ".ad", ".ae", ".af", ".ag", ".ai", ".al", ".am",
        ".an", ".ao", ".aq", ".ar", ".as", ".at", ".au", ".aw", ".ax", ".az",
        ".ba", ".bb", ".bd", ".be", ".bf", ".bg", ".bh", ".bi", ".bj", ".bm",
        ".bn", ".bo", ".br", ".bs", ".bt", ".bv", ".bw", ".by", ".bz", ".ca"
    };

    public static void importTestData() {
        System.out.println("Starting data import with 100 domains and 100 TLDs...");
        
        HistoryManager hm = new HistoryManager();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < TOTAL_RECORDS; i++) {
            // Chọn domain và TLD theo chỉ số vòng lặp để đa dạng
            String domain = DOMAINS[i % DOMAINS.length];
            String tld = TLDs[i % TLDs.length];
            // Thêm số vào domain để mỗi URL là duy nhất (tránh trùng lặp)
            String url = "https://" + domain + i + tld + "/page/" + i;
            String title = "Page " + i + " - " + domain + tld;
            // Nội dung giả, có thể thêm từ khóa ngẫu nhiên để tìm kiếm
            String content = "Content of " + domain + " page " + i + " with keyword: " + (i % 100);
            LocalDateTime timestamp = LocalDateTime.now().minusMinutes(i);
            Page page = new Page(url, title, content, timestamp);
            hm.addHistory(page);
        }

        hm.addHistory(null);
        long endTime = System.currentTimeMillis();
        System.out.println("Imported " + TOTAL_RECORDS + " records in " + (endTime - startTime) + " ms");
        System.out.println("Total entries: " + hm.getTotalEntries());
        System.out.println("Chunks created: " + (hm.getCurrentChunkIndex() + 1));
        System.out.println("Files saved in history/ directory.");
    }

    public static void main(String[] args) {
        importTestData();
    }
}