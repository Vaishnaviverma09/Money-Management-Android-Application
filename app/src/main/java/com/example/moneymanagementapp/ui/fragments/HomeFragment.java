package com.example.moneymanagementapp.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneymanagementapp.R;
import com.example.moneymanagementapp.viewmodels.TransactionViewModel;

public class HomeFragment extends Fragment {
    private TransactionViewModel transactionViewModel;
    private TextView textViewBalance, textViewIncome, textViewExpense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        textViewBalance = view.findViewById(R.id.text_view_balance);
        textViewIncome = view.findViewById(R.id.text_view_income);
        textViewExpense = view.findViewById(R.id.text_view_expense);

        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        transactionViewModel.getCurrentBalance().observe(getViewLifecycleOwner(), balance -> {
            textViewBalance.setText(String.format("₹%.2f", balance));

            // Set color based on balance
            if (balance < 0) {
                textViewBalance.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
            } else {
                textViewBalance.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));
            }
        });

        // Observe total income
        transactionViewModel.getTotalIncome().observe(getViewLifecycleOwner(), totalIncome -> {
            if (totalIncome != null) {
                textViewIncome.setText(String.format("₹%.2f", totalIncome));
                updateBalance();
            }
        });

        // Observe total expense
        transactionViewModel.getTotalExpense().observe(getViewLifecycleOwner(), totalExpense -> {
            if (totalExpense != null) {
                textViewExpense.setText(String.format("₹%.2f", totalExpense));
                updateBalance();
            }
        });

        return view;
    }

    private void updateBalance() {
        Double income = transactionViewModel.getTotalIncome().getValue();
        Double expense = transactionViewModel.getTotalExpense().getValue();

        if (income != null && expense != null) {
            double balance = income - expense;
            textViewBalance.setText(String.format("₹%.2f", balance));

            // Change color based on balance
            if (balance < 0) {
                textViewBalance.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
            } else {
                textViewBalance.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));
            }
        }
    }
}