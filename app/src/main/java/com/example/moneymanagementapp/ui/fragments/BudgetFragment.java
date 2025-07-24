package com.example.moneymanagementapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneymanagementapp.R;
import com.example.moneymanagementapp.viewmodels.TransactionViewModel;

public class BudgetFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private EditText editTextBudget;
    private TextView textViewBudgetStatus;
    private TransactionViewModel transactionViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("budget_prefs", Context.MODE_PRIVATE);
        editTextBudget = view.findViewById(R.id.edit_text_budget);
        textViewBudgetStatus = view.findViewById(R.id.text_view_budget_status);
        Button buttonSetBudget = view.findViewById(R.id.button_set_budget);

        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        // Load saved budget
        float savedBudget = sharedPreferences.getFloat("monthly_budget", 0);
        if (savedBudget > 0) {
            editTextBudget.setText(String.valueOf(savedBudget));
        }

        buttonSetBudget.setOnClickListener(v -> {
            try {
                float budget = Float.parseFloat(editTextBudget.getText().toString());
                sharedPreferences.edit().putFloat("monthly_budget", budget).apply();
                updateBudgetStatus();
                Toast.makeText(getContext(), "Budget set successfully", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });

        // Update budget status whenever expenses change
        transactionViewModel.getTotalExpense().observe(getViewLifecycleOwner(), totalExpense -> {
            if (totalExpense != null) {
                updateBudgetStatus();
            }
        });

        return view;
    }

    private void updateBudgetStatus() {
        float budget = sharedPreferences.getFloat("monthly_budget", 0);
        Double totalExpense = transactionViewModel.getTotalExpense().getValue();

        if (budget > 0 && totalExpense != null) {
            float remaining = budget - totalExpense.floatValue();
            String status;
            int color;

            if (remaining >= 0) {
                status = String.format("Remaining: ₹%.2f", remaining);
                color = ContextCompat.getColor(requireContext(), R.color.green);
            } else {
                status = String.format("Overspent: ₹%.2f", Math.abs(remaining));
                color = ContextCompat.getColor(requireContext(), R.color.red);
            }

            textViewBudgetStatus.setText(status);
            textViewBudgetStatus.setTextColor(color);
        } else if (budget > 0) {
            textViewBudgetStatus.setText(String.format("Budget: ₹%.2f", budget));
            textViewBudgetStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        } else {
            textViewBudgetStatus.setText("No budget set");
            textViewBudgetStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        }
    }
}