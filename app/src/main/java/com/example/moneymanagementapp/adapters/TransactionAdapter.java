package com.example.moneymanagementapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymanagementapp.R;
import com.example.moneymanagementapp.models.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Transaction> transactions = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction currentTransaction = transactions.get(position);
        holder.textViewTitle.setText(currentTransaction.getTitle());
        holder.textViewAmount.setText(String.format("%.2f", currentTransaction.getAmount()));
        holder.textViewCategory.setText(currentTransaction.getCategory());

        // Format date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String dateString = sdf.format(new Date(currentTransaction.getDate()));
        holder.textViewDate.setText(dateString);

        // Set card color based on transaction type
        int color;
        if (currentTransaction.getType().equals("income")) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.green);
        } else {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.yellow);
        }
        holder.cardView.setCardBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public Transaction getTransactionAt(int position) {
        return transactions.get(position);
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewAmount;
        private TextView textViewCategory;
        private TextView textViewDate;
        private CardView cardView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewAmount = itemView.findViewById(R.id.text_view_amount);
            textViewCategory = itemView.findViewById(R.id.text_view_category);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            cardView = itemView.findViewById(R.id.card_view);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(transactions.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}