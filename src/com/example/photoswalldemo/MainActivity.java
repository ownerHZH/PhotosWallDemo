package com.example.photoswalldemo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

/**
 * 照片墙主活动，使用GridView展示照片墙。
 * 
 * @author guolin
 */
public class MainActivity extends Activity {

	/**
	 * 用于展示照片墙的GridView
	 */
	private GridView mPhotoWall;

	/**
	 * GridView的适配器
	 */
	private PhotoWallAdapter mAdapter;

	private int mImageThumbSize;
	private int mImageThumbSpacing;
	
	private final static Gson gson=new GsonBuilder().create();
	public static final Type photo_list_type=(Type) new TypeToken<List<Data>>(){}.getType();
    public String  resultString="";
    
    public List<String> images=new ArrayList<String>();
    Button pre,next;
    int pageNo=0,pageSize=30;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mImageThumbSize = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_size);
		mImageThumbSpacing = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_spacing);
		mPhotoWall = (GridView) findViewById(R.id.photo_wall);
		pre=(Button) findViewById(R.id.buttonPre);
		next=(Button) findViewById(R.id.buttonNext);
		
		pre.setOnClickListener(l);
		next.setOnClickListener(l);
		//getJsonData();
		downloadUrlToStream(pageNo,pageSize);
				
		mAdapter = new PhotoWallAdapter(this,images,
				mPhotoWall);
		mPhotoWall.setAdapter(mAdapter);
		mPhotoWall.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					
					@Override
					public void onGlobalLayout() {
						final int numColumns = (int) Math.floor(mPhotoWall
								.getWidth()
								/ (mImageThumbSize + mImageThumbSpacing));
						if (numColumns > 0) {
							int columnWidth = (mPhotoWall.getWidth() / numColumns)
									- mImageThumbSpacing;
							mAdapter.setItemHeight(columnWidth);
							mPhotoWall.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
						}
					}
				});
	}	
	
	private OnClickListener l=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.buttonPre:
				pageNo-=30;
				if(pageNo>=0)
				{
					downloadUrlToStream(pageNo,pageSize);
				}				
				break;
            case R.id.buttonNext:
            	pageNo+=30;
            	downloadUrlToStream(pageNo,pageSize);
				break;

			default:
				break;
			}
		}
	};
	/*public void getJsonData()
	{
		final HttpClient client = Client.getInstance();
		final HttpPost httpPost = new HttpPost("http://image.baidu.com/channel/listjson");
		new Thread(){

			@Override
			public void run() {
				try {           
		            List<NameValuePair> param = new ArrayList<NameValuePair>();
		            param.add(new BasicNameValuePair("pn", 0+""));
		            param.add(new BasicNameValuePair("rn", 30+""));
		            param.add(new BasicNameValuePair("tag1", "%E7%BE%8E%E5%A5%B3"));
		            param.add(new BasicNameValuePair("tag2", "%E5%85%A8%E9%83%A8"));
		            //param.add(new BasicNameValuePair("ftags", "校花"));
		            param.add(new BasicNameValuePair("ie", "utf8"));
		            param.add(new BasicNameValuePair("code", UUID.randomUUID()+""));
		            httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
		            HttpResponse response = client.execute(httpPost);
		            int code = response.getStatusLine().getStatusCode();
		            if (code == 200) {
		                InputStream is = response.getEntity().getContent();
		                ByteArrayOutputStream baos = new ByteArrayOutputStream();
		                int len = 0;
		                byte[] buffer = new byte[1024];
		                while ((len = is.read(buffer)) != -1) {
		                    baos.write(buffer, 0, len);
		                }
		                is.close();
		                baos.close();
		                byte[] result = baos.toByteArray();
		                resultString = new String(result, "utf-8");
		                h.sendEmptyMessage(0x11);
		            } else {
		                resultString="[]";
		                h.sendEmptyMessage(0x11);
		            }
		        } catch (Exception e) {		
		        	e.printStackTrace();
		        	resultString="[]";
		        	h.sendEmptyMessage(0x11);
		        }
				super.run();
			}}.start();				
	}*/
	
	private void downloadUrlToStream(int pageNo,int pageSize) {
		final String urlString="http://image.baidu.com/channel/listjson?pn="+pageNo+"&rn="+pageSize+"&tag1=%E7%BE%8E%E5%A5%B3&tag2=%E5%85%A8%E9%83%A8&ie=utf8";
		
		new Thread(){

			@Override
			public void run() {
				HttpURLConnection urlConnection = null;
				ByteArrayOutputStream out = null;
				BufferedInputStream in = null;
				//OutputStream outputStream = null;
				try {
					final URL url = new URL(urlString);
					urlConnection = (HttpURLConnection) url.openConnection();
					in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
					out = new ByteArrayOutputStream();
					int b=0;
					byte[] buffer = new byte[1024];
					while ((b = in.read(buffer)) != -1) {
						out.write(buffer,0,b);
					}
					byte[] result = out.toByteArray();
		            resultString = new String(result, "utf-8");
		            h.sendEmptyMessage(0x11);
				} catch (final IOException e) {
					e.printStackTrace();
				} finally {
					if (urlConnection != null) {
						urlConnection.disconnect();
					}
					try {
						if (out != null) {
							out.close();
						}
						if (in != null) {
							in.close();
						}
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
				super.run();
			}}.start();
		
	}
	
	private Handler h=new Handler(){

		@Override
		public void handleMessage(Message msg) {
            if(msg.what==0x11)
            {
            	parseJson();
            }
			super.handleMessage(msg);
		}
		
	};
	
	public void parseJson()
	{	
		//Log.e("Stringss----", resultString);
		Entity entity=gson.fromJson(resultString, Entity.class);
		String dataString=gson.toJson(entity.getData());
		//Log.e("String----", dataString);
		List<Data> datas=gson.fromJson(dataString, photo_list_type);
		images.clear();
		for(Data data:datas)
		{
			images.add(data.getImage_url());
		}
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mAdapter.fluchCache();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出程序时结束所有的下载任务
		mAdapter.cancelAllTasks();
	}

}