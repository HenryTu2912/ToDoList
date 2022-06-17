package com.example.todolist;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<Task> taskList;
    private LayoutInflater inflater;
    private static ItemClickListener itemClickListener;
    Dialog dialog;

    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(View view, int pos);
    }

    public RecyclerAdapter(Context context, List<Task> taskList, ItemClickListener listener){
        this.taskList = taskList;
        setItemClickListener(listener);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.task_items, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.taskDateTime.setText(taskList.get(i).getDateTime());
        viewHolder.taskTitle.setText(taskList.get(i).getTitle());
        viewHolder.taskNotes.setText(taskList.get(i).getDetails());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView taskDateTime;
        private TextView taskTitle;
        private TextView taskNotes;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskDateTime = itemView.findViewById(R.id.taskDateTime);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskNotes = itemView.findViewById(R.id.taskNote);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(itemClickListener != null) itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }


}
