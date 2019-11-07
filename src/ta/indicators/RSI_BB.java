package ta.indicators;

import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;

public class RSI_BB {
	public static TimeSeriesCollection run(OHLCSeries data, int rsiLength, int bbLength, int stDev, double dispersion) {
		final TimeSeries rsiTs = RSI.runSmooth(data, rsiLength);
		TimeSeries basisTs = SMA.run(rsiTs, bbLength);

		final TimeSeries bbStDevTs = BB.standardDev(rsiTs, bbLength);

		TimeSeries devTs = new TimeSeries("DEV");
		for(int i=0; i<data.getItemCount(); i++) {
			devTs.add(
				data.getPeriod(i),
				stDev * bbStDevTs.getDataItem(i).getValue().doubleValue()
			);
		}

		final TimeSeries upperTs = new TimeSeries("UPPER");
		final TimeSeries lowerTs = new TimeSeries("UPPER");
		final TimeSeries dispUpTs = new TimeSeries("");
		final TimeSeries dispDownTs = new TimeSeries("UPPER");

		for(int i=0; i<data.getItemCount(); i++) {
			upperTs.add(
				data.getPeriod(i),
				basisTs.getDataItem(i).getValue().doubleValue() + devTs.getDataItem(i).getValue().doubleValue()
			);
			lowerTs.add(
				data.getPeriod(i),
				basisTs.getDataItem(i).getValue().doubleValue() - devTs.getDataItem(i).getValue().doubleValue()
			);
			dispUpTs.add(
				data.getPeriod(i),
				basisTs.getDataItem(i).getValue().doubleValue() +
				(
					(
						upperTs.getDataItem(i).getValue().doubleValue() -
						lowerTs.getDataItem(i).getValue().doubleValue()
					) *
					dispersion
				)
			);
			dispDownTs.add(
				data.getPeriod(i),
				basisTs.getDataItem(i).getValue().doubleValue() -
				(
					(
						upperTs.getDataItem(i).getValue().doubleValue() -
						lowerTs.getDataItem(i).getValue().doubleValue()
					) *
					dispersion
				)
			);
		}

		TimeSeriesCollection result = new TimeSeriesCollection();

		result.addSeries(upperTs);
		result.addSeries(lowerTs);
		result.addSeries(dispUpTs);
		result.addSeries(dispDownTs);

		return result; 
	}
}
