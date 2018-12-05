package com.vertin_go.topsiteapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Menu_PartageActivity extends AppCompatActivity {

    private ArrayList<String> data = new ArrayList<String>();
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent = new Intent(Menu_PartageActivity.this,
                            MainActivity.class);
                    startActivity(intent);

                    break;
                case R.id.navigation_dashboard:
                    Intent intent2 = new Intent(Menu_PartageActivity.this,
                            Menu_Principale_Drawer.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent2);


                    break;
                case R.id.navigation_notifications:
                    Intent intent3 = new Intent(Menu_PartageActivity.this,
                            Notifications.class);
                    startActivity(intent3);

                    break;
            }
            return false;
        }

    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__partage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        data.add("Partager!");
        data.add("Télécharger!");

        ListView lv = (ListView) findViewById(R.id.listview);
        lv.setAdapter(new Menu_PartageActivity.MyListAdaper(this, R.layout.list_item_download2, data));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Menu_PartageActivity.this, "List item was clicked at " + position, Toast.LENGTH_SHORT).show();

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_partage, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_accueil:
                Intent intent=new Intent(Menu_PartageActivity.this,MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_inventaires:
                Intent intent2=new Intent(Menu_PartageActivity.this,Inventaires.class);
                startActivity(intent2);
            /* DO INVENTAIRES */
                return true;
            case R.id.action_stats:
                Intent intent3=new Intent(Menu_PartageActivity.this,Statistiques.class);
                startActivity(intent3);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_notifications:
                Intent intent4=new Intent(Menu_PartageActivity.this,Notifications.class);
                startActivity(intent4);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_menu_principale:
                Intent intent5=new Intent(Menu_PartageActivity.this,Menu_Principale_Drawer.class);
                startActivity(intent5);
            /* DO NOTIFICATIONS */
                return true;
            /* case R.id.action_mes_videos_you_tube:
                Intent intent6=new Intent(Menu_PartageActivity.this,YouTubeListActivity.class);
                startActivity(intent6);*/
            /* DO NOTIFICATIONS */
            //return true;
            case R.id.action_envoyer_et_partager_you_tube:
                Intent intent7=new Intent(Menu_PartageActivity.this,YouTubeListActivity.class);
                startActivity(intent7);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_you_tube_playlist:
                Intent intent8=new Intent(Menu_PartageActivity.this,PlayerControlsDemoActivity.class);
                startActivity(intent8);
            /* DO NOTIFICATIONS */
                return true;
            case R.id.action_you_tube_wall:
                Intent intent9=new Intent(Menu_PartageActivity.this,VideoWallDemoActivity.class);
                startActivity(intent9);
            /* DO NOTIFICATIONS */
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class MyListAdaper extends ArrayAdapter<String> {
        private int layout;
        private List<String> mObjects;
        private MyListAdaper(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Menu_PartageActivity.ViewHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                Menu_PartageActivity.ViewHolder viewHolder = new Menu_PartageActivity.ViewHolder();

                viewHolder.button = (Button) convertView.findViewById(R.id.list_item_download);
                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.list_item_pdf);
                viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_text);

                convertView.setTag(viewHolder);
            }
            mainViewholder = (Menu_PartageActivity.ViewHolder) convertView.getTag();
            mainViewholder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(position==0)
                    {
                        Intent intent = new Intent(Menu_PartageActivity.this,
                                PartageActivity.class);
                        String choix="share";
                        intent.putExtra("choix", choix);
                        startActivity(intent);
                    }
                    else if(position==1)
                    {
                        Intent intent = new Intent(Menu_PartageActivity.this,
                                PartageActivity.class);
                        String choix="download";
                        intent.putExtra("choix", choix);
                        startActivity(intent);

                    }

                }
            });
            mainViewholder.title.setText(getItem(position));

            return convertView;
        }
    }
    public class ViewHolder {

        ImageView thumbnail;
        TextView title;
        Button button;
    }

}
