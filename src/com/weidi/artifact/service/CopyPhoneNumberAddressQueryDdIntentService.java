package com.weidi.artifact.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.IntentService;
import android.content.Intent;

public class CopyPhoneNumberAddressQueryDdIntentService extends IntentService {

	public CopyPhoneNumberAddressQueryDdIntentService() {
		super("CopyPhoneNumberAddressQueryDdIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if(intent != null){
			try {
				String fileName = intent.getStringExtra("fileName");
				String path = "/data/data/com.aowin.mobilesafe/databases/"+fileName;
				InputStream is = getAssets().open(fileName);
				File dbFile = getApplicationContext().getDatabasePath(path);
				FileOutputStream fos = new FileOutputStream(dbFile);
				byte[] buffer = new byte[1024];
				int len = 0;
				while((len = is.read(buffer)) != -1){
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
