package com.example.albumlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    private static final int SORT_BY_ALBUM_NAME = 0;
    private static final int SORT_BY_ACTOR = 1;
    private static final int SORT_BY_YEAR = 2;
    RecyclerView albumsView;
    ImageView imageMenu;
    SearchView searchView;
    FloatingActionButton addButton;
    ArrayList<Album> albums;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Menu navMenu;
    MenuItem goBackItem, sortByAlbumNameItem, sortByActorItem, sortByYearItem, findByAlbumNameItem, findByActorItem, findByYearItem;

    Boolean isFindingByAlbumName = true;
    Boolean isFindingByActor = false;
    Boolean isFindingByYear = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBMain.createInstance(this);
        //DBMain.getInstance().addTestAlbums();
        albums = new ArrayList<Album>();
        drawerLayout = findViewById(R.id.DrawerLayout);
        imageMenu = findViewById(R.id.imageMenu);
        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        addButton = findViewById(R.id.addButton);
        albumsView = findViewById(R.id.RecyclerView);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddAlbumActivity.class);
                startActivity(intent);
            }
        });
        searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //Submit search
                //Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                UpdateAlbums(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //Dynamic search
                if (s.equals("")){
                    UpdateAlbums();
                }else{
                    UpdateAlbums(s);
                }
                return false;
            }
        });
        navigationView = findViewById(R.id.NavigationView);
        navMenu = navigationView.getMenu();
        goBackItem = navMenu.getItem(0);
        goBackItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
        findByAlbumNameItem = navMenu.getItem(2).getSubMenu().getItem(0);
        findByAlbumNameItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                if (menuItem.isChecked()){
                    menuItem.setChecked(false);
                    isFindingByAlbumName = false;
                }
                else {
                    menuItem.setChecked(true);
                    isFindingByAlbumName = true;
                }

                return false;
            }
        });
        findByActorItem = navMenu.getItem(2).getSubMenu().getItem(1);
        findByActorItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                if (menuItem.isChecked()){
                    menuItem.setChecked(false);
                    isFindingByActor = false;
                }
                else {
                    menuItem.setChecked(true);
                    isFindingByActor = true;
                }
                return false;
            }
        });
        findByYearItem = navMenu.getItem(2).getSubMenu().getItem(2);
        findByYearItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                if (menuItem.isChecked()){
                    menuItem.setChecked(false);
                    isFindingByYear = false;
                }
                else {
                    menuItem.setChecked(true);
                    isFindingByYear = true;
                }
                return false;
            }
        });
        sortByAlbumNameItem = navMenu.getItem(1).getSubMenu().getItem(0);
        sortByAlbumNameItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                sortData(SORT_BY_ALBUM_NAME);

                return false;
            }
        });
        sortByActorItem = navMenu.getItem(1).getSubMenu().getItem(1);
        sortByActorItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                sortData(SORT_BY_ACTOR);
                return false;
            }
        });
        sortByYearItem = navMenu.getItem(1).getSubMenu().getItem(2);
        sortByYearItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                sortData(SORT_BY_YEAR);
                return false;
            }
        });
        UpdateAlbums();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateAlbums();
    }

    private void UpdateAlbumView(){
        albumsView.setAdapter(new MyAdapter(this, albums));
        albumsView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void UpdateAlbums() {
        displayData();
        albumsView.setAdapter(new MyAdapter(this, albums));
        albumsView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void UpdateAlbums(String query) {
        findData(query);
        albumsView.setAdapter(new MyAdapter(this, albums));
        albumsView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void displayData() {
        albums.clear();
        Cursor cursor = DBMain.getInstance().selectAllAlbums();
        if (cursor.getCount()==0){
            Toast.makeText(this, "Nothing here!", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            while(cursor.moveToNext()){
                String album_name = cursor.getString(1);
                String actor = cursor.getString(2);
                byte[] picture = cursor.getBlob(3);
                Integer year = cursor.getInt(4);
                Album newAlbum = new Album(album_name, actor, picture, year);
                albums.add(newAlbum);
            }
        }
    }
    private void findData(String query) {
        albums.clear();
        Cursor cursor = null;
        if ((isFindingByAlbumName && isFindingByActor && isFindingByYear) || (!isFindingByAlbumName && !isFindingByActor && !isFindingByYear))
            cursor = DBMain.getInstance().findAlbums(query);
        else if (isFindingByAlbumName && !isFindingByActor && !isFindingByYear)
            cursor = DBMain.getInstance().findAlbumsByName(query);
        else if (!isFindingByAlbumName && isFindingByActor && !isFindingByYear)
            cursor = DBMain.getInstance().findAlbumsByActor(query);
        else if (!isFindingByAlbumName && !isFindingByActor && isFindingByYear)
            cursor = DBMain.getInstance().findAlbumsByYear(query);
        else if (isFindingByAlbumName && isFindingByActor && !isFindingByYear)
            cursor = DBMain.getInstance().findAlbumsByNameAndActor(query);
        else if (isFindingByAlbumName && !isFindingByActor && isFindingByYear)
            cursor = DBMain.getInstance().findAlbumsByNameAndYear(query);
        else if (!isFindingByAlbumName && isFindingByActor && isFindingByYear)
            cursor = DBMain.getInstance().findAlbumsByActorAndYear(query);

        if (cursor.getCount()==0){
            Toast.makeText(this, "Nothing here!", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            while(cursor.moveToNext()){
                String album_name = cursor.getString(1);
                String actor = cursor.getString(2);
                byte[] picture = cursor.getBlob(3);
                Integer year = cursor.getInt(4);
                Album newAlbum = new Album(album_name, actor, picture, year);
                albums.add(newAlbum);
            }
        }
    }
    private void sortData(int sort_filter){
        switch (sort_filter){
            case SORT_BY_ALBUM_NAME:
                sortByAlbumName(albums);
                UpdateAlbumView();
                break;
            case SORT_BY_ACTOR:
                sortByActor(albums);
                UpdateAlbumView();
                break;
            case SORT_BY_YEAR:
                sortByYear(albums);
                UpdateAlbumView();
                break;
        }
    }
    public static void sortByAlbumName(ArrayList<Album> list) {
        list.sort(Comparator.comparing(Album::getAlbumName));
    }
    public static void sortByActor(ArrayList<Album> list) {
        list.sort(Comparator.comparing(Album::getActor));
    }
    public static void sortByYear(ArrayList<Album> list) {
        list.sort(Comparator.comparing(Album::getYear));
    }


}