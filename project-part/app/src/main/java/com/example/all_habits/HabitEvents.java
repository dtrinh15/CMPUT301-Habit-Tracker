package com.example.all_habits;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HabitEvents extends AppCompatActivity {

    final long ONE_MEGABYTE = 1024 * 1024;
    private TextView commentEditText;
    private ImageView habitEventImage;
    private Button saveCommentButton;
    private Button habitEventImageButton;
    public String photoName;
    private String habitId;
    private String commentString;
    private ArrayAdapter<Comment> commentAdapter;
    FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;
    private CollectionReference habitReference; // collection of selected habit
    private CollectionReference userCollectionReference; // collection of signed in user
    private DocumentReference documentRef;
    private StorageReference storageRef;
    private StorageReference imageRef;
    private StorageReference resetRef;
    private int habitNum;

    //When activity is returned to. checks if the habit picture has been changed.
    @Override
    protected void onRestart() {
        super.onRestart();
        db.collection(currentFireBaseUser.getUid()).document(habitId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    photoName = task.getResult().getString("optionalPhoto");
                    if(photoName != null) {
                        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://projecthabits.appspot.com");
                        resetRef = storageRef.child(photoName);
                        resetRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                habitEventImage.setImageBitmap(bmp);
                            }
                        });
                    }
                }
            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habit_events);
        Intent intent = getIntent();
        habitNum = intent.getIntExtra("habitNum", 1); // gets the habit that was clicked on

        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference collectionReference = db.collection(currentFireBaseUser.getUid().toString());
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://projecthabits.appspot.com");
        //userCollectionReference = db.collection(currentFireBaseUser.getUid());
        commentEditText = findViewById(R.id.commentEditText);
        saveCommentButton = findViewById(R.id.saveCommentButton);
        habitEventImageButton = findViewById(R.id.habitEventImageButton);
        habitEventImage = findViewById(R.id.habitEventImage);

        Query findHabit = db.collection(currentFireBaseUser.getUid()).whereEqualTo("habitNum", habitNum).limit(1);
        findHabit.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                habitId = document.getId();
                                documentRef = db.collection(currentFireBaseUser.getUid()).document(habitId);
                                commentString = document.getString("comment");
                                photoName = document.getString("optionalPhoto");
                                // if the comment string is not empty
                                if (!commentString.equals("")) {
                                    commentEditText.setText(document.getString("comment"));
                                }


                            }
                        }

                        saveCommentButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (commentEditText.getText().toString().length() > 20) {
                                    Toast.makeText(getApplicationContext(), "The comment has to be under 20 characters long.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                documentRef.update("comment", commentEditText.getText().toString());
                                Toast.makeText(getApplicationContext(), "Your comment has been saved", Toast.LENGTH_SHORT).show();


                            }
                        });

                        //Starts CameraActivity.
                        habitEventImageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), cameraActivity.class);
                                intent.putExtra("habitId", habitId);
                                intent.putExtra("photoName", photoName);
                                startActivity(intent);
                            }
                        });

                        //If this habit has a picture, display it in the habitEventImage.
                        if (photoName != null) {
                            imageRef = storageRef.child(photoName);
                            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    habitEventImage.setImageBitmap(bmp);
                                }
                            });
                        }
                    }
        });
    }
}
