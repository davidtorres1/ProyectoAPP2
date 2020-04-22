package com.example.proyectoAPP2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Sign extends AppCompatActivity {
    Button btnReg;
    EditText name, email, pass1, pass2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        btnReg = findViewById(R.id.register);
        email = findViewById(R.id.Email);
        name = findViewById(R.id.Name);
        pass1 = findViewById(R.id.pass1);
        pass2 = findViewById(R.id.pass2);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameS = name.getText().toString();
                String emailS = email.getText().toString();
                String pass1S = pass1.getText().toString();
                String pass2S = pass2.getText().toString();
                if(pass1S.equals(pass2S) &&!pass1S.isEmpty() && !emailS.isEmpty()){
                    register(nameS, emailS,pass1S);
                }else{
                    Toast.makeText(getApplicationContext(),"errorrrr",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void register(String name, String email, String pass){
        Log.wtf("email: ", email);
        Log.wtf("pass: ", pass);
        String url = "https://ecg-super-api.herokuapp.com/register";
        Map<String, String> params = new HashMap<String, String>();
        params.put("password",pass);
        params.put("name",name);
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
