package es.moldovan.givrsapp;

import com.amazonaws.mobileconnectors.apigateway.annotation.Operation;
import com.amazonaws.mobileconnectors.apigateway.annotation.Parameter;
import com.amazonaws.mobileconnectors.apigateway.annotation.Service;

import java.util.*;


@Service(endpoint = "https://1bh6fh0p6a.execute-api.eu-west-1.amazonaws.com/prod")
public interface ProjectMicroserviceClient {

    /**
     * @return void
     */
    @Operation(path = "/givr", method = "POST")
    void givrPost();

}
