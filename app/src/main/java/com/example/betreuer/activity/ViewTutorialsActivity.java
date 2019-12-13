package com.example.betreuer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.betreuer.R;
import com.example.betreuer.helper.UIHelper;
import com.example.betreuer.helper.IOHelper;

public class ViewTutorialsActivity extends AppCompatActivity {

    private ListView listView;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView = findViewById(R.id.listview);
        setContentView(R.layout.activity_view_tutorials);
        // TODO: Ask Leo about what's wrong with doing this in onCreate()
        Bitmap[] images = IOHelper.getFirstImageOfEachTutorial();
        String[] titles = IOHelper.getTutorialNamesFromStorage().toArray(new String[IOHelper.getTutorialNamesFromStorage().size()]);
        adapter = new ListAdapter(this, titles, images);
        listView = findViewById(R.id.listview);
        registerForContextMenu(listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openTutorial(view);
            }
        });
        UIHelper.showErrorDialog(this,"LongClick auf Listenelemente für löschen/editieren.");
    }

    @Override
    public void onResume(){
        super.onResume();
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
        inflater.inflate(R.menu.tutorial_listview_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String tutorialName = listView.getChildAt(info.position-listView.getFirstVisiblePosition()).getTag().toString();

        switch (item.getItemId()) {
            case R.id.edit:
                Intent intent = new Intent(this, CreateTutorialActivity.class);
                intent.putExtra("tutorialName", tutorialName);
                startActivity(intent);
                return true;
            case R.id.delete:
                IOHelper.deleteDirectory(tutorialName);
                // TODO: this doesn't work:
//                adapter.notifyDataSetChanged();
                // TODO: and this is the shitty workaround for it
                this.recreate();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    class ListAdapter extends ArrayAdapter<String> {
        Context context;
        String[] titles;
        Bitmap[] images;

        ListAdapter(Context c, String[] titles, Bitmap[] images) {
            super(c, R.layout.row, R.id.textView1, titles);
            this.context = c;
            this.titles = titles;
            this.images = images;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);
            row.setTag(titles[position]);
            ImageView thumbnail = row.findViewById(R.id.imageView);
            TextView title = row.findViewById(R.id.textView1);
            thumbnail.setImageBitmap(images[position]);
            title.setText(titles[position]);
            return row;
        }
    }
}
