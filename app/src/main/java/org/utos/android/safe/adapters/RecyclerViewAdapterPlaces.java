package org.utos.android.safe.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.utos.android.safe.MainActivity;
import org.utos.android.safe.R;
import org.utos.android.safe.dialogs.PlacesOptionsDialog;
import org.utos.android.safe.model.PlacesModel;

import java.util.ArrayList;

import static org.utos.android.safe.R.id.ratingBar;

public class RecyclerViewAdapterPlaces extends RecyclerView.Adapter<RecyclerViewAdapterPlaces.ViewHolder> {

    private final ArrayList<PlacesModel> placesList;
    private final Activity ctx;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final RelativeLayout placeNormRR;
        private final ImageView placeImageView;
        private final RatingBar placeRatingBar;
        private final TextView placeName, placeNoRating, placeMiles;
        private String mItem;

        ViewHolder(View itemView) {
            super(itemView);

            //
            itemView.setOnClickListener(this);

            //
            placeNormRR = (RelativeLayout) itemView.findViewById(R.id.taxiNormRR);
            placeImageView = (ImageView) itemView.findViewById(R.id.taxiImageView);
            placeName = (TextView) itemView.findViewById(R.id.locName);
            placeNoRating = (TextView) itemView.findViewById(R.id.textViewNoRating);
            placeRatingBar = (RatingBar) itemView.findViewById(ratingBar);
            placeMiles = (TextView) itemView.findViewById(R.id.textViewMiles);

        }

        public void setItem(String item) {
            mItem = item;
        }

        @Override public void onClick(View v) {
            PlacesOptionsDialog df = new PlacesOptionsDialog().newInstance(getAdapterPosition(), placesList);
            df.show(((MainActivity) ctx).getSupportFragmentManager(), "dialog");
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public RecyclerViewAdapterPlaces(Activity _ctx, ArrayList<PlacesModel> _placesList) {
        this.placesList = _placesList;
        this.ctx = _ctx;

    }

    @Override public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cardview_places, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder viewHolder, final int pos) {
        //
        viewHolder.placeNormRR.setVisibility(View.VISIBLE);
        // set image
        switch (((MainActivity) ctx).whatPlaceSelected) {
            case "police":
                viewHolder.placeImageView.setImageResource(R.drawable.ic_police);
                break;
            case "hospital":
                viewHolder.placeImageView.setImageResource(R.drawable.ic_local_hospital);
                break;
        }
        // set name
        viewHolder.placeName.setText(placesList.get(pos).getName());
        // set rating
        if (placesList.get(pos).getRating() != 0) {
            viewHolder.placeRatingBar.setVisibility(View.VISIBLE);
            viewHolder.placeNoRating.setVisibility(View.GONE);
            viewHolder.placeRatingBar.setRating(Float.parseFloat(String.valueOf(placesList.get(pos).getRating())));
        } else {
            viewHolder.placeNoRating.setVisibility(View.VISIBLE);
            viewHolder.placeRatingBar.setVisibility(View.GONE);
        }
        // set miles away
        viewHolder.placeMiles.setText(placesList.get(pos).getMiles() + " miles");
    }

    @Override public int getItemCount() {
        return placesList.size();
    }

}
