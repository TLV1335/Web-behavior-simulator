package browserSimulator.data;

import browserSimulator.model.Page;
import java.util.*;

public class TrieNode {
    private Map<Character, TrieNode> children;
    private Set<Page> entries;
    private boolean isEndOfWord;

    public TrieNode() {
        this.children = new HashMap<>();
        this.entries = new HashSet<>();
        this.isEndOfWord = false;
    }

    public Map<Character, TrieNode> getChildren() { return children; }
    public void setChildren(Map<Character, TrieNode> children) { this.children = children; }
    public List<Page> getEntries() { return new ArrayList<>(entries) ; }
    public void setEntries(Set<Page> entries) { this.entries = entries; }
    public boolean isEndOfWord() { return isEndOfWord; }
    public void setEndOfWord(boolean endOfWord) { isEndOfWord = endOfWord; }

    public TrieNode getChild(char c) {
        return children.get(c);
    }

    public void addChild(char c, TrieNode node) {
        children.put(c, node);
    }

    public void addEntry(Page page) {
            entries.add(page);
    }

    public void removeEntry(Page page) {
        entries.remove(page);
    }
}