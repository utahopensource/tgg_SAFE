package org.utos.android.safe.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.utos.android.safe.R;

public class RecyclerViewAdapterEmpty extends RecyclerView.Adapter<RecyclerViewAdapterEmpty.ViewHolder> {

    private final Context ctx;
    private final String whereComingFrom;

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView textViewTitle;
        final ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);

            // ui
            textViewTitle = (TextView) itemView.findViewById(R.id.textViewEmptyTitle);
            imageView = (ImageView) itemView.findViewById(R.id.imgView);

        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public RecyclerViewAdapterEmpty(Context _ctx, String _whereComingFrom) {
        this.ctx = _ctx;
        this.whereComingFrom = _whereComingFrom;

    }

    @Override public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cardview_empty, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder tripViewHolder, int pos) {
        switch (whereComingFrom) {
            case "no_results":
                tripViewHolder.textViewTitle.setText("No results found.");
                tripViewHolder.imageView.setImageResource(R.drawable.ic_assignment);
                break;
            case "error":
                tripViewHolder.textViewTitle.setText("An error has occurred.");
                tripViewHolder.imageView.setImageResource(R.drawable.ic_error);
                break;
        }

    }

    @Override public int getItemCount() {
        return 1;
    }

}
