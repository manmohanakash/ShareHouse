package com.akash.sharehouse;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.akash.sharehouse.adapters.HouseAdapter;
import com.akash.sharehouse.adapters.SharedUserAdapter;
import com.akash.sharehouse.helpers.AppConfig;
import com.akash.sharehouse.helpers.URLhelper;
import com.akash.sharehouse.models.House;
import com.akash.sharehouse.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import android.location.Geocoder;

public class EditHouse extends ListActivity  {


    private SharedUserAdapter adapter;
    private DialogFragment saveDialog;
    private ArrayList<String> names;
    private House house;
    private String email;
    private final int IMAGE_GALLERY_REQUEST=20;
    private Bitmap image;
    ImageView houseRef;

    EditText input_name,input_no,input_street,input_city,input_state,input_country,input_landmark,input_description,input_weekRate,input_maxWeeks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_house);



        Intent lastintent = getIntent();
        house = (House) lastintent.getSerializableExtra("House");


        input_name = findViewById(R.id.input_name);
        input_no = findViewById(R.id.input_no);
        input_street = findViewById(R.id.input_street);
        input_city = findViewById(R.id.input_city);
        input_state = findViewById(R.id.input_state);
        input_country = findViewById(R.id.input_country);
        input_landmark = findViewById(R.id.input_landmark) ;
        input_description = findViewById(R.id.input_description);
        input_maxWeeks = findViewById(R.id.input_maxWeeks);
        input_weekRate = findViewById(R.id.input_weekRate);
        houseRef = findViewById(R.id.imageRef);

        initHomeButton();
        initEditTexts(house);
        initAddSharedButton();
        initSumbmitButton();
        initAddImageButton();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            initFetchData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void initEditTexts(House house){

        saveDialog = ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Fetching User Details");

        input_name.setText(house.getHouseName().toUpperCase());
        input_no.setText(house.getHouseNo().toUpperCase());
        input_street.setText(house.getHouseStreet().toUpperCase());
        input_city.setText(house.getHouseCity().toUpperCase());
        input_state.setText(house.getHouseState().toUpperCase());
        input_country.setText(house.getHouseCountry().toUpperCase());
        input_landmark.setText(house.getLandmark().toUpperCase());
        input_description.setText(house.getDescription().toUpperCase());
        input_maxWeeks.setText(house.getMaxWeeks().toString());
        input_weekRate.setText(house.getWeekRate().toString());
        Picasso.get().load(house.getHouseImg()).fit().memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(houseRef);
    }


    public void initHomeButton() {
        ImageButton button_register = findViewById(R.id.home_icon);
        button_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(EditHouse.this, DashBoard.class);
                startActivity(intent);

            }
        });
    }

    public void initSumbmitButton() {

        Button button_register = findViewById(R.id.update_house);
        button_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                saveDialog = EditHouse.ProgressDialogFragment.newInstance();
                saveDialog.show(getFragmentManager(), "Updating house ");


                house.setHouseName(input_name.getText().toString());
                house.setHouseNo(input_no.getText().toString());
                house.setHouseStreet(input_street.getText().toString());
                house.setHouseState(input_state.getText().toString());
                house.setHouseCity(input_city.getText().toString());
                house.setHouseCountry(input_country.getText().toString());
                house.setLandmark(input_landmark.getText().toString());
                house.setDescription(input_description.getText().toString());
                house.setWeekRate(Integer.parseInt(input_weekRate.getText().toString()));
                house.setMaxWeeks(Integer.parseInt(input_maxWeeks.getText().toString()));

                String addressToSend = house.getHouseNo()+","+house.getHouseStreet()+","+house.getHouseCity()
                        +","+house.getHouseState()+","+house.getHouseCountry();

                Geocoder geo = new Geocoder(EditHouse.this);
                List<Address> addresses = null;
                try {
                    addresses = geo.getFromLocationName(addressToSend, 1);
                }
                catch (IOException e) { e.printStackTrace();
                }
                if(addresses.size()>0) {
                    Address address = addresses.get(0);
                    String lat = Double.toString(address.getLatitude());
                    String lng = Double.toString(address.getLongitude());
                    house.setMapX(lat);
                    house.setMapY(lng);
                }

                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String houseString = null;
                try {
                    houseString = ow.writeValueAsString(house);
                }catch (JsonProcessingException e){}

                JSONObject request = new JSONObject();

                try {
                    request.put("house",houseString);
                    if (image != null) {
                        request.put("houseImage", encodeTobase64(image));
                        Picasso.get().invalidate(house.getHouseImg());
                        Picasso.get().load(house.getHouseImg()).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(houseRef);
                    }
                    else{ request.put("houseImage","no");}
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody formBody = RequestBody.create(JSON, request.toString());


                URLhelper urlResourceHelper =
                        new URLhelper("updatehouse", formBody, new URLhelper.onFinishListener() {

                            @Override
                            public void onFinishSuccess(JSONObject obj) {

                                try {
                                    String msg = obj.getString("message");
                                    Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                }catch (JSONException e){}

                                Intent intent = new Intent(EditHouse.this,EditHouse.class);
                                intent.putExtra("House",house);
                                saveDialog.dismiss();
                                startActivity(intent);
                            }

                            @Override
                            public void onFinishFailed(String msg) {
                                saveDialog.dismiss();
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        });

                urlResourceHelper.execute();


            }
        });
    }

    public void initAddSharedButton(){


        ImageButton button_register = findViewById(R.id.addshared_button);
        button_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                saveDialog = EditHouse.ProgressDialogFragment.newInstance();
                saveDialog.show(getFragmentManager(), "Loading house details... ");

                EditText user_email = findViewById(R.id.input_sharedby);
                JSONObject request = new JSONObject();
                try {
                    request.put("HouseID",house.getHouseID());
                    request.put("UserEmail",user_email.getText().toString());
                    request.put("makeAdmin","0");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody formBody = RequestBody.create(JSON, request.toString());

                URLhelper urlResourceHelper =
                        new URLhelper("adduserforhouse", formBody, new URLhelper.onFinishListener() {

                            @Override
                            public void onFinishSuccess(JSONObject obj) {

                                try {
                                    String msg = obj.getString("message");
                                    Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                }catch (JSONException e){}

                                    Intent intent = new Intent(EditHouse.this,EditHouse.class);
                                    intent.putExtra("House",house);
                                    saveDialog.dismiss();
                                    startActivity(intent);
                            }

                            @Override
                            public void onFinishFailed(String msg) {
                                saveDialog.dismiss();
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        });

                urlResourceHelper.execute();


            }
        });
    }


    public void initFetchData()  throws JSONException{


        JSONObject request = new JSONObject();

        request.put("HouseID",house.getHouseID());

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody formBody = RequestBody.create(JSON, request.toString());

        URLhelper urlResourceHelper =
                new URLhelper("shareduserlist", formBody, new URLhelper.onFinishListener() {

                    @Override
                    public void onFinishSuccess(JSONObject obj) {

                        try {
                            JSONArray namesArray = obj.getJSONArray("Name");
                            Type listType = new TypeToken<ArrayList<String>>(){}.getType();
                            names = new Gson().fromJson(namesArray.toString(), listType);
                            email = obj.getString("UserEmail");
                            initAdapter();
                            saveDialog.dismiss();
                        } catch (JSONException e) {
                            Log.i("Test", e.toString());
                            Toast.makeText(getApplicationContext(), "Error fetching shared users details", Toast.LENGTH_SHORT);
                        }

                    }

                    @Override
                    public void onFinishFailed(String msg) {
                        saveDialog.dismiss();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });

        urlResourceHelper.execute();

    }

    public void initAdapter() {

        try{
            adapter = new SharedUserAdapter(EditHouse.this,names,house,email,saveDialog);
            setListAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error retrieving Houses", Toast.LENGTH_LONG).show();
        }

    }


    public static class ProgressDialogFragment extends DialogFragment {

        public static EditHouse.ProgressDialogFragment newInstance() {
            return new EditHouse.ProgressDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading...");
            dialog.setIndeterminate(true);

            return dialog;
        }
    }

    public void initAddImageButton(){

        Button imagePhoto = findViewById(R.id.input_button_photo);

        imagePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();

                Uri data = Uri.parse(pictureDirectoryPath);
                photoPickerIntent.setDataAndType(data,"image/*");

                startActivityForResult(photoPickerIntent,IMAGE_GALLERY_REQUEST);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_GALLERY_REQUEST){
                Uri imageUri = data.getData();
                InputStream inputStream;

                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                    image = BitmapFactory.decodeStream(inputStream);
                    houseRef.setImageBitmap(image);
                    houseRef.setBackgroundResource(android.R.color.transparent);
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),"Unable to open Image",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public String encodeTobase64(Bitmap image){
        Bitmap immagex = image;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        int options = 90;
        while (baos.toByteArray().length / 1024 > 500) {
            baos.reset();
            immagex.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;//Every time reduced by 10
        }

        byte[] b = baos.toByteArray();

        String imageEncoded = Base64.encodeToString(b, Base64.URL_SAFE);

        return imageEncoded;
    }
}
