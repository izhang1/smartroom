package com.example.izhang.smartroom;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class LightDevice extends AppCompatActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_device);
        context = this;

        Button changeColor = (Button) findViewById(R.id.changeColorButton);
        changeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(context)
                        .setTitle("Choose color")
                        .initialColor(0xffffffff)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                Toast.makeText(context, "Color Selected: " + Integer.toHexString(selectedColor), Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

    }
}
