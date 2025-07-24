package com.example.moneymanagementapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.moneymanagementapp.database.TransactionRepository;
import com.example.moneymanagementapp.models.Transaction;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel {
    private TransactionRepository repository;
    private LiveData<List<Transaction>> allTransactions;
    private LiveData<Double> totalIncome;
    private LiveData<Double> totalExpense;
    private LiveData<Double> currentBalance;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        allTransactions = repository.getAllTransactions();
        totalIncome = repository.getTotalIncome();
        totalExpense = repository.getTotalExpense();
        currentBalance = repository.getCurrentBalance();


    }

    public void insert(Transaction transaction) {
        repository.insert(transaction);
    }

    public void update(Transaction transaction) {
        repository.update(transaction);
    }

    public void delete(Transaction transaction) {
        repository.delete(transaction);
    }
    public LiveData<Double> getCurrentBalance() {
        return currentBalance;
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<List<Transaction>> getTransactionsByType(String type) {
        return repository.getTransactionsByType(type);
    }

    public LiveData<Double> getTotalIncome() {
        return totalIncome;
    }

    public LiveData<Double> getTotalExpense() {
        return totalExpense;
    }

    public LiveData<List<Transaction>> getTransactionsByDateRange(long start, long end) {
        return repository.getTransactionsByDateRange(start, end);
    }


}