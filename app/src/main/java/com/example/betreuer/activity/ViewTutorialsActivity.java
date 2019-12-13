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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.betreuer.helper.IOHelper;
import com.example.betreuer.R;

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
      //  registerForContextMenu(listView);
        listView.setAdapter(adapter);
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openTutorial(view);
            }
        });
        UIHelper.showErrorDialog(this,"LongClick auf Listenelemente für löschen/editieren."); */
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


  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tutorial_listview_menu, menu);
        return true;
    }*/

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tutorial_listview_menu, menu);
    }
/*

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
*/

    //ab hier neu!!

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
            final View row = layoutInflater.inflate(R.layout.row, parent, false);
            row.setTag(titles[position]);
            ImageView thumbnail = row.findViewById(R.id.imageView);
            TextView title = row.findViewById(R.id.textView1);
            final Button edButton = row.findViewById(R.id.edButton);
            thumbnail.setImageBitmap(images[position]);
            title.setText(titles[position]);

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTutorial(row);
                }
            });
       //     registerForContextMenu(edButton);
           edButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, edButton);
                    popup.inflate(R.menu.tutorial_listview_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    //handle menu1 click
                                    break;
                                case R.id.delete:
                                    //handle menu2 click
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });

           /*
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View customLayout = getLayoutInflater().inflate(R.layout.menu_layout, null);
            builder.setView(customLayout);

            AlertDialog dialog = builder.create();
            dialog.show();*/
            return row;
        }

    }
}
