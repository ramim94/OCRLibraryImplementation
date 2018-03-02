package com.ideabinbd.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.ideabinbd.myapplication.utils.TessInstaller;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    Button selImg;
    TextView output;
    TessInstaller tInstaller;

    String result="-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager AM = getAssets();

        selImg = (Button) findViewById(R.id.sel_file);
        output = (TextView) findViewById(R.id.txt_output);
        tInstaller = new TessInstaller(AM);
        selImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 5);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        if (requestCode == 5 && resultCode == RESULT_OK
                && null != data) {
            Log.d("resultCode","resultOK select IMage");
            Uri selectedImage = data.getData();
            /*
            String[] filePathColumn = { MediaStore.Images.Media.DATA
            };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            bitmap = BitmapFactory.decodeFile(picturePath);
*/

            CropImage.activity(selectedImage).start(MainActivity.this);

            //result= tInstaller.getResults(bitmap);

            output.setText(result);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== RESULT_OK) {
            Log.d("resultCode","resultOK CropImg");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Bitmap croppedPic= getBitmapFromUri(MainActivity.this,resultUri);
                String outputText= tInstaller.getResults(croppedPic);
                output.setText(outputText);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }else if(resultCode==RESULT_CANCELED){
            Log.d("resultCode","resultCancelled");
        }
    }

    public static Bitmap getBitmapFromUri(Context ctx, Uri intentDataUri) {
        InputStream imageStream;
        final BitmapFactory.Options options = new BitmapFactory.Options();
       // final File selectedTmpFile = getAvatarSelectorTempFile();
        Uri resourceURI=null;

        // In case the tmp file is empty and the intent has sent us a URI of a picture
        if (intentDataUri != null) {
            resourceURI = intentDataUri;
        }

        // create the bitmap
        try {
            imageStream = ctx.getContentResolver().openInputStream(resourceURI);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        final Bitmap picture = BitmapFactory.decodeStream(imageStream, null, options);

        // Clean tmp file


        // scale (and center-crop if necessary) the bitmap
        return picture;
    }
}