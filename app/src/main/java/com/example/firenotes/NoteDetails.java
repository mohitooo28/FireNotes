package com.example.firenotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteDetails extends AppCompatActivity {

    EditText titleEditText, contentEditText;
    ImageButton saveBtn, backBtn, delBtn;
    TextView textDateTime;
    String title, content, time, docId;
    boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        saveBtn = findViewById(R.id.save_note_btn);
        backBtn = findViewById(R.id.back_btn);
        delBtn = findViewById(R.id.delete_btn);
        textDateTime = findViewById(R.id.date_time_text);

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        time = getIntent().getStringExtra("time");
        docId = getIntent().getStringExtra("docId");

        if (docId != null && !docId.isEmpty()){
            isEditMode = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);
        textDateTime.setText(time);

        saveBtn.setEnabled(false);
        saveBtn.setImageAlpha(0x3F);
        delBtn.setVisibility(View.GONE);

        if (isEditMode) {
            saveBtn.setEnabled(true);
            saveBtn.setImageAlpha(0xFF);
            delBtn.setVisibility(View.VISIBLE);
        }

        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                if (!s.toString().equals("") || !s.toString().equals(null)) {
                    saveBtn.setEnabled(true);
                    saveBtn.setImageAlpha(0xFF);
                }
                if (s.toString().equals("") || s.toString().equals(null)) {
                    saveBtn.setEnabled(false);
                    saveBtn.setImageAlpha(0x3F);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        saveBtn.setOnClickListener((v) -> saveNote());

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        delBtn.setOnClickListener((v)-> deleteNoteFromFirebase());

        textDateTime.setText(
                new SimpleDateFormat("EEE, dd MMMM | hh:mm a ", Locale.getDefault()).format(new Date()));
    }

    void saveNote() {
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        String noteTime = textDateTime.getText().toString();

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTime(noteTime);

        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note) {
        DocumentReference documentReference;

        if (isEditMode){
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }
        else {
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Utility.showToast(NoteDetails.this, "Note Saved Successfully :D");
                    finish();
                } else {
                    Utility.showToast(NoteDetails.this, "Failed to Save the Note :(");
                }
            }
        });
    }

    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Utility.showToast(NoteDetails.this, "Note Deleted Successfully!");
                    finish();
                } else {
                    Utility.showToast(NoteDetails.this, "Failed to Deleted the Note!");
                }
            }
        });
    }

}