package com.cemalettinaltinas.artbookjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.cemalettinaltinas.artbookjava.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    SQLiteDatabase database;
    ArrayList<Art> artList;
    ArtAdapter artAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        artList = new ArrayList<Art>();
        getData();

        artAdapter = new ArtAdapter(artList,this);
        binding.recyclerView.setAdapter(artAdapter);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //BenimAdapter benimAdapter=new BenimAdapter(this,meyveArrayList);
        //recyclerView.setAdapter(benimAdapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    public void getData() {

        try {
            database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);

            Cursor cursor = database.rawQuery("SELECT * FROM arts", null);
            int nameIx = cursor.getColumnIndex("artname");
            int idIx = cursor.getColumnIndex("id");

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIx);
                System.out.println(name);
                int id = cursor.getInt(idIx);
                Art art = new Art(name,id);
                artList.add(art);
            }
            artAdapter.notifyDataSetChanged();

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.art_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.add_art){
            Intent intent=new Intent(this, ArtActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}