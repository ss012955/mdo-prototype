package Adapters;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.R;

import java.util.List;

import HelperClasses.FAQsItem;

public class FAQsAdapter extends RecyclerView.Adapter<FAQsAdapter.FAQsViewHolder> {

    private final List<FAQsItem> faqsList;

    public FAQsAdapter(List<FAQsItem> faqsList) {
        this.faqsList = faqsList;
    }

    @NonNull
    @Override
    public FAQsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faqs_card, parent, false);
        return new FAQsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQsViewHolder holder, int position) {
        FAQsItem faq = faqsList.get(position);
        holder.question.setText(faq.getQuestions());
        holder.answer.setText(faq.getAnswer());
    }

    @Override
    public int getItemCount() {
        return faqsList.size();
    }

    static class FAQsViewHolder extends RecyclerView.ViewHolder {
        TextView question, answer;

        public FAQsViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.faqsQuestion);
            answer = itemView.findViewById(R.id.faqsAnswer);
        }
    }
}