package com.example.examplefragmentandnavigation;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.examplefragmentandnavigation.databinding.FragmentSecondBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    private ActivityResultLauncher<String> permissionLauncher; // permissionLauncher.launch(String)
    private ActivityResultLauncher<Intent> activityResultLauncher; //activityResultLauncher.launch(intent)

    private Bitmap selectedImageBitmap;

    private SQLiteDatabase database;
    String info = "";


    public SecondFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerLauncher();


    }

    private void registerLauncher() {

        //izin istendiğinde dönen sonuç sonucu ne yapılacak?
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    // izin verildiyse galeriye git

                } else {
                    // daha önce izin istenmiş verilmemiş ve tekrar sorma denmiş
                    //todo dialog çıkarıp uygulama izinleri sayfasına yönlendir.
                    Toast.makeText(getActivity(), "Permission denied!", Toast.LENGTH_LONG).show();

                }
            }
        });


        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                //RESULT_OK kullanıcı galeriden resim seçti
                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent intentFromResult = result.getData();

                    if (intentFromResult != null){
                        Uri imageUri = intentFromResult.getData();

                        //seçilen resmi Bitmap'e çevirelim
                        try {
                            if (Build.VERSION.SDK_INT >= 28){
                                ImageDecoder.Source source = ImageDecoder.createSource(requireContext().getContentResolver(), imageUri);
                                selectedImageBitmap = ImageDecoder.decodeBitmap(source);
                            } else {
                                selectedImageBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                            }

                            binding.imageViewSelect.setImageBitmap(selectedImageBitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //database create
        database = requireContext().openOrCreateDatabase("ArtsBook", MODE_PRIVATE, null);

        //imageViewSelect onClick
        binding.imageViewSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //İZİN YOK, isteyelim
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    // Daha önce izini reddetmiş, dialog göster tekrar iste
                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        Snackbar.make(view, "Galeriye gitmek için izninizi istiyoruz", Snackbar.LENGTH_INDEFINITE)
                                .setAction("İzin Ver", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                       //todo request permission
                                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                                    }
                                }).show();
                    } else {
                        //ilk defa izin istenecek
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }

                }
                else {
                    //İZİN VAR, GALERİYE GİT
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }

            }
        });

        //save button onClick
        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String art_name = binding.editTextName.getText().toString();
                String art_year = binding.editTextYear.getText().toString();

                //Resimi db'ye kaydetmeden önce byteArray'e çevirelim
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                byte[] byteArrayImage = outputStream.toByteArray();

                //Db kayıt
                try {
                    database.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY, name VARCHAR, year VARCHAR, image BLOB)");

                    String sqlString = "INSERT INTO arts (name, year, image) VALUES(?, ?, ?)";
                    SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);

                    //1.soru işaretine name, 2.year, 3.image
                    sqLiteStatement.bindString(1, art_name);
                    sqLiteStatement.bindString(2, art_year);
                    sqLiteStatement.bindBlob(3, byteArrayImage);
                    sqLiteStatement.execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //kaydettikten sonra listeleme ekranına git
                NavDirections action = SecondFragmentDirections.actionSecondFragmentToFirstFragment();
                Navigation.findNavController(requireActivity(), R.id.fragmentNavHost).navigate(action);
            }
        });

        if (getArguments() != null){
            info = SecondFragmentArgs.fromBundle(getArguments()).getInfo();
        } else {
            info = "new";
        }


        if (info.equals("new")){
            binding.editTextName.setText("");
            binding.editTextYear.setText("");
            binding.buttonSave.setVisibility(View.VISIBLE);
            binding.imageViewSelect.setImageResource(R.drawable.ic_round_image_search_24);
        } else {
            int artId = SecondFragmentArgs.fromBundle(getArguments()).getArtId();
            binding.buttonSave.setVisibility(View.INVISIBLE);

            try {
                Cursor cursor = database.rawQuery("SELECT * FROM arts WHERE id = ?", new String[] {String.valueOf(artId)} );
                int nameIx = cursor.getColumnIndex("name");
                int yearIx = cursor.getColumnIndex("year");
                int imageIx = cursor.getColumnIndex("image");

                while (cursor.moveToNext()){
                    binding.editTextName.setText(cursor.getString(nameIx));
                    binding.editTextYear.setText(cursor.getString(yearIx));

                    byte[] bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    binding.imageViewSelect.setImageBitmap(bitmap);
                }
                cursor.close();

            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}