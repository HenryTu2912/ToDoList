package com.example.todolist;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class fragment_toDo extends Fragment {

    public RecyclerView recyclerView;
    public List<Task> lstToDo = new ArrayList<>();
    private RecyclerAdapter adapter;
    private TaskDBHelper db;


    public fragment_toDo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_do, container, false);
        db = new TaskDBHelper(getContext());


        Task task1 = new Task("Task 4", "Have to do");
        Task task2 = new Task("Task 5", "Have to do");
        Task task3 = new Task("Task 6", "Have to do");
        lstToDo.add(task1);
        lstToDo.add(task2);
        lstToDo.add(task3);
        task1.setDateTime("8:15 PM");
        task2.setDateTime("10:15 AM");
        task3.setDateTime("2:15 PM");
        displayTask();
        recyclerView = view.findViewById(R.id.toDoList);
        adapter = new RecyclerAdapter(getContext(), lstToDo, new RecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {

                Toast.makeText(getContext(), "Hello "+pos, Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(), manager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int moveFlag = makeMovementFlags(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT);
                return moveFlag;
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                Task taskDone = lstToDo.get(viewHolder.getAdapterPosition());
                int position = viewHolder.getAdapterPosition();
                if(i == ItemTouchHelper.RIGHT)
                {
                    lstToDo.remove(position);
                    db.deleteTask(taskDone);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    taskDone.setDone();
                    db.updateTaskDone(taskDone);
                    lstToDo.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                    Intent refresh = new Intent(getContext(), MainActivity.class);
                    startActivityForResult(refresh, 1);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.red))

                        .addSwipeRightActionIcon(R.drawable.ic_delete_24)

                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.green))

                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_save_24)

                        .create()

                        .decorate();
            }
        }).attachToRecyclerView(recyclerView);


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void displayTask(){
        Cursor cursor = db.getAllTask();
        if(cursor.getCount() == 0){
            Toast.makeText(getContext(), "No Data!", Toast.LENGTH_SHORT).show();
        }else {
            while (cursor.moveToNext()){
                int id = Integer.parseInt(cursor.getString(0));
                String sTitle = cursor.getString(1);
                String sDetails = cursor.getString(2);
                String sDateTime = cursor.getString(3);
                boolean isDone = (Integer.parseInt(cursor.getString(4))==0) ? false:true;
                byte[] image = cursor.getBlob(5);
                Task task = new Task(sTitle, sDetails);
                task.setDateTime(sDateTime);
                task.setId(id);
                if(image!=null) task.setImg(image);
                if(!isDone)
                {
                    lstToDo.add(task);
                }
            }
        }
        lstToDo = (ArrayList<Task>) lstToDo.stream().sorted((a,b)->a.getMinute()-b.getMinute()).collect(Collectors.toList());
    }


}