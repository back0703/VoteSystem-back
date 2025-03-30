package com.example.votesystem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Vote {
    private String id;
    private String title;
    private List<String> options;
    private Map<String, Integer> results;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Vote(String id, String title, List<String> options, Map<String, Integer> results) {
        this.id = id;
        this.title = title;
        this.options = options;
        this.results = results;
    }

    public Vote(String id, String title, List<String> options, Map<String, Integer> results,
                LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.title = title;
        this.options = options;
        this.results = results;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Map<String, Integer> getResults() {
        return results;
    }

    public void setResults(Map<String, Integer> results) {
        this.results = results;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
