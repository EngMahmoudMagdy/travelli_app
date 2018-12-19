package com.magdy.travelli.Services;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.magdy.travelli.Data.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohamed on 14/06/2018.
 */

public class RegisterRequest extends StringRequestNew {

    private static final String REGISTER_REQUEST_URL = Constants.BASE+"/Register2.php";

    private Map<String,String> params;

    public RegisterRequest(String username, String mail, String phone, String password, Response.Listener<String> listener){
        super(Method.POST,REGISTER_REQUEST_URL,listener,null);

        params = new HashMap<>();
        params.put("name",username);
        params.put("email",mail);
        params.put("phone_num",phone);
        params.put("password",password);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
