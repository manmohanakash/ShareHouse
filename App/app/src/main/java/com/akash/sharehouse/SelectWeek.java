package com.akash.sharehouse;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyboardShortcutGroup;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.sharehouse.adapters.SharedUserAdapter;
import com.akash.sharehouse.adapters.WeekAdapter;
import com.akash.sharehouse.helpers.URLhelper;
import com.akash.sharehouse.models.House;
import com.akash.sharehouse.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static java.util.Calendar.getInstance;

public class SelectWeek extends ListActivity {

    private WeekAdapter adapter;
    private DialogFragment saveDialog;
    private House house;
    Button btnBook;
    String[] listItems;
    boolean[] checkedItems;
    String strStartDate= null;
    String strEndDate=null;

    LinkedHashMap<Integer,String> map=new LinkedHashMap<>();
    LinkedHashMap<Integer,String> mapforadapter=new LinkedHashMap<>();

    ArrayList<Integer> booked = new ArrayList<>();
    ArrayList<Integer> myBooked = new ArrayList<>();
    ArrayList<Integer> newBooked = new ArrayList<>();
    ArrayList<Integer> newBooked2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_week);


        Intent lastintent = getIntent();
        house = (House) lastintent.getSerializableExtra("House");

        TextView housename = findViewById(R.id.houseitem_name);
        housename.setText(house.getHouseName());
        ImageView houseimg = findViewById(R.id.houseitem_image);
        Picasso.get().load(house.getHouseImg()).fit().into(houseimg);

        fetchData();
        try {
            initFetchWeekData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initHomeButton();


    }

    public  void  fetchData(){

        saveDialog = SelectWeek.ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Fetching User Details");

        JSONObject request = new JSONObject();

        try {
            request.put("HouseID",house.getHouseID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody formBody = RequestBody.create(JSON, request.toString());


        URLhelper urlResourceHelper =
                new URLhelper("fetchbookingforhouse", formBody, new URLhelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj){
                        try {
                            JSONArray namesArray = obj.getJSONArray("booking");
                            if (namesArray != null) {
                                for (int i=0;i<namesArray.length();i++){
                                    booked.add(Integer.parseInt(namesArray.getString(i)));
                                    }
                            }


                            createOptions();




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

    public  void createOptions(){

        btnBook =  findViewById(R.id.btnDate);

        Calendar cal = getInstance();

        map.clear();

        for(int i=1;i<2;i++) {

            cal.set(2018, 0, 1);

            {
                Calendar startCal = getInstance();
                startCal.setTimeInMillis(cal.getTimeInMillis());

                int dayOfWeek = startCal.get(Calendar.DAY_OF_WEEK);
                startCal.set(Calendar.DAY_OF_MONTH,
                        (startCal.get(Calendar.DAY_OF_MONTH) - dayOfWeek) + 1);


                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy");
                strStartDate = formatter.format(startCal.getTime());


            }

            {
                Calendar endCal = getInstance();
                endCal.setTimeInMillis(cal.getTimeInMillis());

                int dayOfWeek = endCal.get(Calendar.DAY_OF_WEEK);
                endCal.set(Calendar.DAY_OF_MONTH, endCal.get(Calendar.DAY_OF_MONTH)
                        + (7 - dayOfWeek));

                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy");
                strEndDate = formatter.format(endCal.getTime());


            }

            map.put(i,strStartDate+" - "+strEndDate);
        }

        for(int i=2;i<54;i++) {

            cal.add(Calendar.DATE, 7);

            {
                Calendar startCal = getInstance();
                startCal.setTimeInMillis(cal.getTimeInMillis());

                int dayOfWeek = startCal.get(Calendar.DAY_OF_WEEK);
                startCal.set(Calendar.DAY_OF_MONTH,
                        (startCal.get(Calendar.DAY_OF_MONTH) - dayOfWeek) + 1);


                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy");
                strStartDate = formatter.format(startCal.getTime());
            }

            {
                Calendar endCal = getInstance();
                endCal.setTimeInMillis(cal.getTimeInMillis());

                int dayOfWeek = endCal.get(Calendar.DAY_OF_WEEK);
                endCal.set(Calendar.DAY_OF_MONTH, endCal.get(Calendar.DAY_OF_MONTH)
                        + (7 - dayOfWeek));

                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy");
                strEndDate = formatter.format(endCal.getTime());


            }
            map.put(i, strStartDate + " - " + strEndDate);

        }

        Calendar call = Calendar.getInstance();
        int callweek = call.get(Calendar.WEEK_OF_YEAR);
        for(int i = 0 ; i < callweek ; i++){
            map.remove(i);
        }

        //Removed Unavailable dates from list
        for (int k = 0; k < booked.size(); k++) {
            if (map.containsKey(booked.get(k))) {
                map.remove(booked.get(k));
            }
        }



        listItems=map.values().toArray(new String[0]);

        checkedItems = new boolean[listItems.length];

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(SelectWeek.this);
                mBuilder.setTitle("WEEKS AVAILABLE");
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {

                        if(isChecked){
                            newBooked.add(position);
                        }else{
                            newBooked.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";

                        for (int i = 0; i < newBooked.size(); i++) {
                                item = item + listItems[newBooked.get(i)]+"\n" ;
                        }


                        for (int i = 0; i < newBooked.size(); i++) {
                                for (HashMap.Entry<Integer,String> entry : map.entrySet()){
                                    if( entry.getValue().equals(listItems[newBooked.get(i)])){
                                        newBooked2.add(entry.getKey());
                                    }
                                }
                        }



                        AlertDialog.Builder builder = new AlertDialog.Builder(SelectWeek.this);
                        builder.setMessage("CONFIRM:\n\n"+item);

                        builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ConfirmButton();
                            }
                        });



                        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //perform any action
                                Toast.makeText(getApplicationContext(), "CANCELED", Toast.LENGTH_SHORT).show();
                            }
                        });

                        //creating alert dialog
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                });


                mBuilder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            newBooked.clear();
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();


            }
        });

    }

    public void ConfirmButton(){


                if(newBooked.size()==0){
                    Toast.makeText(getApplicationContext(),"NO WEEK SELECTED", Toast.LENGTH_LONG).show();
                }
                else {
                    saveDialog = SelectWeek.ProgressDialogFragment.newInstance();
                    saveDialog.show(getFragmentManager(), "Fetching User Details");

                    JSONObject request = new JSONObject();

                    try {

                        request.put("HouseID", house.getHouseID());
                        JSONArray WeekArray = new JSONArray();
                        Iterator itr = newBooked2.iterator();
                        while (itr.hasNext()) {
                            int no = ((int) itr.next());
                            WeekArray.put(no);
                        }
                        request.put("weekids", WeekArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody formBody = RequestBody.create(JSON, request.toString());


                    URLhelper urlResourceHelper =
                            new URLhelper("addnewbooking", formBody, new URLhelper.onFinishListener() {
                                @Override
                                public void onFinishSuccess(JSONObject obj) {

                                    try {
                                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent(SelectWeek.this, SelectWeek.class);
                                    intent.putExtra("House", house);
                                    startActivity(intent);
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

    public void initHomeButton() {
        ImageButton button_register = findViewById(R.id.home_icon);
        button_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SelectWeek.this, DashBoard.class);
                startActivity(intent);

            }
        });
    }

    public void initFetchWeekData()  throws JSONException{


        JSONObject request = new JSONObject();

        try {
            request.put("HouseID",house.getHouseID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody formBody = RequestBody.create(JSON, request.toString());


        URLhelper urlResourceHelper =
                new URLhelper("fetchmybookingforhouse", formBody, new URLhelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj){
                        try {
                            JSONArray namesArray = obj.getJSONArray("booking");
                            if (namesArray != null) {
                                for (int i=0;i<namesArray.length();i++){
                                    myBooked.add(Integer.parseInt(namesArray.getString(i)));
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String textcounter = "PREVIOUS WEEKS COUNT:"+myBooked.size();
                        TextView textCounter = findViewById(R.id.textCounter);
                        textCounter.setText(textcounter);
                        initAdapter();
                    }

                    @Override
                    public void onFinishFailed(String msg) {

                        Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();
                    }
                });

        urlResourceHelper.execute();
    }

    public void initAdapter() {

        Calendar cal = getInstance();

        mapforadapter.clear();

        for(int i=1;i<2;i++) {

            cal.set(2018, 0, 1);

            {
                Calendar startCal = getInstance();
                startCal.setTimeInMillis(cal.getTimeInMillis());

                int dayOfWeek = startCal.get(Calendar.DAY_OF_WEEK);
                startCal.set(Calendar.DAY_OF_MONTH,
                        (startCal.get(Calendar.DAY_OF_MONTH) - dayOfWeek) + 1);


                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy");
                strStartDate = formatter.format(startCal.getTime());


            }

            {
                Calendar endCal = getInstance();
                endCal.setTimeInMillis(cal.getTimeInMillis());

                int dayOfWeek = endCal.get(Calendar.DAY_OF_WEEK);
                endCal.set(Calendar.DAY_OF_MONTH, endCal.get(Calendar.DAY_OF_MONTH)
                        + (7 - dayOfWeek));

                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy");
                strEndDate = formatter.format(endCal.getTime());


            }

            mapforadapter.put(i,strStartDate+" - "+strEndDate);
        }

        for(int i=2;i<54;i++) {

            cal.add(Calendar.DATE, 7);

            {
                Calendar startCal = getInstance();
                startCal.setTimeInMillis(cal.getTimeInMillis());

                int dayOfWeek = startCal.get(Calendar.DAY_OF_WEEK);
                startCal.set(Calendar.DAY_OF_MONTH,
                        (startCal.get(Calendar.DAY_OF_MONTH) - dayOfWeek) + 1);


                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy");
                strStartDate = formatter.format(startCal.getTime());
            }

            {
                Calendar endCal = getInstance();
                endCal.setTimeInMillis(cal.getTimeInMillis());

                int dayOfWeek = endCal.get(Calendar.DAY_OF_WEEK);
                endCal.set(Calendar.DAY_OF_MONTH, endCal.get(Calendar.DAY_OF_MONTH)
                        + (7 - dayOfWeek));

                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy");
                strEndDate = formatter.format(endCal.getTime());


            }
            mapforadapter.put(i, strStartDate + " - " + strEndDate);

        }

        try{
             Collections.sort(myBooked);
            adapter = new WeekAdapter(SelectWeek.this,myBooked,house,saveDialog,mapforadapter);
            setListAdapter(adapter);
            saveDialog.dismiss();
        } catch (Exception e) {

            Toast.makeText(this, "Error retrieving Weeks", Toast.LENGTH_LONG).show();
        }

    }

    public static class ProgressDialogFragment extends DialogFragment {

        public static SelectWeek.ProgressDialogFragment newInstance() {
            return new SelectWeek.ProgressDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading...");
            dialog.setIndeterminate(true);

            return dialog;
        }
    }
}
