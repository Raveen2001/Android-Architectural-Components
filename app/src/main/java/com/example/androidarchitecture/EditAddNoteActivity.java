package com.example.androidarchitecture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

public class EditAddNoteActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.example.androidarchitecture.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.androidarchitecture.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.androidarchitecture.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY = "com.example.androidarchitecture.EXTRA_PRIORITY";
    private EditText title, description;
    private NumberPicker priority;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_add_note);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        priority = findViewById(R.id.priority);
        priority.setMinValue(1);
        priority.setMaxValue(10);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_ID)){
            setTitle("Edit Note");
            title.setText(intent.getStringExtra(EXTRA_TITLE));
            description.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            priority.setValue(intent.getIntExtra(EXTRA_PRIORITY, 1));
        }else{
            setTitle("Add Note");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNote() {
        String title = this.title.getText().toString();
        String description = this.description.getText().toString();
        int priority = this.priority.getValue();
        if(title.trim().isEmpty() || description.trim().isEmpty()){
            Toast.makeText(this, "Please Enter a Title and a Description", Toast.LENGTH_SHORT).show();
            return;
           }
        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_PRIORITY,priority);
        if(getIntent().hasExtra(EXTRA_ID)){
            data.putExtra(EXTRA_ID, getIntent().getIntExtra(EXTRA_ID,-1));
        }
        setResult(RESULT_OK, data);
        finish();
    }

}
