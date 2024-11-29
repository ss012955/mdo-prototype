package Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.R;
import com.example.prototype.Trivia;

import java.util.List;

import HelperClasses.TriviaItem;

public class TriviaDashboardAdapter extends RecyclerView.Adapter<TriviaDashboardAdapter.TriviaViewHolder> {
    private List<TriviaItem> triviaList; // Use TriviaItem instead of Trivia
    private Context context;

    public TriviaDashboardAdapter(Context context,List<TriviaItem> triviaList) {
        this.context = context;
        this.triviaList = triviaList;
    }

    @Override
    public TriviaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trivia_card, parent, false);
        return new TriviaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TriviaViewHolder holder, int position) {
        TriviaItem trivia = triviaList.get(position); // Use TriviaItem instead of Trivia
        holder.triviaTitle.setText(trivia.getTitle());

        // Set a click listener on the item view
        holder.itemView.setOnClickListener(v -> {
            // When an item is clicked, start the Trivia activity
            Intent intent = new Intent(context, Trivia.class);
            context.startActivity(intent); // Start the Trivia activity
        });
    }
    @Override
    public int getItemCount() {
        return triviaList.size();
    }

    public static class TriviaViewHolder extends RecyclerView.ViewHolder {
        TextView triviaTitle, triviaText;

        public TriviaViewHolder(View itemView) {
            super(itemView);
            triviaTitle = itemView.findViewById(R.id.triviaTitle);
        }
    }
}
