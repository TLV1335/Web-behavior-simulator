package browserSimulator.service;

import browserSimulator.model.DownloadTask;

import java.util.LinkedList;
import java.util.Queue;

public class DownloadManager {
    private final Queue<DownloadTask> downloadQueue;

    public DownloadManager() {
        this.downloadQueue = new LinkedList<>();
    }

    // ========== Download Operations ==========

    public void enqueueDownload(DownloadTask task) {
        if (task == null) {
            return;
        }
        downloadQueue.offer(task);
        System.out.println("Download added to queue. Position: " + downloadQueue.size());
        System.out.println("  File: " + task.getFileUrl());
    }

    public boolean processNextDownload() {
        if (downloadQueue.isEmpty()) {
            System.out.println("No downloads in queue.");
            return false;
        }

        DownloadTask task = downloadQueue.poll();
        if (task == null) {
            System.out.println("No downloads in queue.");
            return false;
        }
        System.out.println("Processing download: " + task.getFileUrl());

        // Simulate download
        task.setStatus("DOWNLOADING");
        task.setProgress(0);

        // Simulate progress (would be multithreaded in real app)
        for (int i = 0; i <= 100; i += 20) {
            task.setProgress(i);
            System.out.println("  Progress: " + i + "%");
            try {
                Thread.sleep(200); // Simulate time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        task.setStatus("COMPLETED");
        task.setProgress(100);
        System.out.println("Download completed: " + task.getFileUrl());
        return true;
    }

    public void processAllDownloads() {
        if (downloadQueue.isEmpty()) {
            System.out.println("No downloads in queue.");
            return;
        }

        System.out.println("Processing " + downloadQueue.size() + " downloads...");
        while (!downloadQueue.isEmpty()) {
            processNextDownload();
        }
    }

    public void displayQueueStatus() {
        if (downloadQueue.isEmpty()) {
            System.out.println("Download queue is empty.");
            return;
        }

        System.out.println("=== Download Queue ===");
        System.out.println("Pending downloads: " + downloadQueue.size());
        int i = 1;
        for (DownloadTask task : downloadQueue) {
            System.out.printf("%d. %s - %s%n", i++, task.getStatus(), task.getFileUrl());
        }
    }

    public int getQueueSize() {
        return downloadQueue.size();
    }

    public boolean isQueueEmpty() {
        return downloadQueue.isEmpty();
    }

    public void clearQueue() {
        downloadQueue.clear();
        System.out.println("Download queue cleared.");
    }
}
