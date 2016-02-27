package es.moldovan.givrsapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.moldovan.givrsapp.objs.Item;
import es.moldovan.givrsapp.objs.Project;

/**
 * Created by OvidiuMircea on 26/02/2016.
 */
public class ProjectActivity  extends AppCompatActivity {

    private Project project;

    @Bind(R.id.projectImage)
    ImageView imageViewProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ButterKnife.bind(this);

        getData();

        bindData();
    }

    private void getData(){
        project = Static.project;
    }

    private void bindData(){
        setTitle(project.getName());

        imageViewProject.setColorFilter(Color.parseColor("#660097A7"));
        String image = project.getImage() == null ? "https://blog.pusher.com/wp-content/uploads/2013/11/hack4good.png":project.getImage();
        Picasso.with(imageViewProject.getContext()).load(image).into(imageViewProject);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_share:
                shareProject();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareProject() {
        StringBuilder builder = new StringBuilder();
        for (Item item : project.getItems()) {
            builder.append(item.getName() + ", ");
        }
        String shareBody = "Just saw a project on Givr that needs your help. You can collaborate offering " + builder.toString() + "thanx!";
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Givrs needed");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share"));
    }
}
