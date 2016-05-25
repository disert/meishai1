/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.meishai.net;

import java.io.UnsupportedEncodingException;

import com.meishai.net.volley.NetworkResponse;
import com.meishai.net.volley.Request;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.HttpHeaderParser;
import com.meishai.util.GsonHelper;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class MeishaiRequest extends Request<RespData> {
    private final Listener<RespData> mListener;

    /**
     * Creates a new request with the given method.
     *
     * @param method        the request {@link Method} to use
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public MeishaiRequest(int method, String url, Listener<RespData> listener,
                          ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    /**
     * Creates a new GET request.
     *
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public MeishaiRequest(String url, Listener<RespData> listener,
                          ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
    }

    @Override
    protected void deliverResponse(RespData response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<RespData> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        RespData data;
        try {
            parsed = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        data = GsonHelper.parseObject(parsed, RespData.class);
        if (null == data) {
            data = new RespData();
            data.setSuccess(-11);
//			data.setTips("数据异常");
        }
        return Response.success(data,
                HttpHeaderParser.parseCacheHeaders(response));
    }
}
