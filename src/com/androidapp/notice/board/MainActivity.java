package com.androidapp.notice.board;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity implements OnItemClickListener 
{	
	private static String rssFeed;
	List<Item> arrayOfList;
	ListView listView;
	int notice_id;
	String user_id;
	String[] arrayofString;
	String response;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Resources rsc=getResources();
		listView = (ListView) findViewById(R.id.listview);
		listView.setOnItemClickListener(this);
		Bundle b=getIntent().getExtras();
		user_id= b.getString("user_id");
		rssFeed=  rsc.getString(R.string.root_url)+"get_data.php?"+"id="+user_id;
		new NoticeTask().execute();
		
		if (Utils.isNetworkAvailable(MainActivity.this)) 
		{
			new MyTask().execute(rssFeed);
		} 
		else 
		{
			Utils.showToast("No Network Connection!!!", getApplicationContext());			
		}
	}
	String getRead()
	{
		HttpClient client = new DefaultHttpClient();  
		Resources r=getResources();
		String getURL = r.getString(R.string.root_url)+"get_read_notices.php"+"?user_id="+user_id;
	    HttpGet get = new HttpGet(getURL);
	    HttpResponse responseGet;
		try 
		{
			responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();  
		    if (resEntityGet != null) 
		    {  
		    	response = EntityUtils.toString(resEntityGet);
		    	
		    }
		} 
		catch (ClientProtocolException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return response;
		
	}
	
	class NoticeTask extends AsyncTask<Void, Void, String>
	{
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setCancelable(false);
			pDialog.setProgressStyle(0);
			pDialog.setMessage("Please Wait");
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(Void...params)
		{
			return getRead();
		}
		
		protected void onPostExecute(String result) 
		{
			super.onPostExecute(result);
			arrayofString=result.split(",");
			if (null != pDialog && pDialog.isShowing()) 
			{
				pDialog.dismiss();
			}
		}
	}
	
	class MyTask extends AsyncTask<String, Void, Void> 
	{
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setCancelable(false);
			pDialog.setProgressStyle(0);
			pDialog.setMessage("Please Wait");
			pDialog.show();
		}

		@Override
		protected Void doInBackground(String... params)
		{
			arrayOfList = new NamesParser().getData(params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);

			if (null != pDialog && pDialog.isShowing()) 
			{
				pDialog.dismiss();
			}

			if (null == arrayOfList || arrayOfList.size() == 0) 
			{
				Utils.showToast("No data found from web!!!", getApplicationContext());
				MainActivity.this.finish();
			}
			else 
			{
				setAdapterToListview();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) 
	{
		Item item = arrayOfList.get(position);
		Intent intent = new Intent(MainActivity.this, DetailActivity.class);
		intent.putExtra("url", item.getLink());
		intent.putExtra("title", item.getTitle());
		intent.putExtra("desc", item.getDesc());
		intent.putExtra("date", item.getPubdate());
		intent.putExtra("venue", item.getVenue());
		notice_id=Integer.parseInt(item.getId());
		try 
		{
			HttpClient client = new DefaultHttpClient();  
			Resources r=getResources();
			String getURL = r.getString(R.string.root_url)+"notice_read.php"+"?uid="+user_id+"&nid="+notice_id;
			HttpGet get = new HttpGet(getURL);
			HttpResponse responseGet = client.execute(get);
			if(responseGet.toString().equals(null))
			{
				Utils.showToast("Unknown Error", getApplicationContext());
			}
		} 
		catch (ClientProtocolException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startActivity(intent);
	}

	public void setAdapterToListview() 
	{
		NewsRowAdapter objAdapter = new NewsRowAdapter(MainActivity.this,
				R.layout.row, arrayOfList,arrayofString);
		listView.setAdapter(objAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		 switch (item.getItemId())
		 {
		 	case R.id.menu_settings:
		 		onRestart();
		 		return true;
		 }
		return true;
	 }
	@Override
	public void onRestart()
	{
		super.onRestart();
		new MyTask().execute(rssFeed);
		new NoticeTask().execute();
		setAdapterToListview();
	}
}
