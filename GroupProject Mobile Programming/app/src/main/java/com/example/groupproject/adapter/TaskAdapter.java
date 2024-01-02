package com.example.groupproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.example.groupproject.R;
import com.example.groupproject.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    /**
     * Create ViewHolder class to bind list item view
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        public TextView tvTitle;
        public TextView tvDesc;
        public TextView tvStatus;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);

            itemView.setOnLongClickListener(this);
        }
        @Override
        public boolean onLongClick(View view) {
            currentPos = getAdapterPosition(); //key point, record the position here
            return false;
        }

    }

    private List<Task> mListData;   // list of task objects
    private Context mContext;       // activity context
    private int currentPos;         //current selected position.

    public TaskAdapter(Context context, List<Task> listData){
        mListData = listData;
        mContext = context;
    }

    private Context getmContext(){return mContext;}


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the single item layout
        View view = inflater.inflate(R.layout.task_list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // bind data to the view holder
        Task m = mListData.get(position);
        holder.tvTitle.setText(m.getTitle());
        holder.tvDesc.setText(m.getDescription());
        holder.tvStatus.setText(m.getStatus());
    }

    @Override
    public int getItemCount() {

        return mListData.size();
    }

    public Task getSelectedItem() {
        if(currentPos>=0 && mListData!=null && currentPos<mListData.size()) {
            return mListData.get(currentPos);
        }
        return null;
    }


}
