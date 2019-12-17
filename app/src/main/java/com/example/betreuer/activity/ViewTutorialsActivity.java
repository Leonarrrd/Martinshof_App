package com.example.betreuer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.betreuer.R;
import com.example.betreuer.helper.ShareHelper;
import com.example.betreuer.service.ControllerService;

public class ViewTutorialsActivity extends AppCompatActivity {

    private ListView listView;
    private ListAdapter adapter;
    private ControllerService cs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView = findViewById(R.id.listview);
        setContentView(R.layout.activity_view_tutorials);
        cs = ControllerService.getInstance();
    }

    @Override
    public void onResume(){
        super.onResume();
        String[] titles = cs.getTitles().toArray(new String[cs.getTitles().size()]);
        Bitmap[] thumbnails = cs.getThumbnails().toArray(new Bitmap[cs.getThumbnails().size()]);
        adapter = new ListAdapter(this, titles, thumbnails);
        listView = findViewById(R.id.listview);
        registerForContextMenu(listView);
        listView.setAdapter(adapter);
    }

    private void openTutorial(View view){
        Intent intent = new Intent(this, ViewTutorialActivity.class);
        String tagString = (String) view.getTag();
        intent.putExtra("tutorial", tagString);
        intent.putExtra("title", ((TextView)view.findViewById(R.id.textView1)).getText());
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tutorials_listview_menu, menu);
    }

    class ListAdapter extends ArrayAdapter<String> {
        Context context;
        String[] titles;
        Bitmap[] thumbnails;

        ListAdapter(Context c, String[] titles, Bitmap[] images) {
            super(c, R.layout.row, R.id.textView1, titles);
            this.context = c;
            this.titles = titles;
            this.thumbnails = images;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View row = layoutInflater.inflate(R.layout.row, parent, false);
            row.setTag(titles[position]);
            ImageView thumbnail = row.findViewById(R.id.imageView);
            final TextView title = row.findViewById(R.id.textView1);
            final Button edButton = row.findViewById(R.id.edButton);
            thumbnail.setImageBitmap(thumbnails[position]);
            title.setText(titles[position]);

            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTutorial(row);
                }
            });

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTutorial(row);
                }
            });
            edButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, edButton);
                    popup.inflate(R.menu.tutorials_listview_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    Intent intent = new Intent(context, CreateTutorialActivity.class);
                                    intent.putExtra("tutorialName", titles[position]);
                                    startActivity(intent);
                                    break;
                                case R.id.delete:
                                    cs.deleteTutorial(titles[position]);
                                    // TODO: this doesn't work:
                                    // adapter.notifyDataSetChanged();
                                    // TODO: and this is the shitty workaround for it
                                    ((Activity)context).recreate();
                                    break;
                                case R.id.share:
                                    ShareHelper.shareTutorial(getContext(), titles[position]);
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });
            return row;
        }

    }
}
