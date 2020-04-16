package com.example.demo006_word;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

class WordRepository {
    private WordDao wordDao;
    private LiveData<List<Word>> allWordList;

    WordRepository(Context context) {
        WordDataBase wordDataBase = WordDataBase.getDatabase(context.getApplicationContext());
        wordDao = wordDataBase.getWordDao();
        allWordList = wordDao.getAllWords();
    }

    LiveData<List<Word>> getAllWordList() {
        return allWordList;
    }

    LiveData<List<Word>> findWordWithPatten(String patten){
        return wordDao.findWordsWithPatten("%" + patten + "%");
    }



    void insterWord(Word... words){
        new InsterAsyncTask(wordDao).execute(words);
    }
    void upDateWord(Word... words){
        new UpDateAsyncTask(wordDao).execute(words);
    }
    void DeleteAllWord(){
        new DeleteAllAsyncTask(wordDao).execute();
    }
    void DeleteWord(Word... words){
        new DeleteAsyncTask(wordDao).execute(words);
    }

    //增加单词
    static class InsterAsyncTask extends AsyncTask<Word,Void,Void> {
        private WordDao wordDao;

        InsterAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.insertWords(words);
            return null;
        }
    }

    //清空单词
    static class DeleteAllAsyncTask extends AsyncTask<Void,Void,Void>{
        private WordDao wordDao;

        DeleteAllAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wordDao.deleteAllWords();
            return null;
        }
    }

    //删除单词
    static class DeleteAsyncTask extends AsyncTask<Word,Void,Void>{
        private WordDao wordDao;

        DeleteAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.deleteWords(words);
            return null;
        }
    }

    //修改单词
    static class UpDateAsyncTask extends AsyncTask<Word,Void,Void> {
        private WordDao wordDao;

        UpDateAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.updateWords(words);
            return null;
        }
    }
}
