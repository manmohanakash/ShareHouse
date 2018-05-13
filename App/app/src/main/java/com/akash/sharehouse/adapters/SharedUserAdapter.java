package com.akash.sharehouse.adapters;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.sharehouse.EditHouse;
import com.akash.sharehouse.R;
import com.akash.sharehouse.helpers.URLhelper;
import com.akash.sharehouse.models.House;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SharedUserAdapter extends ArrayAdapter<String> {

        private ArrayList<String> names;
        private DialogFragment saveDialog;
        private Context adapterContext;
        private ImageButton delete_icon;
        private House house;
        private String email;


        public SharedUserAdapter(Context context, ArrayList<String> names,House house,String email,DialogFragment saveDialog) {
            super(context, R.layout.shared_user, names);
            adapterContext = context;
            this.names = names;
            this.house = house;
            this.email = email;
            this.saveDialog = saveDialog;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            try {
                final String name = names.get(position);

                if (v == null) {
                    LayoutInflater vi = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.shared_user, null);
                }


                TextView user_name = v.findViewById(R.id.payment_name);
                user_name.setText(name);

                delete_icon = v.findViewById(R.id.delete_icon);

                if(email.equalsIgnoreCase(name)) {
                    delete_icon.setBackgroundResource(R.mipmap.crown);
                    delete_icon.setEnabled(false);
                }else{
                    delete_icon.setVisibility(View.VISIBLE);
                }

                delete_icon.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View view) {

                        saveDialog = EditHouse.ProgressDialogFragment.newInstance();
                        saveDialog.show(((Activity) adapterContext).getFragmentManager(), "Adding new Shared User ");

                        JSONObject request = new JSONObject();
                        try {
                        request.put("HouseID",house.getHouseID());
                        request.put("UserEmail",name);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody formBody = RequestBody.create(JSON, request.toString());

                    URLhelper urlResourceHelper =
                        new URLhelper("removeuserforhouse", formBody, new URLhelper.onFinishListener() {

                            @Override
                            public void onFinishSuccess(JSONObject obj) {
                                String msg = null;
                                try {
                                    msg = obj.getString("message");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getContext(),EditHouse.class);
                                intent.putExtra("House",house);
                                getContext().startActivity(intent);
                            }

                            @Override
                            public void onFinishFailed(String msg) {
                                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        });

                urlResourceHelper.execute();

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