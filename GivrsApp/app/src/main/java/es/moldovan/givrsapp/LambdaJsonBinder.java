package es.moldovan.givrsapp;

import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaDataBinder;
import com.amazonaws.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 * Created by marian.claudiu on 26/2/16.
 */
public class LambdaJsonBinder implements LambdaDataBinder {

    private final Gson gson;

    /**
     * Constructs a Lambda Json binder.
     */
    public LambdaJsonBinder() {
        this.gson = new Gson();
    }

    @Override
    public <T> T deserialize(byte[] content, Class<T> clazz) {
        if (content == null) {
            return null;
        }
        Reader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content)));
        return gson.fromJson(reader, clazz);
    }

    @Override
    public byte[] serialize(Object object) {
        return gson.toJson(object).getBytes(StringUtils.UTF8);
    }
}