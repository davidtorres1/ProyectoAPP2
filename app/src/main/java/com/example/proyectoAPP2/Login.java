package com.example.proyectoAPP2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    TextView signUp, login;
    EditText email, pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = findViewById(R.id.etMail);
                pass = findViewById(R.id.etPass);
                String emailS = email.getText().toString();
                String passS = pass.getText().toString();
                if(!emailS.isEmpty() && !passS.isEmpty()){
                    login(emailS,passS);
                }else{
                    Toast.makeText(getApplicationContext(),"errroroorrooror",Toast.LENGTH_SHORT).show();
                }

            }
        });
        signUp = findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Sign.class));
            }
        });
    }

    public void login(String email, String pass){
        String url = "https://ecg-super-api.herokuapp.com/login";
        Map<String, String> params = new HashMap<String, String>();
        params.put("password",pass);
        params.put("email", email);
        JSONObject jsonObj = new JSONObject(params);
        Log.wtf("jsonObjt", jsonObj+"");
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.wtf("Response", response.toString());
                        try{
                            boolean status = response.getBoolean("error");
                            Log.wtf("error", status+"");
                            if(!status){
                                Toast.makeText(getApplicationContext(),"Bienvenido",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), profile.class));
                            }else{
                                Toast.makeText(getApplicationContext(),
                                        "Se ha producido un error, por favor intentelo de nuevo en unos minutos",
                                        Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),
                                    "Se ha producido un error, por favor intentelo de nuevo en unos minutos",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.wtf("Error.Response", error);
                        Toast.makeText(getApplicationContext(),
                                "Se ha producido un error, por favor intentelo de nuevo en unos minutos",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
        queue.add(getRequest);
    }
}
