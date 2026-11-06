# Web Browser Navigation Simulator

[![Java](https://img.shields.io/badge/Language-Java%208%2B-orange.svg)](https://www.oracle.com/java/)
[![Course](https://img.shields.io/badge/Course-CSD201%20DSA-blue.svg)](#)
[![Architecture](https://img.shields.io/badge/Architecture-Facade%20Pattern-green.svg)](#)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](#)

A high-performance command-line web browser simulator engineered in **Java** to demonstrate the practical application of fundamental **Data Structures and Algorithms (DSA)**.

This project simulates core browser capabilities—including multi-tab navigation, back/forward history traversal, real-time URL autocompletion via Prefix Trees (Trie), LRU page caching, asynchronous download task scheduling, and high-scale tiered storage (Hot/Cold data chunking with Lazy Loading) capable of handling **100,000+ browsing history records** with near-constant lookup times.

---

## Table of Contents

- [Key Features](#key-features)
- [System Architecture](#system-architecture)
- [Data Structures & Algorithms Matrix](#data-structures--algorithms-matrix)
- [Tiered Storage & High-Scale Memory Optimization](#tiered-storage--high-scale-memory-optimization)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Compilation](#compilation)
  - [Running the Browser](#running-the-browser)
  - [Generating Benchmark Data](#generating-benchmark-data)
  - [Running Performance Benchmarks](#running-performance-benchmarks)
- [CLI Command Reference](#cli-command-reference)
- [Benchmark Results](#benchmark-results)
- [Author Roles & Contributions](#author-roles--contributions)
- [License](#license)

---

## Key Features

- **Multi-Tab Management:** Independent tab isolation with dynamic creation, switching, and deletion. Each tab maintains its own isolated navigation state.
- **Back & Forward Navigation:** Dual-stack navigation model replicating standard browser history buttons with $O(1)$ transitions.
- **Prefix-Based URL Autocomplete:** In-memory **Trie** structure enabling instantaneous $O(L)$ URL prefix searches independent of total history scale $N$.
- **Tiered Storage Architecture:** Automatic threshold-based disk chunking that separates "Hot Data" (RAM) from "Cold Data" (Disk), mitigating Java heap `OutOfMemoryError` under heavy loads.
- **LRU Page Cache:** Memory-bounded cache (maximum 100 pages) powered by `LinkedHashMap` with Least Recently Used eviction.
- **Bookmark Management:** Set-based $O(1)$ duplicate prevention combined with a sorted index supporting $O(\log N)$ Binary Search.
- **Download Queue Scheduler:** FIFO queue (`LinkedList`) managing background download tasks with progress simulation.
- **Custom Sorting & Search Engine:** Hand-crafted **MergeSort** algorithm for stable chronological sorting ($O(N \log N)$), paired with two-phase fallback search (Hot RAM $\rightarrow$ Cold Disk).

---

## System Architecture

The project adheres to a clean, modular **MVC-like layered architecture** with the **Facade Pattern** implemented by `Browser.java` as the unified orchestration point:

```
+-------------------------------------------------------------+
|                      User Interface                         |
|                    (BrowserUI - REPL)                       |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                   Browser Controller (Facade)               |
+-------------------------------------------------------------+
    |           |            |           |           |
    v           v            v           v           v
+-------+   +-------+    +-------+   +-------+   +-------+
|  Tab  |   | Cache |    |History|   |Bookmark|  |Download|
|Manager|   |Manager|    |Manager|   |Manager|   |Manager|
+-------+   +-------+    +-------+   +-------+   +-------+
    |           |            |           |           |
    v           v            v           v           v
[NavManager] [LRU Cache]   [ Trie ]   [HashSet]  [FIFO Queue]
[2x Stacks ]             [Chunking]  [BinarySrch]
```

---

## Data Structures & Algorithms Matrix

| Browser Feature | Data Structure | Algorithm / Mechanism | Time Complexity | Class Responsible |
| :--- | :--- | :--- | :--- | :--- |
| **Back / Forward History** | Dual `Stack<String>` | LIFO Push / Pop | $O(1)$ | `NavigationManager` |
| **Download Queue** | `Queue<DownloadTask>` (`LinkedList`) | FIFO Enqueue / Dequeue | $O(1)$ | `DownloadManager` |
| **URL Autocomplete** | Prefix Tree (`TrieNode`) | Trie Traversal | $O(L)$ ($L$ = prefix length) | `HistoryManager`, `TrieNode` |
| **Page Caching** | `LinkedHashMap<String, Page>` | LRU Cache Eviction | $O(1)$ | `CacheManager` |
| **Bookmark De-duplication**| `HashSet<BookmarkItem>` | Hashing | $O(1)$ average | `BookmarkManager` |
| **Exact Bookmark Lookup** | Sorted `ArrayList<BookmarkItem>` | Binary Search | $O(\log N)$ | `BookmarkManager` |
| **Chronological Sort** | Array / List | MergeSort (Divide & Conquer) | $O(N \log N)$ | `SearchEngine` |
| **Tab Isolation** | `List<Tab>` | Index-based switching | $O(1)$ lookup, $O(T)$ close | `TabManager` |
| **URL Validation** | String Pattern | Regular Expression (Regex) | $O(M)$ | `InputValidator` |

---

## Tiered Storage & High-Scale Memory Optimization

To simulate real-world production environments where history sizes can exceed hundreds of thousands of entries:

```
[ New Page Visited ]
         |
         v
+------------------------------------+
|  RAM (Hot Data - Max 10,000 items) | <---> [ Fast Trie Prefix Search O(L) ]
+------------------------------------+
         |
    (If RAM >= 10,000: Flush to Disk)
         v
+-----------------------------------------------------------------+
|  Disk (Cold Storage - history/history_chunk_X.txt)             |
|  Chunk 0 (10k) | Chunk 1 (10k) | Chunk 2 (10k) | ... (100k+)    |
+-----------------------------------------------------------------+
         |
    (Deep Search / Lazy Load when requested)
```

1. **Hot Data (RAM):** Retains the latest **10,000 entries** in memory backed by the `Trie` for sub-millisecond autocompletion and search.
2. **Cold Storage (Disk):** Once capacity is reached, data is serialized into chunk files (`history/history_chunk_X.txt`), clearing RAM for subsequent sessions.
3. **Lazy Loading & Deep Search:** Users can trigger deep disk scans across all archive chunks on demand, reducing memory consumption by over **85%**.

---

## Project Structure

```
.
├── .gitignore
├── README.md
└── src/
    └── browserSimulator/
        ├── Main.java                   # Application entry point
        ├── DataImporter.java           # Test data generator (100k synthetic records)
        ├── data/
        │   ├── HistoryEntry.java       # History record model with timestamp
        │   └── TrieNode.java           # Trie node with entries for fast prefix lookups
        ├── experiment/
        │   ├── PerformanceExperiment.java   # Benchmark: Trie vs Linear vs Binary vs Hash
        │   └── PerformanceLazyLoading.java  # Benchmark: Chunk load, deep search & memory
        ├── model/
        │   ├── BookmarkItem.java       # Bookmark entity
        │   ├── DownloadTask.java       # Download task with status and progress
        │   ├── Page.java               # Page entity (URL, title, content, timestamp)
        │   └── Tab.java                # Tab entity with isolated NavigationManager
        ├── service/
        │   ├── BookmarkManager.java    # Set & sorted list bookmark management
        │   ├── Browser.java            # Main Facade controller
        │   ├── CacheManager.java       # LRU page cache
        │   ├── DownloadManager.java    # FIFO download queue processor
        │   ├── HistoryManager.java     # Hot/Cold tiered history with Trie indexing
        │   ├── NavigationManager.java  # Dual-stack back/forward navigation engine
        │   ├── SearchEngine.java       # MergeSort & multi-source search engine
        │   └── TabManager.java         # Multi-tab lifecycle manager
        ├── ui/
        │   └── BrowserUI.java          # CLI Command-Line Interface (REPL)
        └── util/
            ├── FileHandler.java        # Serialization, chunk persistence & loading
            └── InputValidator.java     # Regex URL validation and normalization
```

---

## Getting Started

### Prerequisites

- **Java Development Kit (JDK):** Version 8 or higher (JDK 11, 17, or 21 recommended).
- **Git**

### Compilation

Compile the entire project from the root repository directory into an output directory `bin/`:

```bash
javac -d bin src/browserSimulator/Main.java src/browserSimulator/DataImporter.java src/browserSimulator/experiment/*.java
```

### Running the Browser

Run the CLI interactive browser simulator:

```bash
java -cp bin browserSimulator.Main
```

### Generating Benchmark Data

To populate the history archive with 100,000 realistic records across 100 domains and 100 TLDs:

```bash
java -cp bin browserSimulator.DataImporter
```

### Running Performance Benchmarks

To run the algorithmic search benchmark (Trie vs Linear Search vs Binary Search vs HashSet):

```bash
java -cp bin browserSimulator.experiment.PerformanceExperiment
```

To run the lazy loading, chunk I/O, and memory footprint benchmark:

```bash
java -cp bin browserSimulator.experiment.PerformanceLazyLoading
```

---

## CLI Command Reference

| Command | Shortcut | Description | Example |
| :--- | :--- | :--- | :--- |
| `help` | `h` | Display available commands | `help` |
| `visit <url>` | `v <url>` | Navigate to a web URL in the active tab | `v https://google.com` |
| `back` | `b` | Navigate backward one page | `b` |
| `forward` | `f` | Navigate forward one page | `f` |
| `history` | `h` | Display in-memory browsing history | `history` |
| `clearhistory`| `clearch`| Clear history from RAM (leaves disk intact)| `clearch` |
| `clearall` | — | Delete all history chunks from disk | `clearall` |
| `storage` | `str` | View storage stats (Hot RAM vs Cold Disk) | `storage` |
| `search <kw>` | `s <kw>` | Search history & bookmarks (prompts deep) | `s github` |
| `tabs` | `t` | List all open tabs and active indicator | `tabs` |
| `newtab` | `nt` | Open a new tab | `nt` |
| `switchtab <i>`| `st <i>` | Switch to tab index (1-based) | `st 2` |
| `closetab <i>`| `ct <i>` | Close tab index (1-based) | `ct 1` |
| `bookmarks` | `bm` | Display all saved bookmarks | `bookmarks` |
| `addbookmark <url>` | `addbm` | Add current or specified URL to bookmarks | `addbm github.com` |
| `removebookmark <url>`| `rmbm` | Remove a URL from bookmarks | `rmbm github.com` |
| `download <url>` | `dl <url>`| Queue a file download task | `dl https://example.com/file.zip` |
| `downloads` | — | View the pending download queue | `downloads` |
| `processdl` | — | Process all downloads in queue (FIFO) | `processdl` |
| `cache` | — | View LRU cache occupancy | `cache` |
| `clearcache` | — | Invalidate and purge cache | `clearcache` |
| `status` | — | Display comprehensive browser status | `status` |
| `save` | — | Manually save history & bookmarks to file | `save` |
| `load` | — | Reload saved data from disk | `load` |
| `exit` | `quit` | Exit the simulator | `exit` |

---

## Benchmark Results

*Tested with worst-case search scenarios (non-existent keys and moderate prefix length) on 100,000 records:*

| Dataset Size ($N$) | Trie Prefix Search ($O(L)$) | Linear Search ($O(N)$) | Binary Search ($O(\log N)$) | HashSet Lookup ($O(1)$) | Trie vs Linear Speedup |
| :---: | :---: | :---: | :---: | :---: | :---: |
| **1,000** | ~1,200 ns | ~28,000 ns | ~400 ns | ~80 ns | **~23x** |
| **5,000** | ~1,250 ns | ~140,000 ns | ~520 ns | ~85 ns | **~112x** |
| **10,000** | ~1,300 ns | ~295,000 ns | ~580 ns | ~90 ns | **~226x** |
| **50,000** | ~1,320 ns | ~1,550,000 ns | ~690 ns | ~95 ns | **~1,174x** |
| **100,000** | ~1,350 ns | ~3,200,000 ns | ~750 ns | ~100 ns | **~2,370x** |

> **Key Takeaway:** While Linear Search scales linearly with dataset size ($O(N)$), Trie search latency remains bounded by the prefix length $L$, delivering over **2,000x speedup** on large datasets.

---

## Author Roles & Contributions

This project was developed as a major coursework project for **CSD201 (Data Structures and Algorithms)** at **FPT University**.

### Lead Software Architect & Core Developer
**Lương Văn Thắng ([@TLV1335](https://github.com/TLV1335))**
- **Sole Codebase Author & Implementer:** Solely developed and implemented 100% of the Java source code across all data structures, domain models, core services, benchmarks, and the interactive CLI interface.
- **System Architecture & Design:** Analyzed domain requirements, designed the overall layered architecture (MVC-like + Facade Pattern), decoupled functional managers (Tab, Navigation, History, Cache, Bookmark, Download, Search), and ensured modular separation of concerns.
- **Algorithm & Data Structure Proposals:** Formulated and selected key data structures and algorithmic paradigms to solve browser performance bottlenecks:
  - **Prefix Tree (Trie):** Proposed and implemented the Trie structure with entry sets at each node for $O(L)$ instantaneous URL autocomplete.
  - **Dual-Stack Navigation:** Proposed and engineered the Two-Stack model (`backStack` and `forwardStack`) for $O(1)$ page traversal.
  - **Tiered Memory Management (Chunking & Lazy Loading):** Architected the Hot (RAM) vs. Cold (Disk) tiered storage system to eliminate Java heap `OutOfMemoryError` when scaling to 100,000+ history entries.
  - **LRU Page Cache:** Designed the memory-bounded LRU eviction mechanism utilizing `LinkedHashMap`.
  - **Binary Search & Custom MergeSort:** Implemented binary search for exact bookmark lookups and engineered a standalone MergeSort algorithm for stable chronological sorting ($O(N \log N)$).
- **Benchmarking & Experimental Validation:** Designed and executed the performance test suites (`PerformanceExperiment` and `PerformanceLazyLoading`) to empirically benchmark time complexity and memory footprints under worst-case scenarios.

### Coursework Team Members
The following members contributed to coursework documentation, research questions, theoretical report compilation (Reports 1–4, Mini-Report), and presentations:
- **Trần Bùi Đạt Thành**
- **Nguyễn Thị Hồng Anh**
- **Mai Quang Minh**
- **Mai Xuân Sinh**
- **Mai Đình Quân**

**Course Instructor:** PhongVT12

---

## License

This project is licensed under the [MIT License](LICENSE) - free for academic and educational use.

