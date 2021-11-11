package com.example.examplefragmentandnavigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.examplefragmentandnavigation.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class ArtAdapter extends RecyclerView.Adapter<ArtAdapter.ArtHolder> {

   ArrayList<Art> artArrayList;

   public ArtAdapter(ArrayList<Art> artArrayList) {
      this.artArrayList = artArrayList;
   }

   @NonNull
   @Override
   public ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
      return new ArtHolder(recyclerRowBinding);
   }

   @Override
   public void onBindViewHolder(@NonNull ArtHolder holder, int position) {
      holder.binding.recyclerViewTextName.setText(artArrayList.get(position).name);
      holder.binding.recyclerViewTextYear.setText(artArrayList.get(position).year);
      holder.binding.recyclerviewImage.setImageBitmap(artArrayList.get(position).image);

   }

   @Override
   public int getItemCount() {
      return artArrayList.size();
   }

   public class ArtHolder extends RecyclerView.ViewHolder {

      private RecyclerRowBinding binding;

      public ArtHolder(RecyclerRowBinding binding) {
         super(binding.getRoot());
         this.binding = binding;
      }
   }


}
