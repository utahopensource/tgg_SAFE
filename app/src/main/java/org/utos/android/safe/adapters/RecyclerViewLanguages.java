package org.utos.android.safe.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.utos.android.safe.R;
import org.utos.android.safe.SetupActivity;
import org.utos.android.safe.model.LanguageModel;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static org.utos.android.safe.BaseActivity.SHARED_PREFS;
import static org.utos.android.safe.BaseActivity.USER_LANG;
import static org.utos.android.safe.BaseActivity.USER_LANG_LOCALE;

public class RecyclerViewLanguages extends RecyclerView.Adapter<RecyclerViewLanguages.ViewHolder> {

    private final ArrayList<LanguageModel> list;
    private final Activity ctx;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView language;
        private String mItem;

        ViewHolder(View itemView) {
            super(itemView);

            //
            itemView.setOnClickListener(this);

            //
            language = (TextView) itemView.findViewById(R.id.langTextView);

        }

        public void setItem(String item) {
            mItem = item;
        }

        @Override public void onClick(View v) {
            SharedPreferences.Editor prefsEditor = ctx.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
            prefsEditor.putString(USER_LANG, list.get(getAdapterPosition()).getLanguage());
            prefsEditor.putString(USER_LANG_LOCALE, list.get(getAdapterPosition()).getLocal());
            prefsEditor.apply();

            //
            Intent intent = new Intent(ctx, SetupActivity.class);
            ctx.startActivity(intent);
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public RecyclerViewLanguages(Activity _ctx, ArrayList<LanguageModel> _placesList) {
        this.list = _placesList;
        this.ctx = _ctx;

    }

    @Override public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cardview_language, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder viewHolder, final int pos) {
        // set name
        viewHolder.language.setText(list.get(pos).getLanguage());
    }

    @Override public int getItemCount() {
        return list.size();
    }

}
