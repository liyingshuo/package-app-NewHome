package com.android.newhome.ui.myself;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.newhome.R;
import com.android.newhome.ui.map.MapViewModel;
import com.android.newhome.ui.message.MessageViewModel;

public class MyselfFragment extends Fragment {

    private MyselfViewModel myselfViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myselfViewModel =
                new ViewModelProvider(this).get(MyselfViewModel.class);
        View root = inflater.inflate(R.layout.fragment_myself, container, false);
        final TextView textView = root.findViewById(R.id.text_myself);
        myselfViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

}