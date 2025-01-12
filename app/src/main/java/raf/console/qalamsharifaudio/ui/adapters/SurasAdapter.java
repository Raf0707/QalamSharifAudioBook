package raf.console.qalamsharifaudio.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import raf.console.qalamsharifaudio.R;

import java.util.List;

public class SurasAdapter extends RecyclerView.Adapter<SurasAdapter.SurahViewHolder> {

    private final List<String> surahList;
    private final List<String> translationList;

    public SurasAdapter(List<String> surahList, List<String> translationList) {
        this.surahList = surahList;
        this.translationList = translationList;
    }

    @NonNull
    @Override
    public SurahViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quran_item, parent, false);
        return new SurahViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SurahViewHolder holder, int position) {
        holder.surahName.setText(surahList.get(position));
        holder.surahTranslation.setText(translationList.get(position));
    }

    @Override
    public int getItemCount() {
        return surahList.size();
    }

    static class SurahViewHolder extends RecyclerView.ViewHolder {
        TextView surahName, surahTranslation;

        public SurahViewHolder(@NonNull View itemView) {
            super(itemView);
            surahName = itemView.findViewById(R.id.suraTitle);
            surahTranslation = itemView.findViewById(R.id.suraTranslate);
        }
    }
}

