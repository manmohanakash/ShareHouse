package com.akash.sharehouse;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.akash.sharehouse.helpers.URLhelper;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ForgotPassword extends AppCompatActivity {

    EditText useremail;
    private DialogFragment saveDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initResetButton();


    }

    public void initResetButton(){

        Button button_register = findViewById(R.id.fpsubmit_button);
        button_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetPassword();
            }
        });
    }


    public void resetPassword(){

        saveDialog = ForgotPassword.ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Please wait...");

        useremail = findViewById(R.id.oldPass);
        String user = useremail.getText().toString();

        RequestBody formBody = new FormBody.Builder()
                .add("username", user)
                .build();

        URLhelper URLhelper =
                new URLhelper("requestnewpassword", formBody, new URLhelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj) {
                        try{
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }catch (JSONException e){}
                        saveDialog.dismiss();

                        Intent intent = new Intent(ForgotPassword.this,Login.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFinishFailed(String msg) {
                        saveDialog.dismiss();
                        Toast.makeText(getApplicationContext(), msg,
                                Toast.LENGTH_SHORT).show();
                    }
                });

        URLhelper.execute();
    }

    public static class ProgressDialogFragment extends DialogFragment {

        public static ForgotPassword.ProgressDialogFragment newInstance() {
            return new ForgotPassword.ProgressDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(true);

            return dialog;
        }

    }

}
