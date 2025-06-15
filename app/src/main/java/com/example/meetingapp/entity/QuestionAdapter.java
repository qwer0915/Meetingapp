package com.example.meetingapp.entity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.meetingapp.R;
import com.example.meetingapp.view.BoardDetailActivity;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private List<QuestionItem> questionList;

    public QuestionAdapter(List<QuestionItem> questionList) {
        this.questionList = questionList;
    }

    // ViewHolder for item
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView subject, author, date;

        public ItemViewHolder(View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.textSubject);
            author = itemView.findViewById(R.id.textAuthor);
            date = itemView.findViewById(R.id.textDate);
        }
    }

    // ViewHolder for header
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            QuestionItem item = questionList.get(position - 1); // 헤더 때문에 -1

            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.subject.setText(item.subject);
            itemHolder.author.setText(item.author);
            itemHolder.date.setText(item.create_date.substring(0, 10));

            // 제목 클릭 시 상세 페이지 이동
            itemHolder.subject.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent intent = new Intent(context, BoardDetailActivity.class);
                intent.putExtra("id", item.id);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size() + 1; // 헤더 포함
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    public void updateData(List<QuestionItem> newList) {
        this.questionList = newList;
        notifyDataSetChanged();
    }
}
