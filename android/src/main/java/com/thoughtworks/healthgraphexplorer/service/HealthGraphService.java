package com.thoughtworks.healthgraphexplorer.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class HealthGraphService extends RetrofitGsonSpiceService {
    @Target(METHOD)
    @Retention(RUNTIME)
    public @interface HealthGraphDynamicPath {
    }

    private final static String BASE_URL = "https://api.runkeeper.com/";

    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(HealthGraphApi.class);
    }

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }

    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        Gson gson = new GsonBuilder().setDateFormat("EEE, d MMM yyyy HH:mm:ss").create();
        Converter converter = new GsonConverter(gson);

        RequestInterceptor requestInterceptor =
                HealthGraphAuthManager.getInstance().getRequestInterceptor();

        return super.createRestAdapterBuilder()
                .setConverter(converter)
                .setRequestInterceptor(requestInterceptor);
    }

    @SuppressWarnings("unchecked")
    @Override
    // Override getRetrofitService() to return a proxy around the service object in order to
    // dynamically fetch the endpoint URL for methods annotated with @HealthGraphDynamicPath. The
    // HealthGraph API spec states that no client should hardcode any endpoints URLs except for
    // the endpoint /user which contains links to the other endpoints.
    protected <T> T getRetrofitService(Class<T> serviceClass) {
        T service = super.getRetrofitService(serviceClass);
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class<?>[]{serviceClass},
                new DynamicRestPathInvocationHandler(service));
    }

    private class DynamicRestPathInvocationHandler<T> implements InvocationHandler {
        private final T targetServiceObj;

        public DynamicRestPathInvocationHandler(T targetServiceObj) {
            this.targetServiceObj = targetServiceObj;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            Log.d("xxx", "Calling service method: " + method.getName());

            for (Annotation methodAnnotation : method.getAnnotations()) {
                if (methodAnnotation.annotationType() == HealthGraphDynamicPath.class) {
                    Log.d("xxx", "TODO: make the URL for this endpoint dynamic");
                    break;
                }
            }

            Log.d("xxx", "Proxying to original implementation");
            return method.invoke(targetServiceObj, objects);
        }
    }
}
