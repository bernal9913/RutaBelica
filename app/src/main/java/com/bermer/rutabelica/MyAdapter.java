package com.bermer.rutabelica;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bermer.rutabelica.R;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<DocumentSnapshot> data;

    public MyAdapter(List<DocumentSnapshot> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_text);
        }

        public void bind(DocumentSnapshot document) {
            String title = document.getString("titulo");
            textView.setText(title);
        }
    }
}
