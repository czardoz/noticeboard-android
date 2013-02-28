package com.androidapp.notice.board;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AdminActivity extends Activity
{
		@Override
		protected void onCreate(Bundle savedInstanceState) 
		{
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.admin);
			Button view = (Button) findViewById(R.id.admin_view);
			Button upload= (Button) findViewById(R.id.admin_upload);
			
			view.setOnClickListener(new OnClickListener() 
			{	
				@Override
				public void onClick(View arg0) 
				{
					// TODO Auto-generated method stub
					Intent i=new Intent(getApplicationContext(), MainActivity.class);
					Bundle b=getIntent().getExtras();
					String id=b.getString("user_id");
					i.putExtra("user_id",id);
					startActivity(i);
				}
			});
			
			upload.setOnClickListener(
					new OnClickListener() 
					{
						@Override
						public void onClick(View v) 
						{
							// TODO Auto-generated method stub
							Intent i=new Intent(getApplicationContext(), UploadData.class);
							
							startActivity(i);
						}
					});
		}
}
