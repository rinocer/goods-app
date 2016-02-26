package es.moldovan.givrsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import es.moldovan.givrsapp.objs.Project;

public class ProjectActivity extends AppCompatActivity {

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
