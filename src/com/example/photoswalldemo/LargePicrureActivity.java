package com.example.photoswalldemo;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import libcore.io.DiskLruCache;
import libcore.io.DiskLruCache.Snapshot;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

public class LargePicrureActivity extends Activity {

	ImageView imageView;
	FileDescriptor fileDescriptor = null;
	FileInputStream fileInputStream = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String url=getIntent().getStringExtra("url");
		url=url==null?"":url;
		setContentView(R.layout.activity_large_picrure);
		imageView=(ImageView) findViewById(R.id.imageView1);
		Bitmap bitmap=PhotoWallAdapter.mMemoryCache.get(url);
		if(bitmap==null)
		{
			try {
				Snapshot snapShot=PhotoWallAdapter.mDiskLruCache.get(hashKeyForDisk(url));
				if (snapShot != null) {
					fileInputStream = (FileInputStream) snapShot.getInputStream(0);
					fileDescriptor = fileInputStream.getFD();
				}
				if (fileDescriptor != null) {
					bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		imageView.setImageBitmap(bitmap);
		
	}
	
	/**
	 * 使用MD5算法对传入的key进行加密并返回。
	 */
	public String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}
	
	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_large_picrure, menu);
		return false;
	}

}
