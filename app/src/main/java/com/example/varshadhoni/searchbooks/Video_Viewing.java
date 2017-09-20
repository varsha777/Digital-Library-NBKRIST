package com.example.varshadhoni.searchbooks;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class Video_Viewing extends AppCompatActivity {

String getRowUrl,updatePostUrl,getCommentsUrl;
String branch_code;


    JCVideoPlayerStandard jcVideoPlayerStandard;
    Button post_values;

    TextView rating_view_video;
    TextView views_view_video;
           //UPLOAD RATING AND COMMENTS
    public float rating_upload;
    public String comment_upload;
//DIALOG BOX VALUES
    EditText multiline_comment_dialog;
    RatingBar ratingBar_dialog;
    TextView ratingview;

    String name,rate,views;

    String title,address;
    String responseServer;
    String gmail_user_name;

    String userName,userComment;
    String responseServer2;

    String mobile_IP,video_url;

    ArrayList<HashMap<String, String>> contactList= new ArrayList<>();
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video__viewing);

        jcVideoPlayerStandard = (JCVideoPlayerStandard) findViewById(R.id.videoView);
        rating_view_video=(TextView)findViewById(R.id.rating_video);
        views_view_video=(TextView)findViewById(R.id.Views_video);
        lv = (ListView) findViewById(R.id.comments);


        post_values=(Button)findViewById(R.id.posting);
         post_values.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 AlertDialog.Builder mBuilder=new AlertDialog.Builder(Video_Viewing.this);
                  final View mview=getLayoutInflater().inflate(R.layout.rating_comment_dialog,null);
                 mBuilder.setTitle("Update Your Comment & Rating");

                 mBuilder.setPositiveButton("POST", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         multiline_comment_dialog=(EditText) mview.findViewById(R.id.comment_user);
                         comment_upload= multiline_comment_dialog.getText().toString();

                         ratingBar_dialog=(RatingBar) mview.findViewById(R.id.ratingBar_user);
                            rating_upload=ratingBar_dialog.getRating();
                         ratingview=(TextView) mview.findViewById(R.id.rating_dialog_view);
                            ratingview.setText("Rating :: "+rating_upload);


                         if (comment_upload.isEmpty() && rating_upload!=0.0){
                             Toast.makeText(Video_Viewing.this,"FILL ALL BLANKS",Toast.LENGTH_LONG).show();
                         }else {
                             Send send=new Send();
                             send.execute();

                             GetComments getComments=new GetComments();
                             getComments.execute();
                             Toast.makeText(Video_Viewing.this,String.valueOf(rating_upload),Toast.LENGTH_SHORT).show();
                             dialog.dismiss();
                         }
                     }
                 });
                 mBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         Toast.makeText(Video_Viewing.this,"DIALOG BOX CANCEL",Toast.LENGTH_SHORT).show();
                         dialog.dismiss();
                     }
                 });

                 mBuilder.setView(mview);
                 final AlertDialog dialog=mBuilder.create();
                   dialog.show();
             }
         });

        Bundle Extras=getIntent().getExtras();
        if (Extras!=null)
        {
            name=Extras.getString("name");
            branch_code=Extras.getString("branch_code");
            gmail_user_name=Extras.getString("gmail_name");
            mobile_IP=Extras.getString("mobileIPAddress");
            Log.e("Name-Branch-GmailUserName-Mobie-IP",name+"-"+branch_code+"-"+gmail_user_name+"-"+mobile_IP);
        }


        switch (Integer.parseInt(branch_code))
        {
            case 0: getRowUrl= "http://"+mobile_IP+"/myFiles/cse/GetRow.php";
                    updatePostUrl= "http://"+mobile_IP+"/myFiles/cse/Update.php";
                    getCommentsUrl= "http://"+mobile_IP+"/myFiles/cse/GetComments.php";
                break;
            case 1: getRowUrl= "http://"+mobile_IP+"/myFiles/civil/GetRow.php";
                    updatePostUrl= "http://"+mobile_IP+"/myFiles/civil/Update.php";
                    getCommentsUrl= "http://"+mobile_IP+"/myFiles/civil/GetComments.php";
                break;
            case 2: getRowUrl= "http://"+mobile_IP+"/myFiles/ece/GetRow.php";
                    updatePostUrl= "http://"+mobile_IP+"/myFiles/ece/Update.php";
                    getCommentsUrl= "http://"+mobile_IP+"/myFiles/ece/GetComments.php";
                break;
            case 3: getRowUrl= "http://"+mobile_IP+"/myFiles/eee/GetRow.php";
                    updatePostUrl= "http://"+mobile_IP+"/myFiles/eee/Update.php";
                    getCommentsUrl= "http://"+mobile_IP+"/myFiles/eee/GetComments.php";
                break;
            case 4: getRowUrl= "http://"+mobile_IP+"/myFiles/mec/GetRow.php";
                    updatePostUrl= "http://"+mobile_IP+"/myFiles/mec/Update.php";
                    getCommentsUrl= "http://"+mobile_IP+"/myFiles/mec/GetComments.php";
                break;
        }
        Toast.makeText(Video_Viewing.this,"Video Name="+name,Toast.LENGTH_SHORT).show();
        AsyncT asyncT = new AsyncT();
        asyncT.execute();


        GetComments getComments=new GetComments();
        getComments.execute();
    }

    @Override
    public void onBackPressed() {
        if (jcVideoPlayerStandard.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        jcVideoPlayerStandard.releaseAllVideos();
    }


    class AsyncT extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getRowUrl);

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("vname",name));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                InputStream inputStream = response.getEntity().getContent();
                InputStreamToStringExample str = new InputStreamToStringExample();

                responseServer = str.getStringFromInputStream(inputStream);
                Log.e("response", "response -----" + responseServer);

                JSONObject json = new JSONObject(responseServer);
                JSONArray results = json.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject c = results.getJSONObject(i);
                    title = c.getString("Name");
                    rate=c.getString("Rating");
                    views=c.getString("Views");
                    video_url=c.getString("Address");
                }
            } catch (Exception e) {
                Log.e("sister",e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            video_url=video_url.replaceAll("\\\\","");
            Log.e("video url",video_url);
            jcVideoPlayerStandard.setUp("http://"+mobile_IP+video_url, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, title);
            Toast.makeText(Video_Viewing.this,address,Toast.LENGTH_SHORT).show();
            //jcVideoPlayerStandard.thumbImageView.setImage("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640");
            rating_view_video.setText("Rating ::"+rate+"*");
            views_view_video.setText("Views ::"+views);

        }
    }


    class GetComments extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getCommentsUrl);

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("vname",name));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                InputStream inputStream = response.getEntity().getContent();
                InputStreamToStringExample str = new InputStreamToStringExample();

                responseServer2 = str.getStringFromInputStream(inputStream);
                Log.e("response", "response -----" + responseServer2);

                JSONObject json = new JSONObject(responseServer2);
                JSONArray results = json.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject c = results.getJSONObject(i);
                      userName=c.getString("Uname");
                      userComment=c.getString("Ucomment");

                    HashMap<String, String> contact = new HashMap<>();

                    contact.put("name", userName);
                    contact.put("comment", userComment);

                    contactList.add(contact);
                }
            } catch (Exception e) {
                Log.e("brother", String.valueOf(e));
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ListAdapter adapter = new SimpleAdapter(
                    Video_Viewing.this, contactList,
                    R.layout.comments_session, new String[]{"name", "comment",
                    "mobile"}, new int[]{R.id.username_display,R.id.comment_display});
            lv.setAdapter(adapter);
        }
    }









   class Send extends AsyncTask<String, Void,Long > {

        protected Long doInBackground(String... urls) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(updatePostUrl);

            try {
                // Add your data
                  List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("vname",name));
                nameValuePairs.add(new BasicNameValuePair("Rating",String.valueOf(rating_upload)));
                nameValuePairs.add(new BasicNameValuePair("Comments",comment_upload));
                nameValuePairs.add(new BasicNameValuePair("UserName",gmail_user_name));
                 httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
            } catch (Exception e) {
                Log.e("brother","sister");
                e.printStackTrace();
            }
            return null;

        }
        protected void onProgressUpdate(Integer... progress) {


        }

        protected void onPostExecute(Long result) {
            GetComments getComments=new GetComments();
            getComments.execute();
        }
    }



    public static class InputStreamToStringExample {

        public static void main(String[] args) throws IOException {

            // intilize an InputStream
            InputStream is =
                    new ByteArrayInputStream("file content..blah blah".getBytes());

            String result = getStringFromInputStream(is);

            Log.e("result value server:",result);


        }

        // convert InputStream to String
        private static String getStringFromInputStream(InputStream is) {

            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return sb.toString();
        }

    }
}



