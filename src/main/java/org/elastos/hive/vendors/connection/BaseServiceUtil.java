/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive.vendors.connection;

import org.elastos.hive.utils.CheckTextUtil;
import org.elastos.hive.utils.LogUtil;
import org.elastos.hive.vendors.connection.model.BaseServiceConfig;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseServiceUtil {
    private static final int DEFAULT_TIMEOUT = 30;

    public static <S> S createService(Class<S> serviceClass, @NotNull String baseUrl ,
                                      BaseServiceConfig baseServiceConfig) throws Exception {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        if (!CheckTextUtil.isEmpty(baseUrl)) {
            retrofitBuilder.baseUrl(baseUrl);
        } else{
            throw new Exception("base url must not null , and end of /");
        }

        retrofitBuilder.addConverterFactory(StringConverterFactory.create());
        retrofitBuilder.addConverterFactory(NobodyConverterFactory.create());
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create());

        clientBuilder.interceptors().clear();
        if (baseServiceConfig!=null && baseServiceConfig.getHeaderConfig()!=null){
            HeaderInterceptor headerInterceptor = new HeaderInterceptor(baseServiceConfig.getHeaderConfig());
            clientBuilder.interceptors().add(headerInterceptor);
        }

        if (LogUtil.debug){
            NetworkLogInterceptor networkLogInterceptor = new NetworkLogInterceptor();
            clientBuilder.interceptors().add(networkLogInterceptor);
        }

        OkHttpClient client = clientBuilder.build();
        Retrofit retrofit = retrofitBuilder.client(client).build();

        return retrofit.create(serviceClass);
    }
}