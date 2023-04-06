package com.example.babybuy.activities.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
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
import com.example.babybuy.databinding.FragmentEditItemBinding;
import com.example.babybuy.helper.DownloadImageTask;
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
public class EditItemFragment extends Fragment {
    Uri imageUri;
    private FragmentEditItemBinding binding;
    private FirebaseAuth mAuth;
    private TextView ItemName , ItemPrice , ItemQty , ItemDescription  , txtLocationName;
    private Button btnUpdate , btnEditUploadImage , btnEditPickLocation;
    String  LocationName , LocationLongitude , LocationLatitude;
    private String currentUser ;
    private ProgressBar progressBar;
    private boolean IsPurchased;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://babybuy-c7d12-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference myRef  = database.getReference();
    String itemName , itemPrice , itemQty, itemDescription, image, itemId, createdDate, isPurchase;
    ImageView imgItem;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        binding = FragmentEditItemBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();

        getParams();
        ItemName = binding.txtEditItemName;
        ItemPrice = binding.txtEditItemPrice;
        ItemQty = binding.txtEditItemQty;
        ItemDescription = binding.txtEditItemDescription;
        btnUpdate = binding.btnUpdateItem;
        progressBar = binding.loaderEditItem;
        btnEditUploadImage = binding.btnEditUploadImage;
        imgItem = binding.imgEditItemImage;


        txtLocationName = binding.txtEditLocationName;
        txtLocationName.setVisibility(View.VISIBLE);
        txtLocationName.setText("Location: "+ LocationName );

        ItemName.setText(itemName);
        ItemPrice.setText(itemPrice);
        ItemQty.setText(itemQty);
        ItemDescription.setText(itemDescription);
        btnEditPickLocation  = binding.btnEditLocationPick;
        btnEditPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LocationPickerActivity.class);
                startActivityForResult(intent, 101);
            }
        });


        storageReference.child("images/" + itemId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imgItem.setVisibility(View.VISIBLE);
                new DownloadImageTask((ImageView) imgItem).execute(uri.toString() + ".jpeg");
            }
        }) ;

        btnEditUploadImage.setOnClickListener(new View.OnClickListener() {
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


        btnUpdate.setOnClickListener(new View.OnClickListener() {
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
                    ItemModel itemModel = new ItemModel(itemId ,itemName, itemQty ,fDate , itemPrice,itemDescription, IsPurchased , LocationName , LocationLongitude , LocationLatitude);
                    UpdateItem(itemModel);
                }
            }
        });
        return root;
    }
    public void EnableButton(boolean State){
        btnUpdate.setEnabled(State);
        btnEditUploadImage.setEnabled(State);
        btnEditPickLocation.setEnabled(State);
        if(State == true){
            progressBar.setVisibility(View.INVISIBLE);
        }else{
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void getParams(){
        itemId = getArguments().getString("ItemId");
        itemName = getArguments().getString("ItemName");
        createdDate = getArguments().getString("CreatedDate");
        itemDescription = getArguments().getString("Description");
        image = getArguments().getString("Image");
        isPurchase = getArguments().getString("IsPurchased");
        itemPrice = getArguments().getString("Price");
        itemQty = getArguments().getString("Qty");
        IsPurchased = getArguments().getBoolean("IsPurchased");

        LocationName  = getArguments().getString("LocationName") ;
        LocationLongitude = getArguments().getString("LocationLongitude") ;
        LocationLatitude   = getArguments().getString("LocationLatitude") ; ;
    }

    public void UpdateItem(ItemModel itemModel){
        myRef.child(currentUser).child("ItemList").child(itemModel.id).setValue(itemModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/" + itemModel.id.toString());
                storageRef.delete();
                storageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Something went wrong" , Toast.LENGTH_SHORT).show();
                EnableButton(true);
            }
        });
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

            txtLocationName.setText("" + placeName);
            txtLocationName.setVisibility(View.VISIBLE);
            LocationName = placeName;
            LocationLongitude = longitude;
            LocationLatitude = latitude.toString();
        }
    }

}