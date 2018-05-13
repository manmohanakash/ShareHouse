package com.akash.sharehouse;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.sharehouse.helpers.hashing;
import com.akash.sharehouse.helpers.URLhelper;
import com.akash.sharehouse.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;


public class Login extends AppCompatActivity {

    private DialogFragment saveDialog;
    private String username, password;
    private SharedPreferences loginData;
    private CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initFields();
        initLoginButton();
        initRegisterButton();
        initForgotPassword();

    }

    public void initFields(){

        username =  ((EditText)findViewById(R.id.username_input)).getText().toString().trim();
        password =  ((EditText)findViewById(R.id.password_input)).getText().toString().trim();
        checkBox = findViewById(R.id.save_checkbox);

        loginData = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String username_SP = loginData.getString("username", "");
        String password_SP = loginData.getString("password", "");
        String remember = loginData.getString("remember", "");

        if(remember.equals("true"))
            checkBox.setChecked(true);

        if (remember.equals("true")){
            ((EditText)findViewById(R.id.username_input)).setText(username_SP);
            ((EditText)findViewById(R.id.password_input)).setText(password_SP);
        }
    }

    public void initLoginButton(){

        Button button_login= findViewById(R.id.login_button);

        button_login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                //Shared preference
                saveUserinfo();
                //Check if fields not empty
                if(validateUser()){
                    doLogin();
                }
            }
        });
    }

    public void initRegisterButton(){
        Button button_register = findViewById(R.id.newuser_button);
        button_register.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }

    public void initForgotPassword(){
        TextView forgotpass = findViewById(R.id.forgotpassword_link);
        forgotpass.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               Intent intent = new Intent(Login.this, ForgotPassword.class);
               startActivity(intent);
            }
        });
    }

    public void saveUserinfo(){
        username =  ((EditText)findViewById(R.id.username_input)).getText().toString().trim();
        password =  ((EditText)findViewById(R.id.password_input)).getText().toString().trim();

        SharedPreferences.Editor editor = loginData.edit();
        if(checkBox.isChecked()) {

            editor.putString("username", username);
            editor.putString("password", password);
            editor.putString("remember", "true");
            editor.apply();
        }
        else{
            editor.putString("username", "");
            editor.putString("password", "");
            editor.putString("remember", "false");
            editor.apply();
        }
    }

    private boolean validateUser() {

        if (username.length() == 0 || password.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter Username/Password",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doLogin(){

        saveDialog = Login.ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Logging in...");

        password = hashing.hashString(password);

        User user = new User();
        user.setUserName(username);
        user.setPassword(password);

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
                new URLhelper("login", formBody, new URLhelper.onFinishListener() {

                    @Override
                    public void onFinishSuccess(JSONObject obj) {
                        saveDialog.dismiss();
                        Intent intent = new Intent(Login.this,DashBoard.class);
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

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Logging in...");
            dialog.setIndeterminate(true);

            return dialog;
        }

    }



}
