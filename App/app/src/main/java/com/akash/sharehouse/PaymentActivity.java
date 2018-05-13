package com.akash.sharehouse;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.sharehouse.adapters.HouseAdminAdapter;
import com.akash.sharehouse.adapters.PaymentAdapter;
import com.akash.sharehouse.helpers.URLhelper;
import com.akash.sharehouse.models.House;
import com.akash.sharehouse.models.Payment;
import com.akash.sharehouse.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PaymentActivity extends ListActivity {

    private ArrayList<Payment> payments;
    private ArrayList<User> users;
    private HashMap<Integer,String> emailList;
    private PaymentAdapter adapter;
    private DialogFragment saveDialog;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initHomeButton();
        fetchMyPaymentDetails();
    }

    public void initHomeButton() {
        ImageButton button_register = findViewById(R.id.home_icon);
        button_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, DashBoard.class);
                startActivity(intent);

            }
        });
    }

    public void fetchMyPaymentDetails(){

        saveDialog = PaymentActivity.ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Fetching User Details");


        JSONObject request = new JSONObject();

        try {
            request.put("Test","hello");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody formBody = RequestBody.create(JSON, request.toString());


        URLhelper urlResourceHelper =
                new URLhelper("fetchmypayments", formBody, new URLhelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj){
                        try{

                            userID = Integer.parseInt(obj.getString("UserID"));

                            JSONArray paymentsArray = obj.getJSONArray("payments");
                            Type listType = new TypeToken<ArrayList<Payment>>(){}.getType();
                            payments = new Gson().fromJson(paymentsArray.toString(), listType);

                            JSONArray namesArray = obj.getJSONArray("Names");
                            listType = new TypeToken<ArrayList<User>>(){}.getType();
                            users =  new Gson().fromJson(namesArray.toString(), listType);

                            if(payments.isEmpty()){
                                TextView nopayments = findViewById(R.id.nopayments);
                                nopayments.setText("There are no payments available at this time");
                            }
                            initAdapter();
                            saveDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFinishFailed(String msg) {

                        Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();
                    }
                });

        urlResourceHelper.execute();
    }

    public void initAdapter() {

        try{
            adapter = new PaymentAdapter(PaymentActivity.this,payments,saveDialog,userID,users);
            setListAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error retrieving Houses", Toast.LENGTH_LONG).show();
        }

    }

    public static class ProgressDialogFragment extends DialogFragment {

        public static PaymentActivity.ProgressDialogFragment newInstance() {
            return new PaymentActivity.ProgressDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading  Payments...");
            dialog.setIndeterminate(true);

            return dialog;
        }

    }
}
