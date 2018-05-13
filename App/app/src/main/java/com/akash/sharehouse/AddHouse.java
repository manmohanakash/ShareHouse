package com.akash.sharehouse;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.akash.sharehouse.helpers.URLhelper;
import com.akash.sharehouse.models.House;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AddHouse extends AppCompatActivity {

    private DialogFragment saveDialog;
    EditText input_name,input_no,input_street,input_city,input_state,input_country,input_landmark,input_description,input_maxWeeks,input_weekRate;
    ImageView image_house;
    private final int IMAGE_GALLERY_REQUEST=20;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_house);

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
        image_house = findViewById(R.id.imageRef);

        initHomeButton();
        initAddButton();
        initResetButton();
        initAddImageButton();
    }

    public void initAddButton(){

        Button addButton = findViewById(R.id.addhouse_button);

        addButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){


                if(input_name.getText().toString().equals("") || input_no.getText().toString().equals("") ||input_street.getText().toString().equals("") || input_city.getText().toString().equals("") ||
                        input_country.getText().toString().equals("") || input_state.getText().toString().equals("") || input_description.getText().toString().equals("") ||
                        input_landmark.getText().toString().equals("") || input_maxWeeks.getText().toString().equals("") || input_weekRate.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please enter all fields",Toast.LENGTH_SHORT).show();
                }
                else{
                    House house = new House();
                    house.setHouseName(input_name.getText().toString());
                    house.setHouseNo(input_no.getText().toString());
                    house.setHouseStreet(input_street.getText().toString());
                    house.setHouseCity(input_city.getText().toString());
                    house.setHouseCountry(input_country.getText().toString());
                    house.setHouseState(input_state.getText().toString());
                    house.setDescription(input_description.getText().toString());
                    house.setLandmark(input_landmark.getText().toString());
                    house.setMaxWeeks(Integer.parseInt(input_maxWeeks.getText().toString()));
                    house.setWeekRate(Integer.parseInt(input_weekRate.getText().toString()));
                    house.setSharedCounter(1);

                    String addressToSend = house.getHouseNo()+","+house.getHouseStreet()+","+house.getHouseCity()
                            +","+house.getHouseState()+","+house.getHouseCountry();

                    Geocoder geo = new Geocoder(AddHouse.this);
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
                    addHouse(house);
                }
            }
        });
    }

    public void initHomeButton() {
        ImageButton button_register = findViewById(R.id.home_icon);
        button_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AddHouse.this, DashBoard.class);
                startActivity(intent);

            }
        });
    }

    public void initResetButton(){

        Button reset = findViewById(R.id.reset_button);

        reset.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                input_name.setText("");
                input_no.setText("");
                input_street.setText("");
                input_city.setText("");
                input_state.setText("");
                input_country.setText("");
                input_landmark.setText("");
                input_description.setText("");
                input_maxWeeks.setText("");
                input_weekRate.setText("");

                Toast.makeText(getApplicationContext(),"Cleared",Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void addHouse(House house){

        saveDialog = AddHouse.ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Adding House");


        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String houseString = null;
        try {
            houseString = ow.writeValueAsString(house);
        }catch (JsonProcessingException e){}

        JSONObject request = new JSONObject();

        try {
            request.put("house",houseString);
            if (image != null){ request.put("houseImage",encodeTobase64(image));}
            else{ request.put("houseImage","default");}
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody formBody = RequestBody.create(JSON, request.toString());

        URLhelper urlResourceHelper =
                new URLhelper("addhouse", formBody, new URLhelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj) {
                        saveDialog.dismiss();
                        Toast.makeText(getApplicationContext(), ("House Added"),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AddHouse.this,DashBoard.class);
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

        public static Register.ProgressDialogFragment newInstance() {
            return new Register.ProgressDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Adding...");
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
                    image_house.setImageBitmap(image);
                    image_house.setBackgroundResource(android.R.color.transparent);
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
