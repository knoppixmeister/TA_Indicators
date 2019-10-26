package ta.indicators;

import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

public class BB {
	public static TimeSeriesCollection run(final TimeSeries data, int period, int deviation) {
		OHLCSeries series = new OHLCSeries("");
		for(int key=0; key<data.getItemCount(); key++) {
			series.add(
				data.getDataItem(key).getPeriod(),
				0,
				0,
				0,
				data.getDataItem(key).getValue().doubleValue()
			);
		}

		return run(series, period, deviation);
	}

	public static TimeSeriesCollection run(OHLCSeries data, int period, double deviation) {
		final TimeSeriesCollection result = new TimeSeriesCollection();

		result.addSeries(ta.indicators.SMA.run(data, period));
		result.addSeries(new TimeSeries("upper_band"));
		result.addSeries(new TimeSeries("lower_band"));

		for(int key=0; key<data.getItemCount(); key++) {
			if(key < period-1) {
				result.getSeries(1).add(data.getPeriod(key), 0);
				result.getSeries(2).add(data.getPeriod(key), 0);
			}

			if(key >= period-1) {
				double[] closes = new double[period];

				for(int i=0; i<period; i++) {
					closes[i] = ((OHLCItem)data.getDataItem(key-period+i+1)).getCloseValue();
				}

				double stDev = BB.standardDev(closes);

				double upperBand = result.getSeries(0).getDataItem(key).getValue().doubleValue()+(stDev*deviation);

				result.getSeries(1).add(result.getSeries(0).getDataItem(key).getPeriod(), upperBand);

				double lowerBand = result.getSeries(0).getDataItem(key).getValue().doubleValue()-(stDev*deviation);

				result.getSeries(2).add(result.getSeries(0).getDataItem(key).getPeriod(), lowerBand);
			}
		}

		return result;
	}

	public static double standardDev(double[] numbers) {
		double sum = 0, avg = 0;

		for(int i=0; i<numbers.length; i++) {
			sum += numbers[i];
		}
		avg = sum / (double)numbers.length;

		double sumOfMinSquares = 0;
		for(int i=0; i<numbers.length; i++) {
			sumOfMinSquares += Math.pow(numbers[i]-avg, 2);
		}

		return Math.sqrt((1/(double)numbers.length) * sumOfMinSquares);
	}

	public static TimeSeries standardDev(final TimeSeries data, final int period) {
		final TimeSeries result = new TimeSeries("");

		for(int key=data.getItemCount()-1; key>=0; key--) {
			if(key >= period-1) {
				double sum = 0, avg = 0;

				for(int i=key; i>=key-period+1; i--) {
					sum += data.getDataItem(i).getValue().doubleValue();
				}
				avg = sum / (double)period;

				double sumOfMinSquares = 0;
				for(int i=key; i>=key-period+1; i--) {
					sumOfMinSquares += Math.pow(data.getDataItem(i).getValue().doubleValue()-avg, 2);
				}

				result.add(
					data.getDataItem(key).getPeriod(),
					Math.sqrt((1/(double)period) * sumOfMinSquares)
				);
			}
			else result.add(data.getDataItem(key).getPeriod(), 0);
		}

		return result;
	}
}
