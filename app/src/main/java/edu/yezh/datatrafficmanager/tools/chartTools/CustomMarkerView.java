package edu.yezh.datatrafficmanager.tools.chartTools;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import edu.yezh.datatrafficmanager.R;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;

public class CustomMarkerView extends MarkerView {

    private TextView tvContent;

    public CustomMarkerView (Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
       // tvContent.setText("" + e.getVal()); // set the entry-value as the display text
        OutputTrafficData data = new BytesFormatter(  ).getPrintSizeByModel(Math.round( e.getY() ));
        String showText = ""+Math.round(Double.valueOf(data.getValue())*100D)/100D+data.getType();
        tvContent.setText(showText);
        //tvContent.getLayoutParams().width = showText.length()*12+100;

    }

    /*@Override
    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }*/

    /*@Override
    public void setOffset(float offsetX, float offsetY) {
        offsetX = -(getWidth() / 2);
        offsetY = -getHeight();
        super.setOffset(offsetX, offsetY);
    }*/
    @Override
    public MPPointF getOffset() {
        // Log.e("ddd", "width:" + (-(getWidth() / 2)) + "height:" + (-getHeight()));
        return new MPPointF(-(getWidth() / 2), (-getHeight())-15);
    }
}
