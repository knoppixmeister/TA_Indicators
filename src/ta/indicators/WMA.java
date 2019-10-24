package ta.indicators;

import org.jfree.chart.util.Args;
import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

public class WMA {
	public static enum WMA_DATA_SOURCE {
		//OPEN,
		//HIGH,
		LOW,
		CLOSE,

		//HL2,
		//HLC3,
		//OHLC4,
	}

	/*
		WMA = (P1 * 5) + (P2 * 4) + (P3 * 3) + (P4 * 2) + (P5 * 1) / (5 + 4+ 3 + 2 + 1)

		Where:
		P1 = current price 
		P2 = price one bar ago, etc…
	*/

	public static TimeSeries run(final OHLCSeries data, int period) {
		return run(data, period, WMA_DATA_SOURCE.CLOSE);
	}

	public static TimeSeries run(final OHLCSeries data, int period, WMA_DATA_SOURCE dataSource) {
		Args.requireNonNegative(period, "period");

		if(period == 0) period = 1;

		final TimeSeries result = new TimeSeries("WMA_"+period);

		double pricesSum = 0, periodsSum = 0;

		for(int i=1; i<=period; i++) {
			periodsSum += i;
		}

		double calcVal = 0;
		for(int key=0; key < data.getItemCount(); key++) {
			if(key < period-1) result.add(data.getPeriod(key), 0);

			if(key >= period-1) {
				pricesSum = 0;

				int idx = 1;
				for(int i = key-(period-1); i <= key; i++) {
					if(dataSource == WMA_DATA_SOURCE.CLOSE) {
						calcVal = ((OHLCItem) data.getDataItem(i)).getCloseValue();
					}
					else if(dataSource == WMA_DATA_SOURCE.LOW) {
						calcVal = ((OHLCItem) data.getDataItem(i)).getLowValue();
					}

					pricesSum += calcVal * idx;

					++idx;
				}

				result.add(data.getPeriod(key), pricesSum / periodsSum);
			}
		}

		return result;
	}

	public static TimeSeries run(final TimeSeries data, int period) {
		Args.nullNotPermitted(data, "data");
		Args.requireNonNegative(period, "period");

		if(period == 0) period = 1;

		OHLCSeries series = new OHLCSeries("");
		for(int i=0; i<data.getItemCount(); i++) {
			series.add(data.getTimePeriod(i), 0, 0, 0, data.getDataItem(i).getValue().doubleValue());
		}

		return run(series, period, WMA_DATA_SOURCE.CLOSE);
	}
}
