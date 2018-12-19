package com.magdy.travelli.Services;

import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.magdy.travelli.UI.MyApp;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringRequestNew extends com.android.volley.toolbox.StringRequest {

        private final Map<String, String> _params;

        /**
         * @param method
         * @param url
         * @param params
         *            A {@link HashMap} to post with the request. Null is allowed
         *            and indicates no parameters will be posted along with request.
         * @param listener
         * @param errorListener
         */
        public StringRequestNew(int method, String url, Map<String, String> params, Response.Listener<String> listener,
                             Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
            _params = params;
        }

        public StringRequestNew(int method, String url, Response.Listener<String> listener,
                                Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
            _params = new HashMap<>();
        }
        @Override
        protected Map<String, String> getParams() {
            return _params;
        }

        /* (non-Javadoc)
         * @see com.android.volley.toolbox.StringRequest#parseNetworkResponse(com.android.volley.NetworkResponse)
         */
        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            // since we don't know which of the two underlying network vehicles
            // will Volley use, we have to handle and store session cookies manually
            MyApp.getInstance().checkSessionCookie(response.headers);
            return super.parseNetworkResponse(response);
        }

        /* (non-Javadoc)
         * @see com.android.volley.Request#getHeaders()
         */
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = super.getHeaders();

            if (headers == null
                    || headers.equals(Collections.emptyMap())) {
                headers = new HashMap<String, String>();
            }

            headers.put("Cookie","__test=d3646775009fafe775e7949f52704e76; expires=Fri, 1-Jan-38 23:55:55 GMT; path=/");
//            headers.put("Cookie","__test=df64772e5295c843a229c7f3f419b5b6; expires=Fri, 1-Jan-38 23:55:55 GMT; path=/");
            MyApp.getInstance().addSessionCookie(headers);

            return headers;
        }
    }
