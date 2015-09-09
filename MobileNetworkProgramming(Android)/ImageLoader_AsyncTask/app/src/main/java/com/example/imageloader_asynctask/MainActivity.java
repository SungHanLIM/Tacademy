package com.example.imageloader_asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ImageLoader";
    String[] imageList = {
            "http://totallyhistory.com/wp-content/uploads/2011/11/Da_Vinci_The_Last_Supper.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/Raphael-School-of-Athens-small.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/Mona_Lisa_by_Leonardo_da_Vinci_small.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/The_Nightwatch_by_Rembrandt_small.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/Las_Meninas_by_Diego_Velazquez-small.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/Johannes_Vermeer_-_The_Girl_With_The_Pearl_Earring-small.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/A_Sunday_on_La_Grande_Jatte_Georges_Seurat-small.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/Bal-du-moulin-de-la-Galette-200.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/Whistlers-Mother-small.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/Starry_Night_Over_the_Rhone-small.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/Van_Gogh_-_Starry_Night-small.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/The_Scream-small.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/His-Station-and-Four-Aces-CM-Coolidge-small.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/Klimt_-_Der_Kuss-small.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/Magritte-Ceci-Nest-pas-une-Pipe-small.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/American_Gothic-small.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/The_Persistence_of_Memory.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/Picasso-Guernica-small.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/No._5_1948-small.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/Magritte_The-Son-Of-Man-small.jpg",
            "http://totallyhistory.com/wp-content/uploads/2011/11/Great_Wave_off_Kanagawa-small.jpg"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return imageList.length;
        }

        @Override
        public Object getItem(int i) {
            return imageList[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.paint_cell_layout, viewGroup, false);
            }

            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            String urlStr = (String)getItem(i);
            PaintDownloadTask task = new PaintDownloadTask(imageView);
            task.i = i;
            task.execute(urlStr);

            return view;
        }
    };

    class PaintDownloadTask extends AsyncTask<String, Integer, Bitmap> {
        ImageView mImageView;
        int i;
        PaintDownloadTask(ImageView imageView) {
            mImageView = imageView;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "Image Load Started " + i);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                String urlStr = params[0];
                URL url = new URL(urlStr);
                Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }f

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d(TAG, "Image Load Finished " + i);
            mImageView.setImageBitmap(bitmap);
        }
    }
}
