package com.cemalettinaltinas.artbookjava;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cemalettinaltinas.artbookjava.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class ArtAdapter extends RecyclerView.Adapter<ArtAdapter.ArtHolder> {

    ArrayList<Art> artArrayList;
    Context context;
    public ArtAdapter(ArrayList<Art> artArrayList,Context context) {
        this.context=context;
        this.artArrayList = artArrayList;
    }

    @Override
    public ArtHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //RecyclerRowBinding binding = RecyclerRowBinding.inflate(LayoutInflater
          //      .from(parent.getContext()), parent, false);
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.recycler_row,parent,false);
        return new ArtHolder(view);
    }

    @Override
    public void onBindViewHolder(ArtAdapter.ArtHolder holder, int position) {
        //holder.recyclerTextView.setText(artArrayList.get(position).name);
        holder.recyclerViewTextView.setText( artArrayList.get(holder.getAdapterPosition()).name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), ArtActivity.class);
                intent.putExtra("artId", artArrayList.get(position).id);
                intent.putExtra("info", "old");
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artArrayList.size();
    }

    public class ArtHolder extends RecyclerView.ViewHolder {
        TextView recyclerViewTextView;

        public ArtHolder(@NonNull View itemView) {
            super(itemView);
            this.recyclerViewTextView=itemView.findViewById(R.id.nameText);


        }
    }
}