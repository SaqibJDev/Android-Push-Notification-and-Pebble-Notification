package com.tumitfahrer_pushnotification;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity implements PushNotificationInterface{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PushNotificationHelper helper = new PushNotificationHelper();
		try {
			helper.getRegistrationID(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPlayServiceRegistrationComplete(String id) {
		if(id == null){
			Toast.makeText(this, "There is some problem with registration. Please try again!", Toast.LENGTH_LONG).show();
		}else{
			// Use this id for your purposes
			Toast.makeText(this, id, Toast.LENGTH_LONG).show();
		}
		
	}

}
