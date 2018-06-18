package com.example.afinal.reportedelitos.Adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.afinal.reportedelitos.Classes.Post;
import com.example.afinal.reportedelitos.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyRecycleItemViewHolder> {
    private ArrayList<Post> items;
    private Post mCurrentPost;

    public MyRecyclerAdapter(List<Post> items) {
        this.items = new ArrayList<>(items);
    }

    @NonNull
    @Override
    public MyRecyclerAdapter.MyRecycleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.activity_my_card_view, parent, false);
        MyRecycleItemViewHolder holder = new MyRecycleItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyRecycleItemViewHolder holder, int position) {
        mCurrentPost = items.get(holder.getAdapterPosition());
        holder.description.setText(mCurrentPost.getDescription());
        holder.city.setText(mCurrentPost.getCity());
        Glide.with(holder.itemView.getContext()).load(mCurrentPost.getImageUrl()).into(holder.imageFromUrl);
        //When CardView is pushed share the intent
        holder.myCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post current = items.get(holder.getAdapterPosition());
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Intent shareIntent;
                Bitmap bitmap = ((BitmapDrawable) holder.imageFromUrl.getDrawable()).getBitmap();
                String path = Environment.
                        getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Share.jpeg";
                OutputStream out = null;
                File file=new File(path);

                try {
                    out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                path=file.getPath();
                Uri bmpUri = Uri.parse("file://"+path);

                shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.putExtra(Intent.EXTRA_TEXT,"description: " + current.getDescription() +
                                                        "\nfrom: "  + current.getCity() +
                                                        "\nSent from Perpetrator!");
                shareIntent.setType("image/jpeg");

                holder.itemView.getContext().startActivity(Intent.createChooser(shareIntent,"Share with: " ));
            }
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    public class MyRecycleItemViewHolder
            extends RecyclerView.ViewHolder {

        TextView description, city;
        ImageView imageFromUrl;
        CardView myCardView;

        public MyRecycleItemViewHolder(View itemView) {
            super(itemView);
            imageFromUrl = (ImageView) itemView.findViewById(R.id.my_reporte_imagev);
            description = (TextView) itemView.findViewById(R.id.my_description_text_view);
            myCardView = (CardView) itemView.findViewById(R.id.my_card_view);
            city = (TextView) itemView.findViewById(R.id.my_city_tv);
        }
    }
}
