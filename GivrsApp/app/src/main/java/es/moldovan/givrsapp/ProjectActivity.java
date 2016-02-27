package es.moldovan.givrsapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import es.moldovan.givrsapp.objs.Project;

/**
 * Created by OvidiuMircea on 26/02/2016.
 */
public class ProjectActivity  extends AppCompatActivity {

    private Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        ButterKnife.bind(this);

        getData();

        bindData();
    }

    private void getData(){
        project = Static.project;
    }

    private void bindData(){

    }
}
