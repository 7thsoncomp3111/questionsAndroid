package hk.ust.cse.hunkim.questionroom.question;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.util.Arrays;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.MainActivity;

/**
 * Created by ReynaldiWijaya on 20/11/15.
 */
public class uploadPicture extends AsyncTask<String, Void, String> {

    private Exception exception;
    private String filepath;
    private String aws_id;
    private String aws_pass;
    private String uploadresult;
    private static Permission Read;
    private MainActivity myActivity;
    private Context mContext;

    public uploadPicture(Context myContext){
        mContext = myContext;
        myActivity = (MainActivity) mContext;
    }

    protected String doInBackground(String... FilePath) {
        try {

            myActivity.setPickerStart();
            filepath = FilePath[0];

            uploadresult = sendPicture();
            if(uploadresult == "Error Executing") {
                return "Error executing, existing file found";
            } else {

                return uploadresult;

            }
        } catch (Exception e) {
            this.exception = e;
            e.printStackTrace();
            return "Error executing";
        }


    }

    protected void onPostExecute(String result){

        myActivity.setUploadedPirctureLink(result);
    }

    public String checkName(AmazonS3Client sClient, String keyname){

        try {
            S3Object checkexisting = sClient.getObject("comp3111images", keyname);
            keyname = "1-" + keyname;
            return checkName(sClient,keyname);
        } catch (Exception e){
            return keyname;
        }

    }

    public String sendBacktheLink(){
        return uploadresult;
    }

    public String sendPicture(){

         try {
             aws_id = "AKIAIZEFM6CFYRMWAWTQ";
             aws_pass = "aqav3C2/uuLP3syDRGmERaqytcAjQNcDb4VPe+cw";
             AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(aws_id, aws_pass));
             String[] filepath2 = filepath.split("/");
             int filepathsize = filepath2.length;
             String keyname = filepath2[filepathsize - 1];
             keyname = checkName(s3Client, keyname);

             AccessControlList acl = new AccessControlList();
             acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);


             PutObjectRequest por = new PutObjectRequest("comp3111images", keyname, new java.io.File(filepath));
             s3Client.putObject(por.withAccessControlList(acl));

             String newMsg = "https://s3-ap-southeast-1.amazonaws.com/comp3111images/"+keyname;

             return newMsg;

         } catch (Exception e){
             e.printStackTrace();
             return "Error Executing";
         }

    }
}
