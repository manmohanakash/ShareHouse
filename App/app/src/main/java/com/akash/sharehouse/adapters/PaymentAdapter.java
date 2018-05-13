package com.akash.sharehouse.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.akash.sharehouse.EditHouse;
import com.akash.sharehouse.PaymentActivity;
import com.akash.sharehouse.R;
import com.akash.sharehouse.helpers.URLhelper;
import com.akash.sharehouse.models.Payment;
import com.akash.sharehouse.models.User;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PaymentAdapter extends ArrayAdapter<Payment> {

    private ArrayList<Payment> payments;
    private Context adapterContext;
    private ImageButton delete_icon;
    private DialogFragment saveDialog;
    private int userID;
    private ArrayList<User> users;

    public PaymentAdapter(Context context, ArrayList<Payment> payments,DialogFragment saveDialog,int userID,ArrayList<User> users) {
        super(context, R.layout.payment_item, payments);
        adapterContext = context;
        this.payments = payments;
        this.saveDialog = saveDialog;
        this.userID = userID;
        this.users = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        try {
            final Payment payment = payments.get(position);

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.payment_item, null);
            }

            TextView payment_name = v.findViewById(R.id.payment_name);
            TextView payment_money = v.findViewById(R.id.payment_money);
            ImageButton delete_icon = v.findViewById(R.id.payment_delete);

            if(userID == payment.getUserID1()){

                payment_name.setTextColor(Color.RED);
                payment_money.setTextColor(Color.RED);
                delete_icon.setVisibility(View.INVISIBLE);

                Iterator<User> itr = users.iterator();
                while(itr.hasNext()){
                    User u = itr.next();
                    if(u.getUserID()==payment.getUserID2()){
                        payment_name.setText(u.getEmail());
                    }
                }
                payment_money.setText(payment.getAmount().toString());
            }
            else{
                payment_name.setTextColor(Color.GREEN);
                payment_money.setTextColor(Color.GREEN);
                delete_icon.setVisibility(View.VISIBLE);

                Iterator<User> itr = users.iterator();
                while(itr.hasNext()){
                    User u = itr.next();
                    if(u.getUserID()==payment.getUserID1()){
                        payment_name.setText(u.getEmail());
                    }
                }

                payment_money.setText(payment.getAmount().toString());
            }




            delete_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(adapterContext);
                    builder.setMessage("CONFIRM PAYMENT:\n\nBy deleting you accept that you have received the money for this booking and cannot be undone.");

                    builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            saveDialog = PaymentActivity.ProgressDialogFragment.newInstance();
                            saveDialog.show(((Activity) adapterContext).getFragmentManager(), "Deleting House ");

                            JSONObject request = new JSONObject();

                            try {
                                request.put("userID1",payment.getUserID1());
                                request.put("userID2",payment.getUserID2());
                                request.put("amt",payment.getAmount());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                            RequestBody formBody = RequestBody.create(JSON, request.toString());


                            URLhelper urlResourceHelper =
                                    new URLhelper("deletepayment", formBody, new URLhelper.onFinishListener() {
                                        @Override
                                        public void onFinishSuccess(JSONObject obj){
                                            try {
                                                String msg = obj.getString("message");
                                                Toast.makeText(adapterContext.getApplicationContext(),msg, Toast.LENGTH_SHORT).show();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            Intent intent = new Intent(adapterContext.getApplicationContext(),PaymentActivity.class);
                                            adapterContext.startActivity(intent);
                                            saveDialog.dismiss();
                                        }

                                        @Override
                                        public void onFinishFailed(String msg) {
                                            Toast.makeText(adapterContext.getApplicationContext(),msg, Toast.LENGTH_SHORT).show();
                                            saveDialog.dismiss();
                                        }
                                    });
                            urlResourceHelper.execute();
                        }
                    });


                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //perform any action
                            //Toast.makeText(adapterContext.getApplicationContext(), "CANCELED", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //creating alert dialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();


                }
            });


        }

        catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return v;
    }

}
