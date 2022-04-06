package com.theost.rxapp;

import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.theost.rxapp.databinding.ActivityMainBinding;

import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(view -> {
            binding.button.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
            Editable request = binding.editText.getText();
            if (request != null) search(request.toString().trim());
        });
    }

    private void search(String request) {
        Disposable disposable = Api.getData()
                .subscribeOn(Schedulers.io())
                .map(apiObjects -> apiObjects.stream()
                        .map(ApiObject::getValue)
                        .filter(value -> value.contains(request))
                        .limit(2000)
                        .sorted()
                        .collect(Collectors.toList()))
                .map(Object::toString)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(output -> {
                    binding.button.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.textView.setText(output);
                }, throwable -> {
                    binding.button.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.textView.setText(throwable.toString());
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

}