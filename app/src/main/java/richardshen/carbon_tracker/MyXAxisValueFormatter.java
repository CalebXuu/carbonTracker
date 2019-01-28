package richardshen.carbon_tracker;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Formatter class for the bar chart
 * Formats it so that the x-axis values are the dates
 */

public class MyXAxisValueFormatter implements IAxisValueFormatter {
        private String[] values;

        public MyXAxisValueFormatter(String[] values){
            this.values = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return values[(int) value];
        }
}

