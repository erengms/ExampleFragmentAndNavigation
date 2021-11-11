package com.example.examplefragmentandnavigation;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.examplefragmentandnavigation.databinding.FragmentFirstBinding;

import java.util.ArrayList;


public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private ArrayList<Art> artArrayList;

    public FirstFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        artArrayList = new ArrayList<>();

        //todo reyclerview set adapter

        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    private void getData() {

        try {
            SQLiteDatabase database = requireContext().openOrCreateDatabase("Arts", Context.MODE_PRIVATE, null);
            Cursor cursor = database.rawQuery("SELECT * FROM arts", null);

            int idIx = cursor.getColumnIndex("id");
            int nameIx = cursor.getColumnIndex("name");
            int yearIx = cursor.getColumnIndex("year");
            int imageIx = cursor.getColumnIndex("image");

            while (cursor.moveToNext()){
                int id = cursor.getInt(idIx);
                String name = cursor.getString(nameIx);
                String year = cursor.getString(yearIx);
                byte[] imageByteArray = cursor.getBlob(imageIx);
                //byteArray To Bitmap
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);

                Art art = new Art(id, name, year, imageBitmap);
                artArrayList.add(art);
            }
            //todo recycler adapter notifyDatasetChanged
            cursor.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}