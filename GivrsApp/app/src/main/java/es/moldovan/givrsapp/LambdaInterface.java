package es.moldovan.givrsapp;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

import java.util.List;

import es.moldovan.givrsapp.objs.ListQuery;
import es.moldovan.givrsapp.objs.Project;

/**
 * Created by marian.claudiu on 26/2/16.
 */
public interface LambdaInterface {

    @LambdaFunction(functionName = "givr")
    Project create(Project project);

    @LambdaFunction(functionName = "givr")
    Project[] list(ListQuery listQuery);
}
