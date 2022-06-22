package com.example.expensemanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.URL;

public class ImageDownloader extends AsyncTask<String,Void, Bitmap> {
    ImageView imageView;

    public ImageDownloader(ImageView imageView){
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String imageUrl=urls[0];
        Bitmap bitmap=null;
        try{
            InputStream is = new URL(imageUrl).openStream();
            bitmap = BitmapFactory.decodeStream(is);
        }catch(Exception e){ // Catch the download exception
            e.printStackTrace();
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result){

        imageView.setImageBitmap(result);
    }
}
