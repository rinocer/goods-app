package es.moldovan.givrsapp;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;

/**
 * Created by OvidiuMircea on 26/02/2016.
 */
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {
    private String[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProjectAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_proj, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //and so on
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ProjectItem feed = dataset.get(position);
        holder.textViewTitle.setText(feed.getTitle());
        holder.textViewContent.setText(Html.fromHtml(feed.getDescription().replaceAll("</?img[^>]*?>", "")));
        if (feed.getImageUrl() != null && feed.getImageUrl().length() > 0) {
            holder.imageViewFeed.setVisibility(View.VISIBLE);
            Picasso.with(holder.imageViewFeed.getContext()).load(feed.getImageUrl()).into(holder.imageViewFeed);
            holder.textViewTitle.setTextColor(Color.WHITE);
            holder.textViewTitle.setShadowLayer(1f, 2f, 2f, Color.DKGRAY);
        } else {
            holder.imageViewFeed.setVisibility(View.GONE);
            holder.textViewTitle.setShadowLayer(0f, 0f, 0f, Color.BLACK);
            holder.textViewTitle.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}