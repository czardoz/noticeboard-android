package com.androidapp.notice.board;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity
{
	EditText uname;
	EditText passwd;
	Button submit;
	Button newusr;
	String response;
	
	public void showToast(String msg) 
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		if(!Utils.isNetworkAvailable(LoginActivity.this))
      	{
      		showToast("No network connection");
      		LoginActivity.this.finish();
      	}

		uname= (EditText) findViewById(R.id.editText1);
		passwd= (EditText) findViewById(R.id.editText2);
		submit= (Button) findViewById(R.id.button1);
		newusr = (Button) findViewById(R.id.button2);
		newusr.setOnClickListener(
			new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					// TODO Auto-generated method stub
					Intent i=new Intent(LoginActivity.this, UserSignup.class);
					startActivity(i);
				}
			});
		
		submit.setOnClickListener(
			new OnClickListener() 
			{	
				@Override
				public void onClick(View v) 
				{
					// TODO Auto-generated method stub
						Pattern pattern;
						Matcher matcher;
						String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
						pattern = Pattern.compile(EMAIL_PATTERN);
						matcher = pattern.matcher(uname.getText().toString());
						if(matcher.matches())
							new YoTask().execute();
						else
							Utils.showToast("Invalid Email", getApplicationContext());
				}  
			});
	}
	
	
	
	String trylogin()
	{
		
		String user_name = uname.getText().toString();
        String password= passwd.getText().toString();        
		try
        {
             
        	HttpClient client = new DefaultHttpClient();  
	        user_name = URLEncoder.encode(user_name, "utf-8");
	        password = URLEncoder.encode(password, "utf-8");
	        Resources r=getResources();
		    String getURL = r.getString(R.string.root_url)+"login_user.php"+"?uname="+user_name+"&pwd="+password;
		    HttpGet get = new HttpGet(getURL);
		    HttpResponse responseGet = client.execute(get);  
		    HttpEntity resEntityGet = responseGet.getEntity();  
		    if (resEntityGet != null) 
		    {  
		    	response = EntityUtils.toString(resEntityGet);
		    }
	    } 
        catch(Exception e)
        {
        	Toast.makeText(getApplicationContext(), "Invalid Exception", Toast.LENGTH_LONG).show();
        }
		return response;
	}
	
	class YoTask extends AsyncTask<String, Void, String>
	{
		ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setCancelable(false);
			pDialog.setProgressStyle(0);
			pDialog.setMessage("Please Wait");
			pDialog.show();
		}
		
		protected String doInBackground(String... params)
		{
			String resp = trylogin();
			return resp;
		}
		
		protected void onPostExecute(String response)
		{
			super.onPostExecute(response);
			if (null != pDialog && pDialog.isShowing()) 
			{
				pDialog.dismiss();
			}
			String[] res=response.split(",");
			response=res[0];
			String id=res[1];
		    if(response.compareTo("1")==0)
		    {
	        	Intent i=new Intent(LoginActivity.this, MainActivity.class);
	        	i.putExtra("user_id", id);
	        	startActivity(i);
	        }
	        else if(response.compareTo("2")==0)
	        {
	        	Intent i=new Intent(LoginActivity.this, AdminActivity.class);
	        	i.putExtra("user_id", id);
	        	startActivity(i);
		        	
	        }
	        else
	        	Toast.makeText(getApplicationContext(), "Invalid User ID or Password", Toast.LENGTH_LONG).show();
		}	
	}
}
