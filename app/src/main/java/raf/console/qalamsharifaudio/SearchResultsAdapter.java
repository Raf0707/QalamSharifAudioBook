package raf.console.qalamsharifaudio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private List<MusicRepository.Track> searchResults;
    private OnItemClickListener onItemClickListener;

    public SearchResultsAdapter(OnItemClickListener onItemClickListener) {
        this.searchResults = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_item, parent, false); // Используйте свой макет для элемента
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicRepository.Track song = searchResults.get(position);
        holder.songTitleTextView.setText(song.title);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView songTitleTextView;
        // Добавьте другие элементы UI, если необходимо

        public ViewHolder(View itemView) {
            super(itemView);
            songTitleTextView = itemView.findViewById(R.id.suraTitle); // Используйте свой ID
            // Инициализируйте другие элементы UI, если необходимо
        }
    }

    public interface OnItemClickListener {
        void onItemClick(MusicRepository.Track song);
    }

    public void updateSearchResults(List<MusicRepository.Track> newResults) {
        this.searchResults = newResults;
        notifyDataSetChanged();
    }
}