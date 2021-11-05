package com.example.all_habits;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Edit or delete's a habit.
 * @author Derek
 */
public class EditDelete extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    Button cancelButton;
    Button editButton;
    Button deleteButton;
    Switch privateSwitch;

    int habitNum;
    String habitId = "habit1";

    DocumentReference documentRef;
    EditText habitName;
    EditText reason;
    EditText startDate;
    TextView habitTextView;

    CheckBox Saturday;
    CheckBox Monday;
    CheckBox Tuesday;
    CheckBox Wednesday;
    CheckBox Thursday;
    CheckBox Friday;
    CheckBox Sunday;

    private FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editdelete);
        Intent intent = getIntent();
        ArrayList<String> habitDayArray = new ArrayList<String>();
        habitNum = intent.getIntExtra("habitNum",1);
        habitName = findViewById(R.id.habitName);
        reason = findViewById(R.id.habitReason);
        startDate = findViewById(R.id.habitStartDate);
        habitTextView = findViewById(R.id.habitNumber);
        privateSwitch = findViewById(R.id.privateSwitch);
        Saturday = findViewById(R.id.saturday);
        Monday = findViewById(R.id.monday);
        Tuesday = findViewById(R.id.tuesday);
        Wednesday = findViewById(R.id.wednesday);
        Thursday = findViewById(R.id.thursday);
        Friday = findViewById(R.id.friday);
        Sunday = findViewById(R.id.sunday);
        cancelButton = findViewById(R.id.cancelButton);
        editButton = findViewById(R.id.createButton);
        deleteButton = findViewById(R.id.deleteButton);
        Context context = getApplicationContext();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        //Creates the DatePickerDialog when StartDate EditText is clicked on.
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(),0);
                DialogFragment datePicker = new DatePickerDialogFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        //Retrieve the habit document that has the same value in the field habitNum.
        Query findHabit = db.collection(currentFireBaseUser.getUid()).whereEqualTo("habitNum", habitNum).limit(1);
        findHabit.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document:task.getResult()){
                                habitId = document.getId();
                                Log.d("TAG", habitId);
                            }
                        }

                        documentRef = db.collection(currentFireBaseUser.getUid()).document(habitId);
                        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                                ArrayList<String> habitDays =  new ArrayList<String>();
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if(document.exists()){
                                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());

                                        //Sets all the Edit Text fields to their database counterparts.
                                        habitName.setText(document.getString("habitName"));
                                        reason.setText(document.getString("reason"));
                                        startDate.setText(document.getString("startDate"));
                                        habitTextView.setText("Habit #" + habitNum);
                                        habitDays = (ArrayList<String>) document.get("habitDays");
                                        for(int i = 0; i < habitDays.size();i++) {
                                            if (habitDays.get(i).equals("Sun")) {
                                                Sunday.setChecked(true);
                                            }
                                            if (habitDays.get(i).equals("Mon")) {
                                                Monday.setChecked(true);
                                            }
                                            if (habitDays.get(i).equals("Tues")) {
                                                Tuesday.setChecked(true);
                                            }
                                            if (habitDays.get(i).equals("Wed")) {
                                                Wednesday.setChecked(true);
                                            }
                                            if (habitDays.get(i).equals("Thurs")) {
                                                Thursday.setChecked(true);
                                            }
                                            if (habitDays.get(i).equals("Fri")) {
                                                Friday.setChecked(true);
                                            }
                                            if (habitDays.get(i).equals("Sat")) {
                                                Saturday.setChecked(true);
                                            }
                                        }
                                    }else{
                                        Log.d("TAG","No such document");
                                    }
                                }
                            }
                        });
                    }
                });

        //Updates text with what is written on the EditText boxes.
        editButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(!Saturday.isChecked() && !Monday.isChecked() && !Tuesday.isChecked() && !Wednesday.isChecked()
                        && !Thursday.isChecked() && !Friday.isChecked() && !Sunday.isChecked()){
                    Toast.makeText(context,"Choose a day of the week or multiple to work on your habit.",Toast.LENGTH_SHORT).show();
                }else{
                    if(Sunday.isChecked()){
                        habitDayArray.add("Sun");
                    }
                    if(Monday.isChecked()){
                        habitDayArray.add("Mon");
                    }
                    if(Tuesday.isChecked()){
                        habitDayArray.add("Tues");
                    }
                    if(Wednesday.isChecked()){
                        habitDayArray.add("Wed");
                    }
                    if(Thursday.isChecked()){
                        habitDayArray.add("Thurs");
                    }
                    if(Friday.isChecked()){
                        habitDayArray.add("Fri");
                    }
                    if(Saturday.isChecked()){
                        habitDayArray.add("Sat");
                    }
                }
                if(habitName.getText().length() > 20){
                    Toast.makeText(context,"Habit name has to be under 20 characters long.",Toast.LENGTH_SHORT).show();
                }else if(reason.getText().length() > 30) {
                    Toast.makeText(context, "Reason has to be under 30 characters long.", Toast.LENGTH_SHORT).show();
                }else if(habitName.getText().toString().isEmpty() || reason.getText().toString().isEmpty()|| startDate.getText().toString().isEmpty()){
                    Toast.makeText(context, "Fill in all the data fields", Toast.LENGTH_SHORT).show();
                }else {
                    documentRef.update("habitName", habitName.getText().toString());
                    documentRef.update("reason", reason.getText().toString());
                    documentRef.update("startDate", startDate.getText().toString());
                    documentRef.update("habitDays", habitDayArray);
                    if (privateSwitch.isChecked()) {
                        documentRef.update("Private", true);
                    } else {
                        documentRef.update("Private", false);
                    }
                    finish();
                }
            }
        });

        //Deletes the habit.
        deleteButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final CollectionReference collectionReference = db.collection(currentFireBaseUser.getUid().toString());
                documentRef.delete();
                //Sets the ordered documents to 1 and increments up by 1 until the loop ends.
                collectionReference.orderBy("habitNum", Query.Direction.ASCENDING).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                int habitNum = 1;
                                if(task.isSuccessful()){
                                    for(QueryDocumentSnapshot document : task.getResult()){
                                        Log.d("Habits ",document.getId());
                                        collectionReference.document(document.getId()).update("habitNum", habitNum);
                                        habitNum++;
                                    }
                                }
                            }
                        });
                //Go back to list of habits.
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Go back to list of habits.
                finish();
            }
        });


    }
    //Sets the start date of the DatePickerDialog.
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,day);
        SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateString = simpleFormat.format(c.getTime());
        startDate.setText(currentDateString);
    }

}
