package com.tacademy.webdataproject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyAdapter extends BaseAdapter {
	private String TAG = "MainActivity";
	private MainActivity context;
	private int layout;
	ArrayList<MyItem> data;
	Dialog dialog;
	
	String dirPath;
	public MyAdapter(MainActivity context, int layout, ArrayList<MyItem> data){
		this.context = context;
		this.layout = layout;
		this.data = data;
		dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tacademy/";
		File f = new File(dirPath);
		if(!f.exists()){
			f.mkdir();
		}
	}
	public void setData(ArrayList<MyItem> data){
		this.data = data;
	}
	public int getCount() {
		return data.size();
	}
	public Object getItem(int position) {
		return data.get(position).getTitle();
	}
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View cView, ViewGroup parent) {
		Log.v(TAG, "view : " + position);
		if(cView == null){
			cView = View.inflate(context, layout, null);			
		}
		final MyItem item = data.get(position);
		TextView tv = (TextView)cView.findViewById(R.id.txtTitle);
		tv.setText(item.getTitle());
		tv = (TextView)cView.findViewById(R.id.txtCount);
		tv.setText(item.getCount());
		
		String price = item.getPrice();
		if(price.equals("0")){
			price = "무료";
		}
		tv = (TextView)cView.findViewById(R.id.txtPrice);
		tv.setText(price);
		
		tv = (TextView)cView.findViewById(R.id.txtcategory);
		tv.setText(item.getCategory());
		
		final ImageView img = (ImageView)cView.findViewById(R.id.img);

		ImageThread trd = null;
		String imgSrc = item.getImage();
		if(imgSrc != null && !imgSrc.equals("")){
			File file = new File (dirPath, "s" + imgSrc);

			// 이미지 저장후 불러오기
			if (file.exists()) {
				Bitmap bitmap = BitmapFactory.decodeFile(dirPath + "s" +imgSrc);
				img.setImageBitmap(bitmap);
			} else {

				trd = new ImageThread(img, imgSrc, position);
				try{
					trd.start();
				}catch(Exception e){}

			}
		}else{

			img.setImageResource(R.drawable.noimage);
		}
		cView.findViewById(R.id.btnOrder).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(context, item.getTitle() + "주문완료", Toast.LENGTH_SHORT).show();
				
			}
		});
		return cView;
	}
	class ImageThread extends Thread {
		String imgSrc;
		ImageView imgFood;
		Bitmap bitmap = null;
		int position = 0;
		
		public ImageThread(ImageView imgFood, String imgSrc, int position) {
			this.imgSrc = imgSrc;
			this.imgFood = imgFood;
			this.position = position;
		}
		
		public void run() {
			
			String url = ServerUtil.SERVER_URL + "/images/" + imgSrc;
			HttpClient client = null;
			HttpGet request = null;
			HttpResponse response = null;
			InputStream is = null;
			int code = 0;

			byte[] arr = null;
			ByteArrayOutputStream baos = null;
			int data = 0;
//			Message msg = handler.obtainMessage();
			
			try {
				yield();
				Log.v(TAG, "");
				client = NetManager1.getHttpClient();
				request = NetManager1.getGet(url);
				response = client.execute(request);
				code = response.getStatusLine().getStatusCode();
				Log.v(TAG, "code: " + code);
				switch(code) {
				case 200 :
					is = response.getEntity().getContent();
					baos = new ByteArrayOutputStream();
					while ((data = is.read()) != -1) {
						baos.write(data);
					}
					
					BitmapFactory.Options options = new BitmapFactory.Options();
	                options.inSampleSize = 1;		// 1/10 ������� ����� ���� �뷮�̳� ũ�Ⱑ �۴� 

					
					arr = baos.toByteArray();
					bitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length, options);
								                 
			        Log.v(TAG, "파일저장경로 : " + dirPath);
			        File cameraDir = new File(dirPath);
			        if( !cameraDir.exists() ){			
			        	cameraDir.mkdirs();
			        }
			        	         
			        File file = new File(cameraDir, "s" + imgSrc);
			        FileOutputStream out = new FileOutputStream(file);
			        bitmap.compress(CompressFormat.JPEG, 80, out);
					arr = baos.toByteArray();
					bitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length, options);
					break;
				default :
					break;
				}
				context.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						imgFood.setImageBitmap(bitmap);
						Log.v(TAG, "위치: " + position);
					}
				});
				
			} catch (Exception e) {
				Log.v(TAG, "이미지로딩오류: " + e);
			} finally {
				try {
					if (baos != null) {
						baos.close();
					}
				} catch (Exception e) {}
			
				try {
					if (is != null) {
						is.close();
					}
				} catch (Exception e) {}
			}

		}		
	}
}
