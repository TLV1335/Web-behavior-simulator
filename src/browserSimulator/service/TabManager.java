package browserSimulator.service;

import browserSimulator.model.Tab;

import java.util.*;

public class TabManager {
    private List<Tab> tabs;
    private int activeTabIndex;
    private int nextTabId;
    private HistoryManager historyManager;

    public TabManager() {
        this(null);
    }

    public TabManager(HistoryManager historyManager) {
        this.tabs = new ArrayList<>();
        this.activeTabIndex = -1;
        this.nextTabId = 1;
        this.historyManager = historyManager;
    }

    // ========== Tab Management ==========

    public Tab createTab() {
        Tab newTab = new Tab(nextTabId++, historyManager);
        tabs.add(newTab);
        activeTabIndex = tabs.size() - 1;
        System.out.println("Opened " + newTab);
        return newTab;
    }

    public Tab createTab(String tabName) {
        Tab newTab = new Tab(nextTabId++, tabName, historyManager);
        tabs.add(newTab);
        activeTabIndex = tabs.size() - 1;
        System.out.println("Opened " + newTab);
        return newTab;
    }

    public boolean closeTab(int index) {
        if (index < 0 || index >= tabs.size()) {
            System.out.println("Invalid tab index.");
            return false;
        }

        if (tabs.size() <= 1) {
            System.out.println("Cannot close the last tab.");
            return false;
        }

        tabs.remove(index);
        if (activeTabIndex >= tabs.size()) {
            activeTabIndex = tabs.size() - 1;
        }
        System.out.println("Tab closed. Switched to " + tabs.get(activeTabIndex));
        return true;
    }

    public boolean switchTab(int index) {
        if (index < 0 || index >= tabs.size()) {
            System.out.println("Invalid tab index.");
            return false;
        }
        activeTabIndex = index;
        System.out.println("Switched to " + tabs.get(activeTabIndex));
        return true;
    }

    public Tab getActiveTab() {
        if (activeTabIndex >= 0 && activeTabIndex < tabs.size()) {
            return tabs.get(activeTabIndex);
        }
        return null;
    }

    public Tab getTab(int index) {
        if (index >= 0 && index < tabs.size()) {
            return tabs.get(index);
        }
        return null;
    }

    public List<Tab> getAllTabs() {
        return new ArrayList<>(tabs);
    }

    public int getTabCount() {
        return tabs.size();
    }

    public int getActiveTabIndex() {
        return activeTabIndex;
    }

    // ========== Display ==========

    public void displayTabs() {
        if (tabs.isEmpty()) {
            System.out.println("No tabs open.");
            return;
        }
        System.out.println("=== Tabs ===");
        for (int i = 0; i < tabs.size(); i++) {
            String marker = (i == activeTabIndex) ? "* " : "  ";
            System.out.printf("%s%d. %s%n", marker, i + 1, tabs.get(i));
        }
    }
}
