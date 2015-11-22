package hk.ust.cse.hunkim.questionroom.question;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

import hk.ust.cse.hunkim.questionroom.MainActivity;

/**
 * Created by ReynaldiWijaya on 22/11/15.
 */
public class setImage extends AsyncTask<String,Void,Bitmap> {

    private ImageView bmImage;

    public setImage(ImageView bmImage){

        this.bmImage = bmImage;
    }
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            Log.v("The link",urldisplay);
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;


    }

    protected void onPostExecute(Bitmap result){

        this.bmImage.setImageBitmap(result);
    }
}
