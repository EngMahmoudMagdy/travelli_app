package com.magdy.travelli.Services;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.magdy.travelli.Data.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohamed on 16/06/2018.
 */

public class LoginRequest extends StringRequestNew {
    private static final String LOGIN_REQUEST_URL = Constants.BASE+"/Login2.php";

    private Map<String,String> params;

    public LoginRequest( String mailorphone,  String password, Response.Listener<String> listener){
        super(Method.POST,LOGIN_REQUEST_URL,listener,null);

        params = new HashMap<>();
        params.put("emailorphone",mailorphone);
        params.put("password",password);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
