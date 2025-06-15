package com.example.meetingapp.entity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetingapp.ApiClient;
import com.example.meetingapp.ApiService;
import com.example.meetingapp.R;
import com.example.meetingapp.util.OnAnswerEditListener;
import com.example.meetingapp.view.AnswerFormActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {
    private List<AnswerItem> items = new ArrayList<>();
    private Context context;
    private ApiService api;
    private int questionId;
    private OnAnswerEditListener editListener;
    public AnswerAdapter(Context context, ApiService api, int questionId, OnAnswerEditListener editListener) {
        this.context = context;
        this.api = (api != null) ? api : ApiClient.getClient(context.getApplicationContext()).create(ApiService.class);
        this.questionId = questionId;
        this.editListener = editListener;
    }
    public void setItems(List<AnswerItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor, tvDate, tvContent;
        TextView btnEdit, btnDelete;
        public ViewHolder(View view) {
            super(view);
            tvAuthor = view.findViewById(R.id.tv_answer_author);
            tvDate = view.findViewById(R.id.tv_answer_date);
            tvContent = view.findViewById(R.id.tv_answer_content);
            btnEdit = view.findViewById(R.id.btn_edit_answer);
            btnDelete = view.findViewById(R.id.btn_delete_answer);
        }
    }

    @Override
    public AnswerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_answer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AnswerItem item = items.get(position);
        holder.tvAuthor.setText(item.author);
        holder.tvDate.setText(item.create_date);
        holder.tvContent.setText(item.content);
        // 본인만 수정 삭제 가능하도록
        String username = getLoggedInUsername();
        if (!username.equals(item.author)) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }
        // 삭제버튼
        holder.btnDelete.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            AnswerItem currentItem = items.get(currentPosition);
            Log.d("AnswerAdapter", "Delete request: questionId=" + questionId + ", answerId=" + currentItem.id);

            Map<String, Object> body = new HashMap<>();
            body.put("author", username);
            body.put("questionId", questionId);
            body.put("id", currentItem.id);

            api.deleteAnswer(body).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "댓글 삭제 완료", Toast.LENGTH_SHORT).show();
                        items.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                    } else {
                        Toast.makeText(context, "삭제 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show();
                }
            });
        });


        //수정모드
        holder.btnEdit.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEditAnswer(item, holder.getAdapterPosition());
            }
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            AnswerItem currentItem = items.get(currentPosition);
            Context context = v.getContext();

            Intent intent = new Intent(context, AnswerFormActivity.class);
            intent.putExtra("mode", "edit");
            intent.putExtra("answerId", currentItem.id);
            intent.putExtra("content", currentItem.content);

            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, 2000 + currentPosition); // 임의 requestCode
            }
        });
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    private String getLoggedInUsername() {
        SharedPreferences prefs = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        return prefs.getString("username", null);
    }

}

