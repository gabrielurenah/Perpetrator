package com.example.afinal.reportedelitos.Fragments;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.afinal.reportedelitos.Manifest;
import com.example.afinal.reportedelitos.Classes.Post;
import com.example.afinal.reportedelitos.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class PublishFragment
        extends Fragment
        implements View.OnClickListener {

    private ImageView mUploadPicture;
    private Button mAddImage, mPublish;
    private EditText mEditPost;
    private TextView mLocationtv;
    private ProgressBar mpb;
    private Uri mImageUri;

    private FirebaseDatabase mDatabasePost;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private double mLat, mLon;

    public PublishFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_publish, container, false);

        mDatabasePost = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabasePost.getReference("posts");
        mStorageRef = FirebaseStorage.getInstance().getReference("posts");

        mAddImage = (Button) view.findViewById(R.id.my_add_image_button);
        mUploadPicture = (ImageView) view.findViewById(R.id.my_uploaded_image);
        mEditPost = (EditText) view.findViewById(R.id.my_edit_text);
        mPublish = (Button) view.findViewById(R.id.my_publish_button);
        mLocationtv = (TextView) view.findViewById(R.id.location_lat_long);
        mpb = (ProgressBar) view.findViewById(R.id.my_pb_upload_post);

        mAddImage.setOnClickListener(this);
        mPublish.setOnClickListener(this);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLat = location.getLatitude();
                mLon = location.getLongitude();
                mLocationtv.setText(String.valueOf(location.getLatitude() + ", " + location.getLongitude()));
            }
            @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override public void onProviderEnabled(String provider) { }
            @Override public void onProviderDisabled(String provider) { }
        };

        if (ActivityCompat.
                checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    2);

        } else {
            locationManager.requestLocationUpdates(LocationManager.
                            NETWORK_PROVIDER, 0, 0, locationListener);
        }

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            Glide.with(this).load(mImageUri).into(mUploadPicture);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        } if(requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.
                            NETWORK_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_add_image_button:
                openGallery();
                break;
            case R.id.my_publish_button:
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getContext(), "Upload in Progress", Toast.LENGTH_SHORT).show();
                } else addPost();
                break;
        }
    }

    private void addPost() {
        String description = mEditPost.getText().toString();

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(getContext(), "Need to write", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mUploadPicture == null) {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
            return;
        } else {
            //Start making url for image
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(() -> mpb.setProgress(0), 500);

                            Toast.makeText(getActivity(), "Upload Succesful", Toast.LENGTH_SHORT).show();

                            String id = mDatabaseRef.push().getKey();

                            taskSnapshot.getMetadata().getReference().getDownloadUrl().
                                    addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String city = hereLocation(mLat, mLon);
                                            String imageUrl = uri.toString();
                                            Log.wtf("onSuccesUri", uri.toString());
                                            Post post = new Post(id, description, imageUrl, city);
                                            if (id != null) mDatabaseRef.child(id).setValue(post);
                                        }
                                    });
                            //clean the fields after post is successful
                            mEditPost.setText("");
                            mUploadPicture.setImageResource(R.drawable.img_placeholder);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mpb.setProgress((int) progress);
                        }
                    });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }


    public String hereLocation(double lat, double lon) {
        String ourCity ="";

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addressList;
        try{
            addressList = geocoder.getFromLocation(lat, lon , 1);
            if(addressList.size() > 0){
                ourCity = addressList.get(0).getAddressLine(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ourCity;
    }

    private void openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Context context = getContext();
            Activity activity = getActivity();
            if (context != null && activity != null) {
                if (permissionIsGranted(context)) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                } else {
                    Log.wtf("TAG", "PERMISSION GRANTED");
                    getPhoto();
                }
            }
        } else {
            //if is lower than API23 permission is given when the app is installed;
            getPhoto();
        }
    }

    public boolean permissionIsGranted(Context context) {
        return ActivityCompat.
                checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED;
    }
}
