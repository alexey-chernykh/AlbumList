package com.example.albumlist;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<Album> albums;
    public MyAdapter(@NonNull Context context, ArrayList<Album> albums) {
       this.context = context;
       this.albums = albums;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.album_item, parent, false);
        return new MyViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.albumName.setText(String.valueOf(albums.get(position).getAlbumName()));
        holder.actor.setText(String.valueOf(albums.get(position).getActor()));
        holder.year.setText(String.valueOf(albums.get(position).getYear()));
        byte[] imageBytes = albums.get(position).getPicture();
        Bitmap bitmap;
        if (imageBytes != null){
            bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.picture.setImageBitmap(bitmap);
            //holder.card.setBackgroundColor(getAverageColor(bitmap));
            holder.card.setBackgroundTintList(ColorStateList.valueOf(getAverageColor(bitmap)));
        }
    }

    public static int getAverageColor(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        long redSum = 0;
        long greenSum = 0;
        long blueSum = 0;
        long pixelCount = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bitmap.getPixel(x, y);
                redSum += Color.red(pixel);
                greenSum += Color.green(pixel);
                blueSum += Color.blue(pixel);
            }
        }

        int averageRed = (int) (redSum / pixelCount);
        int averageGreen = (int) (greenSum / pixelCount);
        int averageBlue = (int) (blueSum / pixelCount);

        return Color.rgb(averageRed, averageGreen, averageBlue);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView albumName, actor, year;
        ImageView picture;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            albumName = itemView.findViewById(R.id.albumName);
            actor = itemView.findViewById(R.id.actor);
            picture = itemView.findViewById(R.id.picture);
            year = itemView.findViewById(R.id.year);
            card = itemView.findViewById(R.id.CardView);
        }
    }
}
