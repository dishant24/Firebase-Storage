package com.idiots.firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {
    private Context context;
    private List<Upload> mUploads;

    public ImageAdapter(Context context, List<Upload> mUploads) {
       this.context = context;
       this.mUploads = mUploads;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.image_item,parent,false);
        return new ImageHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        Upload upload = mUploads.get(position);
        holder.name.setText(upload.getName());
        Picasso.get().load(upload.getImageUrl()).fit().centerCrop().into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder{
        TextView name;
        ImageView imageView;
        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewName);
            imageView = itemView.findViewById(R.id.imageView_upload);
        }
    }
}
