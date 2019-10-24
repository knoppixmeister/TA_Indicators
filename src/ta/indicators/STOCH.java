package ta.indicators;

import org.jfree.chart.util.Args;
import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

// https://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:stochrsi

public class STOCH {
	public static TimeSeriesCollection run(OHLCSeries data, int kLength, int dLength, int smoothK) {
		Args.requireNonNegative(kLength, "kLength");
		Args.requireNonNegative(dLength, "dLength");
		Args.requireNonNegative(smoothK, "smoothK");

		TimeSeriesCollection result = new TimeSeriesCollection();

		if(smoothK < 1) smoothK = 1;

		TimeSeries kTs = new TimeSeries("K_TS");

		for(int key=0; key<data.getItemCount(); key++) {
			if(key < kLength-1) {
				kTs.add(((OHLCItem)data.getDataItem(key)).getPeriod(), 0);
			}

			if(key >= kLength-1) {
				double highestHigh = Double.MIN_VALUE;
				for(int i=key-kLength+1; i<key; i++) {
					if(((OHLCItem)data.getDataItem(i)).getHighValue() > highestHigh) {
						highestHigh = ((OHLCItem)data.getDataItem(i)).getHighValue();
					}
				}

				double lowestLow = Double.MAX_VALUE;
				for(int i=key-kLength+1; i<key; i++) {
					if(((OHLCItem)data.getDataItem(i)).getLowValue() < lowestLow) {
						lowestLow = ((OHLCItem)data.getDataItem(i)).getLowValue();
					}
				}

				double k = 	(
								((OHLCItem)data.getDataItem(key)).getCloseValue() - lowestLow
							) /
							(
								highestHigh - lowestLow
							) * 100;

				kTs.add(
					((OHLCItem)data.getDataItem(key)).getPeriod(),
					k
				);
			}
		}

		//result.addSeries(indicators.SMA.run(kTs, smoothK));
		//result.addSeries(indicators.SMA.run(result.getSeries(0), dLength));

		return result;
	}

	public static TimeSeriesCollection run(TimeSeries data, int kLength, int dLength, int smoothK) {
		final TimeSeriesCollection result = new TimeSeriesCollection();

		final TimeSeries kTs = new TimeSeries("K_TS");

		double k;
		for(int key=0; key<data.getItemCount(); key++) {
			if(key < kLength-1) kTs.add(data.getDataItem(key).getPeriod(), 0);

			if(key >= kLength-1) {
				double highestHigh = Double.MIN_VALUE;
				for(int i=key-kLength+1; i<=key; i++) {
					if(data.getDataItem(i).getValue().doubleValue() > highestHigh) {
						highestHigh = data.getDataItem(i).getValue().doubleValue();
					}
				}

				double lowestLow = Double.MAX_VALUE;
				for(int i=key-kLength+1; i<=key; i++) {
					if(data.getDataItem(i).getValue().doubleValue() < lowestLow) {
						lowestLow = data.getDataItem(i).getValue().doubleValue();
					}
				}

				// Stoch RSI = (RSI - Lowest Low RSI) / (Highest High RSI - Lowest Low RSI)

				k = (
						data.getDataItem(key).getValue().doubleValue() - lowestLow
					)
					/
					(
						highestHigh - lowestLow
					);

				if(k < 0) k = 0;

				kTs.add(data.getTimePeriod(key), k);
			}
		}

		result.addSeries(SMA.run(kTs, smoothK));					// K series
		result.addSeries(SMA.run(result.getSeries(0), dLength));	// D series

		return result;
	}
}
