package com.example.moneymanagementapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private double amount;
    private String category;
    private String type; // "income" or "expense"
    private String note;
    private long date; // stored as timestamp

    // Constructor, getters and setters
    public Transaction(String title, double amount, String category, String type, String note, long date) {
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.type = type;
        this.note = note;
        this.date = date;
    }

    // Getters and setters for all fields
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
}