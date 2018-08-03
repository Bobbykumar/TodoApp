package com.example.navgurukul.todoappfirebase.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.navgurukul.todoappfirebase.MainActivity;
import com.example.navgurukul.todoappfirebase.Model.Todo;
import com.example.navgurukul.todoappfirebase.R;

import java.util.List;

class listItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener{

    ItemClickListner itemClickListner;
    TextView title_text,note_text;

    public listItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);

        title_text=itemView.findViewById(R.id.text_title);
        note_text=itemView.findViewById(R.id.text_note);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View view) {
        itemClickListner.onClick(view,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        contextMenu.setHeaderTitle("Select the action");
        contextMenu.add(0,0,getAdapterPosition(),"DELETE");
    }
}

public class listItemAdapter extends RecyclerView.Adapter<listItemViewHolder>{



    MainActivity mainActivity;
    List<Todo>todoList;

    public listItemAdapter(MainActivity mainActivity, List<Todo> todoList) {
        this.mainActivity = mainActivity;
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public listItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        LayoutInflater inflater= LayoutInflater.from(mainActivity.getBaseContext());
        View view=inflater.inflate(R.layout.list_item,parent,false);

        return new listItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull listItemViewHolder holder, int position) {


        holder.title_text.setText(todoList.get(position).getTitle());
        holder.note_text.setText(todoList.get(position).getNote());

        holder.setItemClickListner(new ItemClickListner() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

                mainActivity.titleText.setText(todoList.get(position).getTitle());
                mainActivity.noteText.setText(todoList.get(position).getNote());

                mainActivity.isUpdate=true;
                mainActivity.idUpdate=todoList.get(position).getId();

            }
        });


    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }
}
