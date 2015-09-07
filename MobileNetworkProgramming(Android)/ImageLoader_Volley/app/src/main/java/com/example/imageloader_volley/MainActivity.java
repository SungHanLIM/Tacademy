package com.example.imageloader_volley;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ImageLoader-Sample";
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

    RequestQueue queue;
    public ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        queue = Volley.newRequestQueue(this);

        PaintImageCache imageCache = new PaintImageCache(5 * 1024 * 1024); // 5mb
        imageLoader = new ImageLoader(queue, imageCache);


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

            if ( view == null ) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.paint_cell_layout, viewGroup, false);
            }

            NetworkImageView imageView = (NetworkImageView) view.findViewById(R.id.imageView);
            // TODO : 리스트에 이미지 로딩
            String url = (String) getItem(i);
            imageView.setImageUrl(url, imageLoader);

            return view;
        }
    };

    class PaintImageCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {
        public PaintImageCache(int maxSize) {
            super(maxSize);
        }

        @Override
        public Bitmap getBitmap(String url) {
            return this.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            this.put(url, bitmap);
        }
    }
}