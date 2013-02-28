package com.androidapp.notice.board;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.androidapp.notice.board.LoginActivity.YoTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class UserSignup extends Activity{

	EditText uname;
	EditText passwd;
	Button submit;
	String response;
	Spinner spinner1;
	String tag[]={"eni","eee","cs","swd"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		uname= (EditText) findViewById(R.id.nuser);
		passwd= (EditText) findViewById(R.id.npasswd);
		submit= (Button) findViewById(R.id.nbutton);
		spinner1 = (Spinner)findViewById(R.id.spinner1);
		spinner1.setPrompt("Select Tag");
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
							new SignTask().execute();
						else
							Utils.showToast("Invalid Email", getApplicationContext());
					}  
				});	
	}
	
	class SignTask extends AsyncTask<String, Void, String>
	{
		ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(UserSignup.this);
			pDialog.setCancelable(false);
			pDialog.setProgressStyle(0);
			pDialog.setMessage("Please Wait");
			pDialog.show();
		}

		protected String doInBackground(String... params)
		{
			String resp = trysignup();
			return resp;
		}
		
		protected void onPostExecute(String response)
		{
			super.onPostExecute(response);
			if (null != pDialog && pDialog.isShowing()) 
			{
				pDialog.dismiss();
			}
		    if(response.compareTo("1")==0)
		    {
		    	Utils.showToast("Registration Successful", getApplicationContext());
				UserSignup.this.finish();
		    }
		    else
		    {
		    	Utils.showToast("Invalid Inputs", getApplicationContext());
		    }			
		}
	}
	
	String trysignup()
	{
		String user_name = uname.getText().toString();
		String password= passwd.getText().toString();
		String tag=String.valueOf(spinner1.getSelectedItem());
		try
		{
			user_name = URLEncoder.encode(user_name, "utf-8");
			password = URLEncoder.encode(password, "utf-8");
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("uname", user_name));
			nameValuePairs.add(new BasicNameValuePair("pwd", password));
			nameValuePairs.add(new BasicNameValuePair("tag", tag));
			HttpClient httpclient = new DefaultHttpClient();
			Resources r=getResources();
			HttpPost httppost = new HttpPost(r.getString(R.string.root_url)+"new_user.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse httpResponse = httpclient.execute(httppost);
			HttpEntity resEntityGet = httpResponse.getEntity();  
			if (resEntityGet != null) 
			{  
				response = EntityUtils.toString(resEntityGet);
			}
		} 
        catch(Exception e)
        {
        	Utils.showToast("An error has occured", getApplicationContext());
        }
		return response;
	}
	
}
