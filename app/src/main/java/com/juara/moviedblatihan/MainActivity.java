package com.juara.moviedblatihan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.juara.moviedblatihan.model.SuccessPost.SuccessPost;
import com.juara.moviedblatihan.service.APIClient;
import com.juara.moviedblatihan.service.APIInterfacesRest;
import com.juara.moviedblatihan.service.AppUtil;

import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Button btnGallery, btnCamera;
    ImageView gambar;
    File file1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnGallery = findViewById(R.id.btnGallery);
        btnCamera = findViewById(R.id.btnCamera);
        gambar = findViewById(R.id.gambar);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(MainActivity.this)
                        .cameraOnly()
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
       // addMovieDB();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK


             gambar.setImageURI(data.getData());
             file1 = ImagePicker.Companion.getFile(data);
             sendDataWithImage();

        }
    }

    APIInterfacesRest apiInterface;
    ProgressDialog progressDialog;
    public void addMovieDB(){

        apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.show();
        Call<SuccessPost> call3 = apiInterface.addMovieDB("Pocari","10","Drama Horror","Hanif","Desta","XXX","2");
        call3.enqueue(new Callback<SuccessPost>() {
            @Override
            public void onResponse(Call<SuccessPost> call, Response<SuccessPost> response) {
                progressDialog.dismiss();
                SuccessPost data = response.body();
                //Toast.makeText(LoginActivity.this,userList.getToken().toString(),Toast.LENGTH_LONG).show();
                if (data !=null) {

                    Toast.makeText(MainActivity.this,data.getMessage(),Toast.LENGTH_LONG).show();



                }else{

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(MainActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<SuccessPost> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Maaf koneksi bermasalah",Toast.LENGTH_LONG).show();
                call.cancel();
            }
        });




    }






    //send post data with image
    private void sendDataWithImage() {


        apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.show();



        byte[] bImg1 = AppUtil.FiletoByteArray(file1);
        RequestBody requestFile1 = RequestBody.create(MediaType.parse("image/jpeg"),bImg1);
        //  RequestBody requestFile1 = RequestBody.create(MediaType.parse("image/jpeg"), compressCapture(bImg1, 900));
        MultipartBody.Part bodyImg1 =
                MultipartBody.Part.createFormData("image1", file1.getName() + ".jpg", requestFile1);


        apiInterface = APIClient.getClient().create(APIInterfacesRest.class);

//"Pocari","10","Drama Horror","Hanif","Desta","XXX","2"
        Call<SuccessPost> postAdd = apiInterface.sendDataWithImage(

                toRequestBody("KKN Di Desa Penjilat"),
                toRequestBody("10"),
                toRequestBody("Drama"),
                toRequestBody("Hanif"),
                toRequestBody("Dinda"),
                toRequestBody("XYY"),
                toRequestBody("1"),
                bodyImg1);

        postAdd.enqueue(new Callback<SuccessPost>() {
            @Override
            public void onResponse(Call<SuccessPost> call, Response<SuccessPost> response) {

                progressDialog.dismiss();

                SuccessPost responServer = response.body();

                if (responServer != null) {

                    Toast.makeText(MainActivity.this,responServer.getMessage(),Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<SuccessPost> call, Throwable t) {

                progressDialog.show();
                Toast.makeText(MainActivity.this, "Maaf koneksi bermasalah", Toast.LENGTH_LONG).show();
                call.cancel();
            }
        });

    }


    //change string to requestbody
    public RequestBody toRequestBody(String value) {
        if (value == null) {
            value = "";
        }
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }
}
