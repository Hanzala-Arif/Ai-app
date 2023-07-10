package com.example.aiapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aiapp.ml.LiteModelAiyVisionClassifierBirdsV13;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
Button b1;
TextView t1;
ImageView img1;
ActivityResultLauncher<Intent> activityResultLauncher;
ActivityResultLauncher<String> mgetcontent;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1=(Button) findViewById(R.id.b1);
        t1=(TextView) findViewById(R.id.t1);
        img1=(ImageView) findViewById(R.id.img1);
        mgetcontent=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        Bitmap imgbitmap=null;
                        try{
                            imgbitmap = UriToBitmap(result);
                        }
                        catch(Exception e){
e.printStackTrace();
                        }
                        img1.setImageBitmap(imgbitmap);
                        outputGenerator(imgbitmap);
                    }
                }

        );


    }
    private void outputGenerator(Bitmap imgbitmap)
    {
        try {
            LiteModelAiyVisionClassifierBirdsV13 model = LiteModelAiyVisionClassifierBirdsV13.newInstance(MainActivity.this);

            // Creates inputs for reference.
            TensorImage image = TensorImage.fromBitmap(imgbitmap);

            // Runs model inference and gets result.
            LiteModelAiyVisionClassifierBirdsV13.Outputs outputs = model.process(image);
            List<Category> probability = outputs.getProbabilityAsCategoryList();

            int index=0;
            float max=probability.get(0).getScore();
            for(int i=0;i<probability.size();i++){

if(max<probability.get(i).getScore()){

    max=probability.get(i).getScore();
    index=i;
}

            }
            Category output=probability.get(index);
            t1.setText(output.getLabel());






            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

    }

    private Bitmap UriToBitmap(Uri result) throws IOException {
        return MediaStore.Images.Media.getBitmap(this.getContentResolver(),result);
    }

    public void click(View view) {
        mgetcontent.launch("image/*");
    }

    public void tclick(View view) {
        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/search?q="+t1.getText().toString()));
        startActivity(intent);
    }
}