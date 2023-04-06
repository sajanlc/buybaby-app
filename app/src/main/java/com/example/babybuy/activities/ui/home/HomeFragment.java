package com.example.babybuy.activities.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybuy.LocationViewActivity;
import com.example.babybuy.R;
import com.example.babybuy.adapters.RecyclerAdpaterItem;
import com.example.babybuy.databinding.FragmentHomeBinding;
import com.example.babybuy.helper.DownloadImageTask;
import com.example.babybuy.models.ItemModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements RecyclerAdpaterItem.ItemClickInterface {

    private ArrayList<ItemModel> itemList = new ArrayList<>() ;
    private FragmentHomeBinding binding;
    private RecyclerAdpaterItem adapterItem;
    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://babybuy-c7d12-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference myRef  = database.getReference() ;
    private String currentUser ;
    FloatingActionButton fab;

    StorageReference storageReference =  FirebaseStorage.getInstance().getReference();

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        RecyclerView recyclerView = binding.recyclerVItem;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterItem = new RecyclerAdpaterItem(getContext() , itemList , this::onItemClick);
        recyclerView.setAdapter(adapterItem);
        getItems();
        fab=binding.fabAddItem;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.nav_addItem );
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onItemClick(int position){
        onItemClickFunc(position);
    }

    private void onItemClickFunc(int position){

        final CharSequence[] options = { "Edit","Delete" , "Map" , "Send Msg" , "Set as Purchased"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Actions");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if(options[item].equals("Edit")){
                    Bundle bundle = new Bundle();
                    bundle.putString("ItemId",itemList.get(position).id);
                    bundle.putString("ItemName",itemList.get(position).name);
                    bundle.putString("CreatedDate",itemList.get(position).createdDate);
                    bundle.putString("Description",itemList.get(position).description);
                    bundle.putBoolean("IsPurchased",itemList.get(position).isPurchased);
                    bundle.putString("Price", itemList.get(position).price);
                    bundle.putString("Qty", itemList.get(position).qty);
                    bundle.putString("LocationName", itemList.get(position).locationName);
                    bundle.putString("LocationLongitude", itemList.get(position).locationLongitude);
                    bundle.putString("LocationLatitude", itemList.get(position).locationLatitude);
                    Navigation.findNavController(getView()).navigate(R.id.nav_editItem , bundle);
                }else if(options[item].equals("Delete")){
                    myRef.child(currentUser).child("ItemList").child(itemList.get(position).id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                            getItems();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to delete item", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else if(options[item].equals("Map")){
                    Intent intent = new Intent(getContext() , LocationViewActivity.class);
                    intent.putExtra("longitude",itemList.get(position).locationLongitude);
                    intent.putExtra("latitude" , itemList.get(position).locationLatitude);
                    startActivity(intent);
                }else if(options[item].equals("Send Msg")){
                    Bundle bundle = new Bundle();
                    bundle.putString("ItemName",itemList.get(position).name);
                    bundle.putString("Description",itemList.get(position).description);
                    bundle.putString("Price", itemList.get(position).price);
                    bundle.putString("Qty", itemList.get(position).qty);
                    bundle.putString("Location", itemList.get(position).location);
                    bundle.putBoolean("IsPurchased", itemList.get(position).isPurchased);
                    Navigation.findNavController(getView()).navigate(R.id.nav_sendSms , bundle);
                }else if(options[item].equals("Set as Purchased")){
                    myRef.child(currentUser).child("ItemList").child(itemList.get(position).id).child("isPurchased").setValue(!itemList.get(position).isPurchased);
                }
            }
        });
        AlertDialog alertDialog =  builder.create();
        alertDialog.show();
    }
    private void getItems() {
        ValueEventListener postListener =  new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for(DataSnapshot itemSnapshot : snapshot.getChildren()){
                    ItemModel itemModel = itemSnapshot.getValue(ItemModel.class);
                    itemList.add(itemModel);
                }
                adapterItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext() , "Failed to load babies!!" , Toast.LENGTH_SHORT);
            }
        };
        myRef.child(currentUser).child("ItemList").addValueEventListener(postListener);
    }
}