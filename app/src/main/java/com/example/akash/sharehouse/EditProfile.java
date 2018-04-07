package com.example.akash.sharehouse;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.akash.sharehouse.helpers.URLhelper;
import com.example.akash.sharehouse.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class EditProfile extends AppCompatActivity {

    private DialogFragment saveDialog;
    private EditText first_name,last_name,user_name,email,password;
    private String input_firstname,input_lastname,input_email,input_username,input_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        initHomeButton();

        saveDialog = EditProfile.ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Fetching User Details");

        RequestBody formBody = new FormBody.Builder()
                .add("username", "")
                .build();

        URLhelper urlResourceHelper =
                new URLhelper("user", formBody, new URLhelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj){

                        saveDialog.dismiss();


                        first_name = findViewById(R.id.input_firstname);
                        last_name = findViewById(R.id.input_lastname);
                        email = findViewById(R.id.input_email);
                        user_name = findViewById(R.id.input_username);
                        password = findViewById(R.id.input_password);


                        try {
                            String userString = obj.getString("User");
                            Gson gson=new Gson();
                            User user=gson.fromJson(userString,User.class);
                            first_name.setText(user.getFirstName());
                            last_name.setText(user.getLastName());
                            email.setText(user.getEmail());
                            user_name.setText(user.getUserName());
                            password.setText(user.getPassword());

                        }catch(JSONException e){
                            Log.i("Test",e.toString());
                            Toast.makeText(getApplicationContext(),"Error Fetching User Profile",Toast.LENGTH_SHORT);
                        }



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
        public static Register.ProgressDialogFragment newInstance() {
            return new Register.ProgressDialogFragment();
        }
    }



    public void initHomeButton() {
        ImageButton button_register = findViewById(R.id.home_icon);
        button_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(EditProfile.this, DashBoard.class);
                startActivity(intent);

            }
        });
    }

}
