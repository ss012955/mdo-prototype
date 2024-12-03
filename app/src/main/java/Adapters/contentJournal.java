package Adapters;

import java.util.ArrayList;
import java.util.List;

import HelperClasses.HistoryItem;
import HelperClasses.Note;

public class contentJournal {
    private String title;
    private String description;
    private String type;  // Can be "notes" or "history"
    private List<Note> notesList;  // List to store notes
    private List<HistoryItem> historyList;  // List to store history items

    // Constructor
    public contentJournal(String title, String description, String type, List<Note> notesList, List<HistoryItem> historyList) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.notesList = notesList;
        this.historyList = historyList;
    }

    public contentJournal(String title, String description, String type) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.notesList = notesList != null ? notesList : new ArrayList<>();  // Default to empty list if null
        this.historyList = historyList != null ? historyList : new ArrayList<>();  // Initialize with an empty list for history
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public List<Note> getNotesList() {
        return notesList;
    }

    public List<HistoryItem> getHistoryList() {
        return historyList;
    }

    // You can add setters if needed
}