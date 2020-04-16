package com.example.demo006_word;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

class WordViewModel extends AndroidViewModel {

    private WordRepository wordRepository;//创建的仓库类

    WordViewModel(@NonNull Application application) {
        super(application);
        wordRepository = new WordRepository(application);
    }

    LiveData<List<Word>> getAllWordList() {
        return wordRepository.getAllWordList();
    }
    LiveData<List<Word>> findWordWithPatten(String patten) {
        return wordRepository.findWordWithPatten(patten);
    }


    void insterWord(Word... words){
        wordRepository.insterWord(words);
    }
    void upDateWord(Word... words){
        wordRepository.upDateWord(words);
    }
    void DeleteAllWord(){
        wordRepository.DeleteAllWord();
    }
    void DeleteWord(Word... words){
        wordRepository.DeleteWord(words);
    }
}
