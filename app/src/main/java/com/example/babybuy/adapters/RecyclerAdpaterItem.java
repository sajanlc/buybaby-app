package com.example.babybuy.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybuy.R;
import com.example.babybuy.helper.DownloadImageTask;
import com.example.babybuy.models.ItemModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecyclerAdpaterItem extends RecyclerView.Adapter<RecyclerAdpaterItem.ViewHolder> {
    Context context;
    ArrayList<ItemModel> itemList;
    StorageReference storageReference =  FirebaseStorage.getInstance().getReference();
    private ItemClickInterface itemClickInterface;

    public RecyclerAdpaterItem(Context context, ArrayList<ItemModel> itemList , ItemClickInterface itemClickInterface){
        this.context = context;
        this.itemList = itemList;
        this.itemClickInterface = itemClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card , parent,  false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        new DownloadImageTask((ImageView) holder.imageView).execute(itemList.get(position).image);


        storageReference.child("images/" + itemList.get(position).id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                new DownloadImageTask((ImageView) holder.imageView).execute(uri.toString() + ".jpeg");
            }
        }) ;
        holder.txtItemName.setText(itemList.get(position).name);
        holder.txtCreatedDate.setText(itemList.get(position).createdDate);
        holder.txtPrice.setText(itemList.get(position).price);
        holder.txtQty.setText(itemList.get(position).qty);
        holder.txtLocation.setText("Location: " + itemList.get(position).locationName);

        if(itemList.get(position).isPurchased){
            holder.txtIsPurchased.setVisibility(View.VISIBLE);
        }else{
            holder.txtIsPurchased.setVisibility(View.INVISIBLE);
        }

        holder.itemCardContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickInterface.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtItemName,txtQty,txtPrice,txtLocation , txtCreatedDate , txtIsPurchased;
        ImageView imageView;
        LinearLayout itemCardContainer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtItemName = itemView.findViewById(R.id.cardTxtItemName);
            txtQty = itemView.findViewById(R.id.cardTxtQty);
            txtPrice = itemView.findViewById(R.id.cardTxtPrice);
            txtLocation= itemView.findViewById(R.id.cardTxtLocation);
            imageView= itemView.findViewById(R.id.imgItem);
            txtCreatedDate= itemView.findViewById(R.id.cardTxtCreatedDate);
            itemCardContainer= itemView.findViewById(R.id.itemCardContainer);
            txtIsPurchased = itemView.findViewById(R.id.cardTxtIsPurchased);

        }
    }

    // creating a interface for on click
    public interface ItemClickInterface {
        void onItemClick(int position);
    }
}
