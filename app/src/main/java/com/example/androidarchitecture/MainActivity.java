package com.example.androidarchitecture;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_REQUEST = 1;
    public static final int EDIT_REQUEST = 2;
    private NoteViewModel noteViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private NoteAdapter adapter;
    private FloatingActionButton addBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Architecture Components");
        recyclerView = findViewById(R.id.recyclerView);
        addBtn = findViewById(R.id.addNote);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditAddNoteActivity.class);
                startActivityForResult(intent, ADD_REQUEST);
            }
        });
        layoutManager = new LinearLayoutManager(this);
        adapter = new NoteAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getLiveData().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.submitList(notes);
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT){

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this,EditAddNoteActivity.class);
                intent.putExtra(EditAddNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(EditAddNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(EditAddNoteActivity.EXTRA_PRIORITY, note.getPriority());
                intent.putExtra(EditAddNoteActivity.EXTRA_ID, note.getId());
                startActivityForResult(intent, EDIT_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == ADD_REQUEST && resultCode == RESULT_OK){
                Note new_note = new Note(data.getStringExtra(EditAddNoteActivity.EXTRA_TITLE),
                        data.getStringExtra(EditAddNoteActivity.EXTRA_DESCRIPTION),
                                data.getIntExtra(EditAddNoteActivity.EXTRA_PRIORITY,1));
                noteViewModel.insert(new_note);
            Toast.makeText(this, "Note Added", Toast.LENGTH_SHORT).show();
        }else if(requestCode == EDIT_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(EditAddNoteActivity.EXTRA_ID,-1);
            if(id == -1){
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                return;
            }
            Note new_note = new Note(data.getStringExtra(EditAddNoteActivity.EXTRA_TITLE),
                    data.getStringExtra(EditAddNoteActivity.EXTRA_DESCRIPTION),
                    data.getIntExtra(EditAddNoteActivity.EXTRA_PRIORITY,1));
            new_note.setId(id);
            noteViewModel.update(new_note);
        }else {
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.deleteAll:
                noteViewModel.deleteAllNotes();
                Toast.makeText(this, "All notes Deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
