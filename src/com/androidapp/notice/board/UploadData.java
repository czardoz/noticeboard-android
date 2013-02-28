package com.androidapp.notice.board;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
public class UploadData extends Activity{

	private static final int SELECT_FILE1 = 1;
    String selectedPath1 = "NONE";
    TextView tv, res;
    ProgressDialog progressDialog;
    Button b1,b2,b3;
    HttpEntity resEntity;
    EditText title;
    EditText venue;
    EditText date;
    EditText description;
    TextView up_tv;
    TimePicker timePicker;
	Spinner spinner2;

    String time;
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        	
       
        //tv.setText(tv.getText() + selectedPath1 + "," + selectedPath2);
        b1 = (Button)findViewById(R.id.up_image);
        
        b3 = (Button)findViewById(R.id.upload);
        title=(EditText) findViewById(R.id.up_title);
        venue=(EditText) findViewById(R.id.up_venue);
        date=(EditText) findViewById(R.id.up_date);
        up_tv = (TextView) findViewById(R.id.up_tv);
		spinner2 = (Spinner)findViewById(R.id.spinner2);

        timePicker=(TimePicker) findViewById(R.id.up_time);
		spinner2.setPrompt("Select Tag");

        ViewGroup v = (ViewGroup) timePicker.getChildAt(0);
        ViewGroup numberPicker1 = (ViewGroup) v.getChildAt(0);
        ViewGroup numberPicker2 = (ViewGroup) v.getChildAt(1);
        String hours = ((EditText) numberPicker1.getChildAt(1)).getText().toString();
        String mins = ((EditText) numberPicker2.getChildAt(1)).getText().toString();

        time = hours+":"+mins;
        
        description = (EditText) findViewById(R.id.up_desc);
        b1.setOnClickListener(
        	new OnClickListener() 
        	{
        		@Override
        		public void onClick(View v) {
        			openGallery(SELECT_FILE1);
            }
        });
        
        b3.setOnClickListener(
        	new OnClickListener() 
        	{
        		@Override
        		public void onClick(View v) 
        		{
        			if(!(selectedPath1.trim().equalsIgnoreCase("NONE")))
        			{
        				progressDialog = ProgressDialog.show(UploadData.this, "", "Uploading file to server.....", false);
        				Thread thread=new Thread(new Runnable()
        				{
                            public void run()
                            {
                                doFileUpload();
                                runOnUiThread(
                                		new Runnable()
                                		{
                                			public void run() 
                                			{
                                				if(progressDialog.isShowing())
                                					progressDialog.dismiss();
                                			}
                                		});
                            }
        				});
        				thread.start();
	                }
	        		else
	        		{
	        			Toast.makeText(getApplicationContext(),"Please select a file to upload.", Toast.LENGTH_SHORT).show();
	                }
        		}
        	}); 
    }
 
    public void openGallery(int req_code){
 
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select file to upload"), req_code);
   }
 
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
 
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            if (requestCode == SELECT_FILE1)
            {
                selectedPath1 = getPath(selectedImageUri);
                up_tv.setText(selectedPath1);
            }
        }
    }
 
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
 
    private void doFileUpload(){
 
        File file1 = new File(selectedPath1);
        Resources r=getResources();
        String urlString = r.getString(R.string.root_url)+"upload_media_test.php";
        Log.i("BEFORE TRY BLOCK", urlString);
        try
        {
        	Log.i("ENTERED TRY BLOCK", urlString);
        	HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(urlString);
			FileBody bin1 = new FileBody(file1);
			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("uploadedfile1", bin1);
			reqEntity.addPart("user", new StringBody("User"));
			reqEntity.addPart("description", new StringBody(description.getText().toString()));
			reqEntity.addPart("date", new StringBody(date.getText().toString()));
			reqEntity.addPart("venue", new StringBody(venue.getText().toString()));
			reqEntity.addPart("time", new StringBody(time));
			reqEntity.addPart("tag", new StringBody(String.valueOf(spinner2.getSelectedItem())));
			reqEntity.addPart("title", new StringBody(title.getText().toString()));
			post.setEntity(reqEntity);
			HttpResponse response = client.execute(post);
			resEntity = response.getEntity();
			final String response_str = EntityUtils.toString(resEntity);
			if (resEntity != null) 
			{
				Log.i("REsponse NOt NULL", "YEA!!");
				Log.i("RESPONSE",response_str);
				runOnUiThread(
						new Runnable()
						{
							public void run() 
			                {
								try 
								{
			                		Log.i("y00y",response_str);
			                		Toast.makeText(getApplicationContext(),"Upload Complete. Check the server uploads directory.", Toast.LENGTH_LONG).show();
			                	}
			                	catch (Exception e) 
			                	{
			                		Log.i("EXCEPTION OCCURED", "FAAK!");
			                		e.printStackTrace();
			                	}
			                }
						});
			}
		}
		catch (Exception ex)
		{
			Log.i("MAJOR EXCEPTION","FUAK!");
			Log.e("Debug", "error: " + ex.getMessage(), ex);
		}
	}
}