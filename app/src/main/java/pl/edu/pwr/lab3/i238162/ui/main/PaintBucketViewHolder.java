package pl.edu.pwr.lab3.i238162.ui.main;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

import pl.edu.pwr.lab3.i238162.Colour;
import pl.edu.pwr.lab3.i238162.R;

public class PaintBucketViewHolder {
    public ImageView fillrect;
    public TextView filltext;
    public TextView gaintext;

    public PaintBucketViewHolder(Activity a, Colour c) {
        switch (c) {
            case Red:
                fillrect = a.findViewById(R.id.fillrect_red);
                filltext = a.findViewById(R.id.filltext_red);
                gaintext = a.findViewById(R.id.gaintext_red);
                break;
            case Green:
                fillrect = a.findViewById(R.id.fillrect_green);
                filltext = a.findViewById(R.id.filltext_green);
                gaintext = a.findViewById(R.id.gaintext_green);
                break;
            case Blue:
                fillrect = a.findViewById(R.id.fillrect_blue);
                filltext = a.findViewById(R.id.filltext_blue);
                gaintext = a.findViewById(R.id.gaintext_blue);
                break;
        }
    }
}
