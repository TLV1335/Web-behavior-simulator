package browserSimulator.ui;

import browserSimulator.service.*;
import browserSimulator.util.*;

import java.util.*;

public class BrowserUI {
    private Browser browser;
    private Scanner scanner;

    public BrowserUI() {
        this.browser = new Browser();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== Web Browser Navigation Simulator ===");
        System.out.println("Welcome! Type 'help' for available commands.\n");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                break;
            }

            if (!InputValidator.isValidMenuInput(input)) {
                System.out.println("Invalid command. Type 'help' for available commands.");
                continue;
            }

            String[] parts = input.split("\\s+", 2);
            String command = parts[0].toLowerCase();
            String args = parts.length > 1 ? parts[1] : "";

            switch (command) {
                case "help":
                    showHelp();
                    break;
                case "visit":
                case "v":
                    handleVisit(args);
                    break;
                case "clearhistory":
                case "clearch":
                    browser.clearHistory();
                    break;
                case "back":
                case "b":
                    browser.goBack();
                    break;
                case "forward":
                case "f":
                    browser.goForward();
                    break;
                case "history":
                case "h":
                    browser.showHistory();
                    break;
                case "bookmarks":
                case "bm":
                    browser.showBookmarks();
                    break;
                case "addbookmark":
                case "addbm":
                    handleAddBookmark(args);
                    break;
                case "removebookmark":
                case "rmbm":
                    handleRemoveBookmark(args);
                    break;
                case "search":
                case "s":
                    handleSearch(args);
                    break;
                case "tabs":
                    browser.showTabs();
                    break;
                case "newtab":
                case "nt":
                    browser.createNewTab();
                    break;
                case "switchtab":
                case "st":
                    handleSwitchTab(args);
                    break;
                case "storage":
                case "str":
                    browser.showStorageInfo();
                    break;
                case "closetab":
                case "ct":
                    handleCloseTab(args);
                    break;
                case "download":
                case "dl":
                    handleDownload(args);
                    break;
                case "downloads":
                    browser.showDownloads();
                    break;
                case "processdl":
                    browser.processDownloads();
                    break;
                case "cache":
                    browser.showCache();
                    break;
                case "clearcache":
                    browser.clearCache();
                    break;
                case "save":
                    browser.saveData();
                    break;
                case "load":
                    browser.loadData();
                    break;
                case "status":
                    browser.showStatus();
                    break;
                case "clearall":
                    browser.clearAllHistory();
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
            }
        }

        System.out.println("Thank you for using Web Browser Navigation Simulator!");
        scanner.close();
    }

    // ========== Command Handlers ==========

    private void handleVisit(String args) {
        if (args.isEmpty()) {
            System.out.println("Usage: visit <url>");
            return;
        }
        if (!InputValidator.isValidUrl(args)) {
            System.out.println("Invalid URL format. Please enter a valid URL.");
            return;
        }
        browser.visitPage(args);
    }

    private void handleAddBookmark(String args) {
        if (args.isEmpty()) {
            System.out.println("Usage: addbookmark <url>");
            return;
        }
        browser.addBookmarkByUrl(args);
    }

    private void handleRemoveBookmark(String args) {
        if (args.isEmpty()) {
            System.out.println("Usage: removebookmark <url>");
            return;
        }
        browser.removeBookmarkByUrl(args);
    }

    private void handleSearch(String args) {
        if (args.isEmpty()) {
            System.out.println("Usage: search <keyword>");
            return;
        }
        browser.search(args, this.scanner);
    }

    private void handleSwitchTab(String args) {
        if (args.isEmpty()) {
            System.out.println("Usage: switchtab <index>");
            return;
        }
        try {
            int index = Integer.parseInt(args) - 1; // Convert to 0-based
            browser.switchTab(index);
        } catch (NumberFormatException e) {
            System.out.println("Invalid index. Please enter a number.");
        }
    }

    private void handleCloseTab(String args) {
        if (args.isEmpty()) {
            System.out.println("Usage: closetab <index>");
            return;
        }
        try {
            int index = Integer.parseInt(args) - 1;
            browser.closeTab(index);
        } catch (NumberFormatException e) {
            System.out.println("Invalid index. Please enter a number.");
        }
    }

    private void handleDownload(String args) {
        if (args.isEmpty()) {
            System.out.println("Usage: download <url>");
            return;
        }
        if (!InputValidator.isValidUrl(args)) {
            System.out.println("Invalid URL format.");
            return;
        }
        browser.downloadFile(args);
    }

    // ========== Help Menu ==========

    private void showHelp() {
        System.out.println(
            "=== Available Commands ===\n" +
            "help, h                    - Show this help menu\n" +
            "visit <url>, v <url>       - Visit a web page\n" +
            "clear History, clearhch    - Clear history on RAM\n" +
            "back, b                    - Go back one page\n" +
            "forward, f                 - Go forward one page\n" +
            "history, h                 - Show browsing history\n" +
            "bookmarks, bm              - Show bookmarks\n" +
            "addbookmark <url>, addbm   - Add page to bookmarks\n" +
            "removebookmark <url>, rmbm - Remove page from bookmarks\n" +
            "search <keyword>, s        - Search history and bookmarks\n" +
            "tabs, t                    - Show all open tabs\n" +
            "newtab, nt                 - Open a new tab\n" +
            "switchtab <index>, st      - Switch to tab by index\n" +
            "closetab <index>, ct       - Close tab by index\n" +
            "download <url>, dl         - Download a file\n" +
            "downloads                  - Show download queue\n" +
            "processdl                  - Process all downloads\n" +
            "cache                      - Show cache status\n" +
            "clearcache                 - Clear cache\n" +
            "save                       - Save data to file\n" +
            "load                       - Load data from file\n" +
            "status                     - Show system status\n" +
            "clearall                   - Clear all history on disk\n"+
            "exit, quit                 - Exit the program"
        );
    }
}