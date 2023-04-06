package com.example.babybuy.activities.ui.item;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babybuy.LocationPickerActivity;
import com.example.babybuy.R;
import com.example.babybuy.databinding.FragmentAddItemBinding;
import com.example.babybuy.models.ItemModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AddItemFragment extends Fragment {
    private Uri imageUri;
    private FragmentAddItemBinding binding;
    private FirebaseAuth mAuth;
    private TextView ItemName , ItemPrice , ItemQty , ItemDescription , locationName ;
    String  LocationName , LocationLongitude , LocationLatitude ;
    private Button btnSave , btnUploadImage , btnPickLocation;
    private String currentUser ;
    private ProgressBar progressBar;

    private ImageView imgItem ;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://babybuy-c7d12-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference myRef  = database.getReference(); ;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        binding = FragmentAddItemBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();

        ItemName = binding.txtItemName;
        ItemPrice = binding.txtItemPrice;
        ItemQty = binding.txtItemQty;
        ItemDescription = binding.txtItemDescription;
        btnSave = binding.btnSaveItem;
        progressBar = binding.loaderAddItem;
        btnUploadImage = binding.btnUploadImage;
        imgItem = binding.imgItemImage;
        btnPickLocation = binding.btnLocationPick;
        locationName = binding.txtLocationName;

        btnPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LocationPickerActivity.class);
                startActivityForResult(intent, 101);
            }
        });
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = { "Choose from Gallery","Cancel" };
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Upload Image");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if(options[item].equals("Choose from Gallery")){
                            SelectImage();
                        }
                    }
                });
                AlertDialog alertDialog =  builder.create();
                alertDialog.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnableButton(false);
                String itemName = ItemName.getText().toString();
                String itemQty = ItemQty.getText().toString();
                String itemPrice = ItemPrice.getText().toString();
                String itemDescription = ItemDescription.getText().toString();
                if(itemName.isEmpty()){
                    Toast.makeText(getContext(), "Please enter Item Name", Toast.LENGTH_SHORT).show();
                }else if(itemQty.isEmpty()){
                    Toast.makeText(getContext(), "Please enter Item Qty" , Toast.LENGTH_SHORT).show();
                }else if(itemPrice.isEmpty()){
                    Toast.makeText(getContext() , "Please enter Item Price" , Toast.LENGTH_SHORT).show();
                }else{
                    Date cDate = new Date();
                    String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
                    String itemId = myRef.push().getKey();
                    ItemModel itemModel = new ItemModel(itemId ,itemName, itemQty ,fDate , itemPrice,itemDescription,false , LocationName ,  LocationLongitude , LocationLatitude);
                    CreateItem(itemModel);
                }
            }
        });

        return root;
    }

    public void CreateItem(ItemModel itemModel){
        myRef.child(currentUser).child("ItemList").child(itemModel.id).setValue(itemModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if(imageUri == null){
                    Toast.makeText(getContext(), "Successfully saved" , Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(getView()).navigate(R.id.nav_home);
                    EnableButton(true);
                }else{
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + itemModel.id.toString());
                    storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Successfully saved" , Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(getView()).navigate(R.id.nav_home);
                            EnableButton(true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Image was not uploaded" , Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(getView()).navigate(R.id.nav_home);
                            EnableButton(true);
                        }
                    });
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Something went wrong" , Toast.LENGTH_SHORT).show();
                EnableButton(true);
            }
        });

    }

    public void EnableButton(boolean State){
        btnSave.setEnabled(State);
        btnUploadImage.setEnabled(State);
        btnPickLocation.setEnabled(State);
        if(State == true){
            progressBar.setVisibility(View.INVISIBLE);
        }else{
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void SelectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==  100  && data != null && data.getData()  != null){
            imageUri = data.getData();
            imgItem.setVisibility(View.VISIBLE);
            imgItem.setImageURI(imageUri);

        }
        if(requestCode == 101 && data!= null && data.getStringExtra("placeName") != null){
            String placeName = data.getStringExtra("placeName");
            String longitude= data.getStringExtra("LocationLongitude");
            String latitude= data.getStringExtra("LocationLatitude");

            locationName.setText("" + placeName);
            locationName.setVisibility(View.VISIBLE);
            LocationName = placeName;
            LocationLongitude = longitude;
            LocationLatitude = latitude.toString();
        }
    }
}