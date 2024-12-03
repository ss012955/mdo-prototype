package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.R;

import java.util.List;

public class TermsAdapter extends RecyclerView.Adapter<TermsAdapter.TermsViewHolder> {

    private final List<String> termsList;

    public TermsAdapter(List<String> termsList) {
        this.termsList = termsList;
    }

    @NonNull
    @Override
    public TermsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Corrected the layout reference to R.layout.item_terms
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_terms, parent, false);
        return new TermsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TermsViewHolder holder, int position) {
        holder.textView.setText(termsList.get(position));
    }

    @Override
    public int getItemCount() {
        return termsList.size();
    }

    static class TermsViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public TermsViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewTerm);
        }
    }
}