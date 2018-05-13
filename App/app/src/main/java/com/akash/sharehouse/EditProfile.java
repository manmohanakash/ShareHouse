package com.akash.sharehouse;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.akash.sharehouse.helpers.URLhelper;
import com.akash.sharehouse.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class EditProfile extends AppCompatActivity {

    private DialogFragment saveDialog;
    private EditText first_name,last_name,user_name,email;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);



        first_name = findViewById(R.id.input_firstname);
        last_name = findViewById(R.id.input_lastname);
        email = findViewById(R.id.input_email);
        user_name = findViewById(R.id.input_username);



        initHomeButton();
        initSaveButton();

        saveDialog = EditProfile.ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Fetching User Details...");

        RequestBody formBody = new FormBody.Builder()
                .add("username", "")
                .build();

        URLhelper urlResourceHelper =
                new URLhelper("user", formBody, new URLhelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj){

                        saveDialog.dismiss();


                        try {
                            String userString = obj.getString("User");
                            Gson gson=new Gson();
                            user=gson.fromJson(userString,User.class);
                            first_name.setText(user.getFirstName());
                            last_name.setText(user.getLastName());
                            email.setText(user.getEmail());
                            user_name.setText(user.getUserName());

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

        public static EditProfile.ProgressDialogFragment newInstance() {
            return new EditProfile.ProgressDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Fetching User Details...");
            dialog.setIndeterminate(true);

            return dialog;
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

    public void initSaveButton(){
        Button save = findViewById(R.id.edit_button);

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                saveDialog = EditProfile.ProgressDialogFragment.newInstance();
                saveDialog.show(getFragmentManager(), "Fetching User Details");

                user.setFirstName(first_name.getText().toString());
                user.setLastName(last_name.getText().toString());
                user.setUserName(user_name.getText().toString());
                user.setEmail(email.getText().toString());

                JSONObject request = new JSONObject();
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
                        new URLhelper("editprofile", formBody, new URLhelper.onFinishListener() {
                            @Override
                            public void onFinishSuccess(JSONObject obj){
                                try{
                                saveDialog.dismiss();
                                String msg = obj.getString("message");
                                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(EditProfile.this, DashBoard.class);
                                startActivity(intent);

                                }catch(JSONException e){
                                    Log.i("Test",e.toString());
                                    Toast.makeText(getApplicationContext(),"Error Fetching User Profile",Toast.LENGTH_SHORT).show();
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
        });

    }

}
