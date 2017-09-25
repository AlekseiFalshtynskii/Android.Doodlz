package com.example.aleksei.doodlz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

public class ColorDialogFragment extends DoodleDialogFragment {
    private SeekBar alphaSeekBar;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private View colorView;
    private int color;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View colorDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_color, null);
        builder.setView(colorDialogView);
        builder.setTitle(R.string.title_color_dialog);

        alphaSeekBar = (SeekBar) colorDialogView.findViewById(R.id.alphaSeekBar);
        redSeekBar = (SeekBar) colorDialogView.findViewById(R.id.redSeekBar);
        greenSeekBar = (SeekBar) colorDialogView.findViewById(R.id.greenSeekBar);
        blueSeekBar = (SeekBar) colorDialogView.findViewById(R.id.blueSeekBar);
        colorView = colorDialogView.findViewById(R.id.colorView);

        alphaSeekBar.setOnSeekBarChangeListener(colorChangeListener);
        redSeekBar.setOnSeekBarChangeListener(colorChangeListener);
        greenSeekBar.setOnSeekBarChangeListener(colorChangeListener);
        blueSeekBar.setOnSeekBarChangeListener(colorChangeListener);

        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        color = doodleView.getDrawingColor();
        alphaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        builder.setPositiveButton(R.string.button_set_color,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doodleView.setDrawingColor(color);
                    }
                });
        return builder.create();
    }

    private final SeekBar.OnSeekBarChangeListener colorChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                color = Color.argb(
                        alphaSeekBar.getProgress(),
                        redSeekBar.getProgress(),
                        greenSeekBar.getProgress(),
                        blueSeekBar.getProgress()
                );
                colorView.setBackgroundColor(color);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };
}
