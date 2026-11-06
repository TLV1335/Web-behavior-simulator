package browserSimulator.model;

import browserSimulator.service.HistoryManager;
import browserSimulator.service.NavigationManager;

public class Tab {
    private int tabId;
    private String tabName;
    private NavigationManager navigationManager;

    public Tab(int tabId) {
        this(tabId, "Tab " + tabId, null);
    }

    public Tab(int tabId, String tabName) {
        this(tabId, tabName, null);
    }

    public Tab(int tabId, HistoryManager historyManager) {
        this(tabId, "Tab " + tabId, historyManager);
    }

    public Tab(int tabId, String tabName, HistoryManager historyManager) {
        this.tabId = tabId;
        this.tabName = tabName;
        this.navigationManager = (historyManager == null)
                ? new NavigationManager()
                : new NavigationManager(historyManager);
    }

    // Getters and Setters
    public int getTabId() { return tabId; }
    public void setTabId(int tabId) { this.tabId = tabId; }
    public String getTabName() { return tabName; }
    public void setTabName(String tabName) { this.tabName = tabName; }
    public NavigationManager getNavigationManager() { return navigationManager; }
    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    public String getCurrentPage() {
        return navigationManager.getCurrentPage();
    }

    @Override
    public String toString() {
        return String.format("Tab[%d] - %s - Current: %s",
                tabId, tabName,
                getCurrentPage() != null ? getCurrentPage() : "Empty");
    }
}