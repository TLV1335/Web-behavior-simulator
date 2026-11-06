package browserSimulator.service;

import java.util.Stack;

import browserSimulator.model.Page;

public class NavigationManager {
    private Stack<String> backStack;
    private Stack<String> forwardStack;
    private String currentPage;
    private HistoryManager historyManager;

    public NavigationManager() {
        this.backStack = new Stack<>();
        this.forwardStack = new Stack<>();
        this.currentPage = null;
        this.historyManager = new HistoryManager();
    }

    public NavigationManager(HistoryManager historyManager) {
        this();
        this.historyManager = historyManager;
    }

    // ========== Navigation Operations ==========

    public void visitNewPage(String url) {
        if (currentPage != null) {
            backStack.push(currentPage);
        }

        forwardStack.clear();

        currentPage = url;

        Page page = new Page(url, "Page: " + url, "Content of " + url);
        historyManager.addHistory(page);
    }

    public void visitNewPage(Page page) {
        if (currentPage != null) {
            backStack.push(currentPage);
        }

        forwardStack.clear();
        currentPage = page.getUrl();
        historyManager.addHistory(page);
    }

    public boolean goBack() {
        if (backStack.isEmpty()) {
            System.out.println("No previous page to go back to.");
            return false;
        }

        forwardStack.push(currentPage);
        currentPage = backStack.pop();
        System.out.println("Back to: " + currentPage);
        return true;
    }

    public boolean goForward() {
        if (forwardStack.isEmpty()) {
            System.out.println("No next page to go forward to.");
            return false;
        }

        backStack.push(currentPage);
        currentPage = forwardStack.pop();
        System.out.println("Forward to: " + currentPage);
        return true;
    }

    // ========== Getters ==========

    public String getCurrentPage() {
        return currentPage;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public Stack<String> getBackStack() { return backStack; }
    public Stack<String> getForwardStack() { return forwardStack; }

    public boolean canGoBack() { return !backStack.isEmpty(); }
    public boolean canGoForward() { return !forwardStack.isEmpty(); }

    // ========== Display ==========

    public void displayNavigationState() {
        System.out.println("=== Navigation State ===");
        System.out.println("Back Stack: " + backStack);
        System.out.println("Current: " + currentPage);
        System.out.println("Forward Stack: " + forwardStack);
    }
}