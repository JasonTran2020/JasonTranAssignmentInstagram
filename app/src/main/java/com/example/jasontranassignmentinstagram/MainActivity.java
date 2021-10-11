package com.example.jasontranassignmentinstagram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG="MainActivity";
    private EditText etDescription;
    private Button btnSubmit;
    private Button btnCaptureImage;
    private Button btnLogOut;
    private ImageView ivPostImage;
    private ProgressBar pbUploadPost;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE=42;
    private File photoFile;
    private String photoFileName = "photo.jpg";
    private Drawable defaultPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDescription=findViewById(R.id.etDescription);
        btnSubmit=findViewById(R.id.btnSubmit);
        btnCaptureImage=findViewById(R.id.btnCaptureImage);
        btnLogOut=findViewById(R.id.btnLogOut);
        ivPostImage=findViewById(R.id.ivPostImage);
        defaultPicture=ivPostImage.getDrawable();
        pbUploadPost=findViewById(R.id.pbPostUpload);



        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        //queryPosts();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description=etDescription.getText().toString();
                pbUploadPost.setVisibility(View.VISIBLE);
                if(description.isEmpty())
                {
                    Toast.makeText(MainActivity.this,"Description cannot be empty", Toast.LENGTH_SHORT).show();
                    pbUploadPost.setVisibility(View.INVISIBLE);
                    return;
                }
                if(photoFile==null || ivPostImage.getDrawable()==null){
                    Toast.makeText(MainActivity.this,"No image taken", Toast.LENGTH_SHORT).show();
                    pbUploadPost.setVisibility(View.INVISIBLE);
                    return;
                }
                ParseUser currentUser=ParseUser.getCurrentUser();
                savePost(description,currentUser,photoFile);
                //pbUploadPost.setVisibility(View.INVISIBLE);
            }
        });
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent i=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
                //finish();
            }
        });
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview

                ivPostImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);

    }

    private void savePost(String description, ParseUser currentUser, File photoFile) {

        Post post=new Post();
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUser);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null)
                {
                    Log.e(TAG,"Error while saving",e);
                    Toast.makeText(MainActivity.this,"Error while saving",Toast.LENGTH_SHORT).show();
                    pbUploadPost.setVisibility(View.INVISIBLE);
                }
                else{
                    Log.i(TAG,"Post was successful");
                    Toast.makeText(MainActivity.this,"Post was saved!",Toast.LENGTH_SHORT).show();
                    etDescription.setText("");
                    ivPostImage.setImageDrawable(defaultPicture);
                    pbUploadPost.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e!=null)
                {
                    Log.i(TAG,"Issue with getting post");
                    return;
                }
                for(Post post:posts)
                {
                    Log.i(TAG, "Post: "+post.getDescription());
                }
            }
        });
    }
}