package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.R;

import java.util.List;

import HelperClasses.TriviaItem;

public class TriviaAdapter extends RecyclerView.Adapter<TriviaAdapter.TriviaViewHolder> {
    private List<TriviaItem> triviaList; // Use TriviaItem instead of Trivia

    public TriviaAdapter(List<TriviaItem> triviaList) {
        this.triviaList = triviaList;
    }

    @Override
    public TriviaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trivia_item, parent, false);
        return new TriviaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TriviaViewHolder holder, int position) {
        TriviaItem trivia = triviaList.get(position); // Use TriviaItem instead of Trivia
        holder.triviaTitle.setText(trivia.getTitle());
        holder.triviaText.setText(trivia.getText());
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
            triviaText = itemView.findViewById(R.id.triviaText);
        }
    }
}