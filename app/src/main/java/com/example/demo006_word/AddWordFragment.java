package com.example.demo006_word;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.demo006_word.databinding.FragmentAddWordBinding;

import java.util.Objects;

public class AddWordFragment extends Fragment {

    public AddWordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final FragmentAddWordBinding binding = FragmentAddWordBinding.inflate(inflater, container, false);
        final WordViewModel wordViewModel = new WordViewModel(Objects.requireNonNull(getActivity()).getApplication());
        binding.editTextEnglishWord.hasFocusable();
        setHasOptionsMenu(true);

        binding.buttonAddWordSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EnglishWord = binding.editTextEnglishWord.getText().toString();
                String ChineseWord = binding.editTextChineseWord.getText().toString();
                Word word = new Word(EnglishWord, ChineseWord);
                wordViewModel.insterWord(word);

                //收起键盘
                InputMethodManager manager = ((InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE));
                NavController controller = Navigation.findNavController(v);
                if (manager != null)
                    //收键盘
                    manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                controller.navigate(R.id.action_addWordFragment_to_mainFragment);
            }
        });

        binding.buttonAddWordSure.setEnabled(false);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String eg = binding.editTextEnglishWord.getText().toString().trim();
                String ch = binding.editTextChineseWord.getText().toString().trim();
                binding.buttonAddWordSure.setEnabled(!eg.isEmpty() && !ch.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        binding.editTextChineseWord.addTextChangedListener(textWatcher);
        binding.editTextEnglishWord.addTextChangedListener(textWatcher);


        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}
