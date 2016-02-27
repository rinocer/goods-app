package es.moldovan.givrsapp;

import android.graphics.Color;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.moldovan.givrsapp.objs.Project;
import io.nlopez.smartlocation.SmartLocation;

/**
 * Created by OvidiuMircea on 26/02/2016.
 */
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {
    private Location userLocation;
    private List<Project> dataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imageViewProject)
        ImageView imageViewProject;
        @Bind(R.id.textViewTitle)
        TextView textViewTitle;
        @Bind(R.id.textViewContent)
        TextView textViewContent;
        @Bind(R.id.textViewName)
        TextView textViewName;
        @Bind(R.id.textViewDistance)
        TextView textViewDistance;
        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProjectAdapter(List<Project> dataset, Location userLocation) {
        this.dataset = dataset;
        this.userLocation = userLocation;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Project project = dataset.get(position);
        holder.textViewTitle.setText(project.getName());
        if (project.getImage() != null && project.getImage().length() > 0) {
            holder.imageViewProject.setVisibility(View.VISIBLE);
            Picasso.with(holder.imageViewProject.getContext()).load(project.getImage()).into(holder.imageViewProject);
            holder.textViewTitle.setTextColor(Color.WHITE);
            holder.textViewTitle.setShadowLayer(1f, 2f, 2f, Color.DKGRAY);
        } else {
            holder.imageViewProject.setVisibility(View.GONE);
            holder.textViewTitle.setShadowLayer(0f, 0f, 0f, Color.BLACK);
            holder.textViewTitle.setTextColor(Color.BLACK);
        }
        holder.textViewName.setText(project.getInitiator());
        holder.textViewContent.setText(project.getDescription());
        holder.textViewDistance.setText(this.getDistance(project.getLocation()));
    }

    private CharSequence getDistance(Double[] projLocation) {
        Double distance;
        Location locationA = new Location("point A");
        locationA.setLatitude(projLocation[1]);
        locationA.setLongitude(projLocation[0]);
        distance = Double.valueOf(locationA.distanceTo(userLocation));

        DecimalFormat df = new DecimalFormat("0.0");
        String goTo="Not known";
        if (distance!=null) {
            if (distance < 1000) {
                goTo = "Menos de 1 kilómetro";
            }
            else {
                goTo = String.valueOf(df.format(distance/1000))+" Kms";
            }
        }
        return goTo;
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}