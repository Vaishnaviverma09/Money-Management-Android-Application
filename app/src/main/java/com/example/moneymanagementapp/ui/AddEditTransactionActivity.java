package com.example.moneymanagementapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneymanagementapp.R;
import com.example.moneymanagementapp.models.Transaction;
import com.example.moneymanagementapp.viewmodels.TransactionViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class AddEditTransactionActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.example.moneymanager.EXTRA_ID";

    private EditText editTextTitle;
    private EditText editTextAmount;
    private Spinner spinnerCategory;
    private RadioGroup radioGroupType;
    private EditText editTextNote;
    private DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_transaction);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextAmount = findViewById(R.id.edit_text_amount);
        spinnerCategory = findViewById(R.id.spinner_category);
        radioGroupType = findViewById(R.id.radio_group_type);
        editTextNote = findViewById(R.id.edit_text_note);
        datePicker = findViewById(R.id.date_picker);

        // Setup category spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Set current date as default
        Calendar calendar = Calendar.getInstance();
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), null);

        // Handle save button
        FloatingActionButton buttonSave = findViewById(R.id.button_save);
        buttonSave.setOnClickListener(v -> saveTransaction());

        // Check if we're editing an existing transaction
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit Transaction");
            int id = intent.getIntExtra(EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(this, "Cannot update transaction", Toast.LENGTH_SHORT).show();
                return;
            }

            TransactionViewModel transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
            transactionViewModel.getAllTransactions().observe(this, transactions -> {
                for (Transaction transaction : transactions) {
                    if (transaction.getId() == id) {
                        populateFields(transaction);
                        break;
                    }
                }
            });
        } else {
            setTitle("Add Transaction");
        }
    }

    private void populateFields(Transaction transaction) {
        editTextTitle.setText(transaction.getTitle());
        editTextAmount.setText(String.valueOf(transaction.getAmount()));

        // Set category
        ArrayAdapter adapter = (ArrayAdapter) spinnerCategory.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(transaction.getCategory())) {
                spinnerCategory.setSelection(i);
                break;
            }
        }

        // Set type
        if (transaction.getType().equals("income")) {
            radioGroupType.check(R.id.radio_income);
        } else {
            radioGroupType.check(R.id.radio_expense);
        }

        editTextNote.setText(transaction.getNote());

        // Set date
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(transaction.getDate());
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void saveTransaction() {
        String title = editTextTitle.getText().toString().trim();
        String amountString = editTextAmount.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String note = editTextNote.getText().toString().trim();

        // Get selected type
        int selectedId = radioGroupType.getCheckedRadioButtonId();
        String type;
        if (selectedId == R.id.radio_income) {
            type = "income";
        } else {
            type = "expense";
        }

        // Validate inputs
        if (title.isEmpty()) {
            editTextTitle.setError("Please enter a title");
            editTextTitle.requestFocus();
            return;
        }

        if (amountString.isEmpty()) {
            editTextAmount.setError("Please enter an amount");
            editTextAmount.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            editTextAmount.setError("Please enter a valid amount");
            editTextAmount.requestFocus();
            return;
        }

        // Get date from date picker
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        long date = calendar.getTimeInMillis();

        // Create transaction object
        Transaction transaction = new Transaction(title, amount, category, type, note, date);

        // Check if we're updating or inserting new
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            transaction.setId(id);
        }

        TransactionViewModel transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        if (id == -1) {
            transactionViewModel.insert(transaction);
        } else {
            transactionViewModel.update(transaction);
        }

        finish();
    }
}