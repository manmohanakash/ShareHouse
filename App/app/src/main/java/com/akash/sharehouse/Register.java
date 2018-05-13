package com.akash.sharehouse;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.akash.sharehouse.helpers.URLhelper;
import com.akash.sharehouse.helpers.hashing;
import com.akash.sharehouse.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class Register extends AppCompatActivity {

    private DialogFragment saveDialog;
    private EditText first_name,last_name,user_name,email,password;
    private String input_firstname,input_lastname,input_email,input_username,input_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initRegisterButton();
    }

    public void initRegisterButton(){
        Button button_register = findViewById(R.id.register_button);
        button_register.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(validateFeilds()){
                    doRegister();
                }
            }
        });

    }

    public boolean validateFeilds(){




        first_name = findViewById(R.id.input_firstname);
        last_name = findViewById(R.id.input_lastname);
        email = findViewById(R.id.input_email);
        user_name = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);

        input_firstname = first_name.getText().toString();
        input_lastname = last_name.getText().toString();
        input_email = email.getText().toString();
        input_username = user_name.getText().toString();
        input_password = password.getText().toString();

        if(input_firstname.equals("") || input_lastname.equals("") ||input_email.equals("") || input_username.equals("") || input_password.equals("")){
            Toast.makeText(getApplicationContext(),"Please enter all fields",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void doRegister(){

        saveDialog = Register.ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Registering");
        input_password = hashing.hashString(input_password);

        User user = new User();
        user.setFirstName(input_firstname);
        user.setLastName(input_lastname);
        user.setEmail(input_email);
        user.setUserName(input_username);
        user.setPassword(input_password);

        String json = null;
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        try {
            json = ow.writeValueAsString(user);

        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody formBody = RequestBody.create(JSON, json);

        URLhelper urlResourceHelper =
                new URLhelper("register", formBody, new URLhelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj) {
                        saveDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Account Registered",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Register.this,Login.class);
                        startActivity(intent);

                    }

                    @Override
                    public void onFinishFailed(String msg) {
                        saveDialog.dismiss();
                        Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();
                    }
                });

        urlResourceHelper.execute();
    }

    public static class ProgressDialogFragment extends DialogFragment {
        public static ProgressDialogFragment newInstance() {
            return new ProgressDialogFragment();
        }
    }


}
