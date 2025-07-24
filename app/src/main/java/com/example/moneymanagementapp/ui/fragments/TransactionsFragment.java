package com.example.moneymanagementapp.ui.fragments;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.Observer;

import com.example.moneymanagementapp.R;
import com.example.moneymanagementapp.adapters.TransactionAdapter;
import com.example.moneymanagementapp.models.Transaction;
import com.example.moneymanagementapp.ui.AddEditTransactionActivity;
import com.example.moneymanagementapp.viewmodels.TransactionViewModel;

import java.util.Calendar;
import java.util.List;

public class TransactionsFragment extends Fragment {
    RecyclerView recyclerView;
    private TransactionViewModel transactionViewModel;
    private TransactionAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        setupRecyclerView(view);
        setupSwipeActions();

        // Setup filter options
        Spinner filterSpinner = view.findViewById(R.id.filter_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.filter_options,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                applyFilter(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Setup date range picker
        Button dateRangeButton = view.findViewById(R.id.button_date_range);
        dateRangeButton.setOnClickListener(v -> showDateRangeDialog());

        return view;
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new TransactionAdapter();
        recyclerView.setAdapter(adapter);

        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);
        transactionViewModel.getAllTransactions().observe(getViewLifecycleOwner(), new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {
                adapter.setTransactions(transactions);
                // Force balance update by triggering a fake change

            }
        });
    }

    private void setupSwipeActions() {
        ItemTouchHelper.SimpleCallback touchCallback = new ItemTouchHelper.SimpleCallback(
                0, // drag directions (0 means disabled)
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT // swipe directions
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false; // drag disabled
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Transaction transaction = adapter.getTransactionAt(position);

                if (direction == ItemTouchHelper.LEFT) {
                    // Swipe LEFT to delete
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Delete Transaction")
                            .setMessage("Delete this transaction?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                transactionViewModel.delete(transaction);
                                Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                adapter.notifyItemChanged(position); // Reset swiped item
                            })
                            .show();
                } else {
                    // Swipe RIGHT to edit
                    Intent intent = new Intent(getActivity(), AddEditTransactionActivity.class);
                    intent.putExtra(AddEditTransactionActivity.EXTRA_ID, transaction.getId());
                    startActivity(intent);
                    adapter.notifyItemChanged(position); // Reset swiped item
                }
            }

            // Optional: Change swipe background color
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;
                Paint paint = new Paint();

                // Swipe Right (Edit)
                if (dX > 0) {
                    paint.setColor(ContextCompat.getColor(requireContext(), R.color.swipe_edit));
                    c.drawRect(itemView.getLeft(), itemView.getTop(),
                            dX, itemView.getBottom(), paint);

                    // Draw edit icon
                    Drawable editIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit);
                    int iconMargin = (itemView.getHeight() - editIcon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconBottom = iconTop + editIcon.getIntrinsicHeight();
                    int iconLeft = itemView.getLeft() + iconMargin;
                    int iconRight = iconLeft + editIcon.getIntrinsicWidth();
                    editIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    editIcon.draw(c);
                }
                // Swipe Left (Delete)
                else if (dX < 0) {
                    paint.setColor(ContextCompat.getColor(requireContext(), R.color.swipe_delete));
                    c.drawRect(itemView.getRight() + dX, itemView.getTop(),
                            itemView.getRight(), itemView.getBottom(), paint);

                    // Draw delete icon
                    Drawable deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete);
                    int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                    int iconRight = itemView.getRight() - iconMargin;
                    int iconLeft = iconRight - deleteIcon.getIntrinsicWidth();
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    deleteIcon.draw(c);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.5f; // Swipe halfway to trigger action
            }
        };

        new ItemTouchHelper(touchCallback).attachToRecyclerView(recyclerView);
    }

    private void applyFilter(String filter) {
        switch (filter) {
            case "All":
                transactionViewModel.getAllTransactions().observe(getViewLifecycleOwner(),
                        transactions -> adapter.setTransactions(transactions));
                break;
            case "Income":
                transactionViewModel.getTransactionsByType("income").observe(getViewLifecycleOwner(),
                        transactions -> adapter.setTransactions(transactions));
                break;
            case "Expense":
                transactionViewModel.getTransactionsByType("expense").observe(getViewLifecycleOwner(),
                        transactions -> adapter.setTransactions(transactions));
                break;
        }
    }

    private void showDateRangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_date_range, null);

        DatePicker startDatePicker = view.findViewById(R.id.start_date_picker);
        DatePicker endDatePicker = view.findViewById(R.id.end_date_picker);

        builder.setView(view)
                .setTitle("Select Date Range")
                .setPositiveButton("Apply", (dialog, which) -> {
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.set(startDatePicker.getYear(), startDatePicker.getMonth(), startDatePicker.getDayOfMonth());
                    long startDate = startCalendar.getTimeInMillis();

                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.set(endDatePicker.getYear(), endDatePicker.getMonth(), endDatePicker.getDayOfMonth());
                    long endDate = endCalendar.getTimeInMillis();

                    // Add one day to end date to include the entire selected day
                    endDate += 86400000; // 24 hours in milliseconds

                    transactionViewModel.getTransactionsByDateRange(startDate, endDate)
                            .observe(getViewLifecycleOwner(), transactions -> {
                                adapter.setTransactions(transactions);
                            });
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }
}