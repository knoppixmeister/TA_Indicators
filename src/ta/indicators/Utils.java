package ta.indicators;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.jfree.data.time.*;

public class Utils {
	public static double round(double value, int places) {
		if(places < 0) throw new IllegalArgumentException();

		return (new BigDecimal(value)).setScale(places, RoundingMode.HALF_UP).doubleValue();
	}

	public static boolean crossUp(final TimeSeries series1, final TimeSeries series2) {
		return	series1.getDataItem(series1.getItemCount()-1).getValue().doubleValue() > series2.getDataItem(series2.getItemCount()-1).getValue().doubleValue() &&
				series1.getDataItem(series1.getItemCount()-2).getValue().doubleValue() <= series2.getDataItem(series2.getItemCount()-2).getValue().doubleValue();
	}

	public static boolean crossDown(final TimeSeries series1, final TimeSeries series2) {
		return	series1.getDataItem(series1.getItemCount()-1).getValue().doubleValue() < series2.getDataItem(series2.getItemCount()-1).getValue().doubleValue() &&
				series1.getDataItem(series1.getItemCount()-2).getValue().doubleValue() >= series2.getDataItem(series2.getItemCount()-2).getValue().doubleValue();
	}

	public static TimeSeries lowest(final TimeSeries data, final int length) {
		final TimeSeries result = new TimeSeries("");

		for(int key=data.getItemCount()-1; key >= 0; key--) {
			if(key >= length) {
				double minVal = data.getDataItem(key).getValue().doubleValue();
				for(int i=key-1; i>=key-length+1; i--) {
					if(data.getDataItem(i).getValue().doubleValue() < minVal) {
						minVal = data.getDataItem(i).getValue().doubleValue();
					}
				}

				result.add(data.getTimePeriod(key), minVal);
			}
			else result.add(data.getTimePeriod(key), 0);
		}

		return result;
	}

	public static TimeSeries highest(final TimeSeries data, final int length) {
		final TimeSeries result = new TimeSeries("");

		for(int key=data.getItemCount()-1; key >= 0; key--) {
			if(key < length) result.add(data.getTimePeriod(key), 0);
			else {
				double maxVal = data.getDataItem(key).getValue().doubleValue();
				for(int i=key-1; i>=key-length+1; i--) {
					if(data.getDataItem(i).getValue().doubleValue() > maxVal) maxVal = data.getDataItem(i).getValue().doubleValue();
				}

				result.add(data.getTimePeriod(key), maxVal);
			}
		}

		return result;
	}

	public static TimeSeries change(final TimeSeries data) {
		return change(data, 1);
	}

	public static TimeSeries change(final TimeSeries data, final int offset) {
		final TimeSeries result = new TimeSeries("");

		for(int key=data.getItemCount()-1; key >= 0; key--) {
			if(key < offset) result.add(data.getDataItem(key).getPeriod(), 0);
			else {
				result.add(
					data.getTimePeriod(key),
					data.getDataItem(key).getValue().doubleValue() - data.getDataItem(key-offset).getValue().doubleValue()
				);
			}
		}

		return result;
	}

	public static TimeSeries sum(final TimeSeries data, final int offset) {
		final TimeSeries result = new TimeSeries("RESULT");

		double sum = 0;
		for(int key=data.getItemCount()-1; key > offset; key--) {
			sum = 0;
			for(int i=key; i>=key-offset+1; i--) {
				sum += data.getDataItem(i).getValue().doubleValue();
			}

			result.add(data.getDataItem(key).getPeriod(), sum);
		}

		for(int key=offset; key>=0; key--) {
			result.add(data.getDataItem(key).getPeriod(), 0);
		}

		return result;
	}

	public static TimeSeries percentrank(TimeSeries data, int period) {
		final TimeSeries result = new TimeSeries("");

		for(int key=data.getItemCount()-1; key > period; key--) {
		}

		return result;
	}
}
