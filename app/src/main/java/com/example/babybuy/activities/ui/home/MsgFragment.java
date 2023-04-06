package com.example.babybuy.activities.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babybuy.R;
import com.example.babybuy.databinding.FragmentEditItemBinding;
import com.example.babybuy.databinding.FragmentMsgBinding;
import com.example.babybuy.helper.DownloadImageTask;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class MsgFragment extends Fragment {

    private FragmentMsgBinding binding;
    private Button  btnSendSms;
    TextView  txtItemName , txtItemDescription , txtItemLocation , txtItemQty , txtItemPrice ;
    EditText message , phoneNumber ;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMsgBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        message=binding.txtMsg;
        phoneNumber = binding.txtPhoneNumber;
        txtItemDescription = binding.txtMsgDesc;
        txtItemLocation = binding.txtMsgItemLocation;
        txtItemQty = binding.txtMsgItemQty;
        txtItemPrice = binding.txtMsgItemPrice;
        txtItemName = binding.txtMsgItemName;
        btnSendSms = binding.btnSendSms;
        btnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });

        getParam();
        return root ;
    }

    private void getParam(){
        String itemName = getArguments().getString("ItemName");
        String itemDescription = getArguments().getString("Description");
        String itemPrice = getArguments().getString("Price");
        String itemQty = getArguments().getString("Qty");
        String itemLocation = getArguments().getString("Location");

        txtItemName.setText("ItemName: " + itemName);
        txtItemPrice.setText("Price: " + itemPrice);
        txtItemDescription.setText("Description: " + itemDescription);
        txtItemQty.setText("Qty: " + itemQty);
        txtItemLocation.setText("Location: " + itemLocation);
    }


    private void sendSMS(){
        String phoneNo = phoneNumber.getText().toString();
        String smsMessage = txtItemName.getText().toString() +"\n" +  txtItemPrice.getText().toString() +"\n"+  txtItemQty.getText().toString() +"\n"+
                txtItemLocation.getText().toString() +"\n"+  txtItemDescription.getText().toString() +"\n Message:"+  message.getText().toString();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo,null,smsMessage, null,null);
            Toast.makeText(getContext(), "Message sent successfully", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(getView()).navigate(R.id.nav_home);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Please grant SMS permission!!!", Toast.LENGTH_LONG).show();
        }


    }

}