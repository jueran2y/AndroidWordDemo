package com.example.demo006_word;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo006_word.databinding.CellviewBinding;

import java.util.ArrayList;
import java.util.List;

/*
 * 1.添加完单词后默认中文不显示，这是一个BUG*/

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Word> allWords = new ArrayList<>();
    private WordViewModel wordViewModel;

    MyAdapter(WordViewModel wordViewModel) {
        this.wordViewModel = wordViewModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final CellviewBinding binding = CellviewBinding.inflate(LayoutInflater.from(parent.getContext())
                , parent, false);
        return new MyViewHolder(binding);
    }

    void setAllWords(List<Word> allWords) {
        this.allWords = allWords;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        //把要请求的单词号给到word
        final Word word = allWords.get(position);

        holder.itemView.setTag(R.id.word_my_adapter, word);

        holder.binding.chineseWord.setText(word.getChineseMeaning());
        holder.binding.englishWord.setText(word.getWord());
        holder.binding.number.setText(String.valueOf(position + 1));
        holder.binding.isChinese.setOnCheckedChangeListener(null);

        if (word.isChinese()) {
            holder.binding.isChinese.setChecked(false);//设置switch保持住false
            holder.binding.chineseWord.setVisibility(View.VISIBLE);
        } else {
            holder.binding.isChinese.setChecked(true);//设置switch保持住true
            holder.binding.chineseWord.setVisibility(View.INVISIBLE);
        }
        holder.binding.isChinese.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    word.setChinese(false);//设置Word里面的布尔值状态
                    holder.binding.chineseWord.setVisibility(View.VISIBLE);//文本不可见（位置不变） GONE（不可见位置改变）
                    wordViewModel.upDateWord(word);
                } else {
                    word.setChinese(true);//设置Word里面的布尔值状态
                    holder.binding.chineseWord.setVisibility(View.INVISIBLE);//文本不可见（位置不变） GONE（不可见位置改变）
                    wordViewModel.upDateWord(word);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return allWords.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        CellviewBinding binding;

        MyViewHolder(@NonNull CellviewBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
