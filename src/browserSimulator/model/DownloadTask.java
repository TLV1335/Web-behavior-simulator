package browserSimulator.model;

import java.time.LocalDateTime;

public class DownloadTask {
    private String fileUrl;
    private String destinationPath;
    private String status; // PENDING, DOWNLOADING, COMPLETED, FAILED
    private int progress;  // 0-100
    private LocalDateTime requestedAt;

    public DownloadTask(String fileUrl, String destinationPath) {
        this.fileUrl = fileUrl;
        this.destinationPath = destinationPath;
        this.status = "PENDING";
        this.progress = 0;
        this.requestedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getDestinationPath() { return destinationPath; }
    public void setDestinationPath(String destinationPath) { this.destinationPath = destinationPath; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %d%%", status, fileUrl, progress);
    }
}