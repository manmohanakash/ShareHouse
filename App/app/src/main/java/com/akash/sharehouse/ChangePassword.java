package com.akash.sharehouse;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.akash.sharehouse.helpers.URLhelper;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.akash.sharehouse.helpers.hashing.hashString;

public class ChangePassword extends AppCompatActivity {


    private DialogFragment saveDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initHomeButton();
        initChangeButton();

    }


    public void initHomeButton() {
        ImageButton button_register = findViewById(R.id.home_icon);
        button_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ChangePassword.this, DashBoard.class);
                startActivity(intent);

            }
        });
    }

    public void initChangeButton() {


        final Button button_register = findViewById(R.id.changepass_button);

        button_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(button_register.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                final EditText ET_oldpass = findViewById(R.id.oldPass);
                final EditText ET_newpass =  findViewById(R.id.newPass);
                final EditText ET_confirmpass = findViewById(R.id.confirmPass);

                String old_pass = ET_oldpass.getText().toString();
                String new_pass = ET_newpass.getText().toString();
                String confirm_pass = ET_confirmpass.getText().toString();

                if(old_pass.equals("")  || new_pass.equals("") || confirm_pass.equals("")){

                    Toast.makeText(getApplicationContext(),"Fields cannot be empty",Toast.LENGTH_SHORT).show();


                }
                else if(!new_pass.equals(confirm_pass)){
                    Toast.makeText(getApplicationContext(),"New passwords dont match",Toast.LENGTH_SHORT).show();
                }
                else{
                    saveDialog = ChangePassword.ProgressDialogFragment.newInstance();
                    saveDialog.show(getFragmentManager(), "Changing Password...");

                    RequestBody formBody = new FormBody.Builder()
                            .add("oldPassword", hashString(old_pass))
                            .add("newPassword",hashString(new_pass))
                            .build();

                    URLhelper urlResourceHelper = new URLhelper("changePassword", formBody, new URLhelper.onFinishListener() {

                        @Override
                        public void onFinishSuccess(JSONObject obj) {

                            ET_oldpass.setText("");
                            ET_newpass.setText("");
                            ET_confirmpass.setText("");
                            Toast.makeText(getApplicationContext(), "Password Changed", Toast.LENGTH_SHORT).show();
                            saveDialog.dismiss();

                        }


                        @Override
                        public void onFinishFailed(String msg) {
                            saveDialog.dismiss();
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        }
                    });

                    urlResourceHelper.execute();
                }
            }
        });
    }

    public static class ProgressDialogFragment extends DialogFragment {

        public static ChangePassword.ProgressDialogFragment newInstance() {
            return new ChangePassword.ProgressDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Changing Password...");
            dialog.setIndeterminate(true);

            return dialog;
        }

    }


}
