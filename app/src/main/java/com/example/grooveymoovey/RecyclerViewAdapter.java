package com.example.grooveymoovey;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> mImageNames;
    private ArrayList<String> mImages;
    private JSONArray mResultsArray;
    private Context mContext;

    RecyclerViewAdapter(ArrayList<String> imageNames, ArrayList<String> images, JSONArray resultsArray, Context context)
    {
        mImageNames = imageNames;
        mImages = images;
        mResultsArray = resultsArray;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Glide.with(mContext)
                .asBitmap()
                .load(mImages.get(position))
                .placeholder(R.drawable.ic_exclamation_mark)
                .into(holder.image);

        holder.imageName.setText(mImageNames.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String movieInfo = "";
                try
                {
                    movieInfo = mResultsArray.getJSONObject(position).toString();
                }
                catch (Exception ignored){}

                Intent intent = new Intent(mContext, Activity_movieInfo.class);
                intent.putExtra("movieInfo", movieInfo);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mImageNames.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
        TextView imageName;
        RelativeLayout parentLayout;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.image_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
