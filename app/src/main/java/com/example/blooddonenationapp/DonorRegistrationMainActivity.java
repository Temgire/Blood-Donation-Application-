package com.example.blooddonenationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonorRegistrationMainActivity extends AppCompatActivity {

    private TextView backButton;

    private CircleImageView profile_image;
    private TextInputEditText registerfullname,registeridnumber,registerphonenumber,registeremail,registerpassword;
    private Spinner bloodgroupspinner;
    private Button registerButton;

    private Uri resultUri;

    private ProgressDialog loader;

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_registration_main);

        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DonorRegistrationMainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        profile_image=findViewById(R.id.profile_image);
        registerfullname=findViewById(R.id.registerfullname);
        registeridnumber=findViewById(R.id.registeridnumber);
        registerphonenumber=findViewById(R.id.registerphonenumber);
        registeremail=findViewById(R.id.registeremail);
        registerpassword=findViewById(R.id.registerpassword);
        bloodgroupspinner=findViewById(R.id.bloodgroupspinner);
        registerButton=findViewById(R.id.registerButton);
        loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent,1);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = registeremail.getText().toString().trim();
                final String password = registerpassword.getText().toString().trim();
                final String fullname = registerfullname.getText().toString().trim();
                final String idnumber = registeridnumber.getText().toString().trim();
                final String phonenumber = registerphonenumber.getText().toString().trim();
                final String bloodgroup = bloodgroupspinner.getSelectedItem().toString().trim();

                if (TextUtils.isEmpty(email)){
                    registeremail.setError("Email is Required...");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    registerpassword.setError("Password is Required...");
                    return;
                }

                if (TextUtils.isEmpty(fullname)){
                    registerfullname.setError("Name is Required...");
                    return;
                }

                if (TextUtils.isEmpty(idnumber)){
                    registeridnumber.setError("ID is Required...");
                    return;
                }

                if (TextUtils.isEmpty(phonenumber)){
                    registerphonenumber.setError("Phone Number is Required...");
                    return;
                }

                if (bloodgroup.equals("Select a Blood group")){
                    Toast.makeText(DonorRegistrationMainActivity.this, "Select Blood Group", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    loader.setMessage("registering you...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()){
                                String error = task.getException().toString();
                                Toast.makeText(DonorRegistrationMainActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String currentuserId = mAuth.getCurrentUser().getUid();
                                userDatabaseRef = FirebaseDatabase.getInstance().getReference()
                                        .child("users").child(currentuserId);
                                HashMap userInfo = new HashMap();
                                userInfo.put("id",currentuserId);
                                userInfo.put("name",fullname);
                                userInfo.put("email",email);
                                userInfo.put("idnumber",idnumber);
                                userInfo.put("phonenumber",phonenumber);
                                userInfo.put("bloodgroup",bloodgroup);
                                userInfo.put("type","donor");
                                userInfo.put("search","donor"+bloodgroup);


                                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                       if (task.isSuccessful()){
                                           Toast.makeText(DonorRegistrationMainActivity.this, "Data set Successfully", Toast.LENGTH_SHORT).show();
                                       }
                                       else {
                                           Toast.makeText(DonorRegistrationMainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                       }

                                       finish();
                                      // loader.dismiss();
                                    }
                                });

                                if (resultUri !=null){
                                    final StorageReference filepath = FirebaseStorage.getInstance().getReference()
                                            .child("profile images").child(currentuserId);
                                    Bitmap bitmap = null;

                                    try {
                                       bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
                                    }catch (IOException e){
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
                                    byte[] data = byteArrayOutputStream.toByteArray();
                                    UploadTask uploadTask = filepath.putBytes(data);

                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DonorRegistrationMainActivity.this, "Image Upload Failed..", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null){
                                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                      String imageUrl = uri.toString();
                                                        Map newImageMap = new HashMap();
                                                        newImageMap.put("profilePicture",imageUrl);

                                                        userDatabaseRef.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                              if (task.isSuccessful()){
                                                                  Toast.makeText(DonorRegistrationMainActivity.this, "Image Url added to database Successfully", Toast.LENGTH_SHORT).show();
                                                              }
                                                              else {
                                                                  Toast.makeText(DonorRegistrationMainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                              }
                                                            }
                                                        });

                                                        finish();

                                                    }
                                                });
                                            }
                                        }
                                    });


                                    Intent intent = new Intent(DonorRegistrationMainActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    loader.dismiss();
                                }

                            }
                        }
                    });


                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data !=null){
            resultUri = data.getData();
            profile_image.setImageURI(resultUri);
        }
    }
}