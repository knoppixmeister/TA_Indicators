package ta.indicators;

import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

public class AO {
	public static TimeSeriesCollection run(final OHLCSeries data) {
		final TimeSeries aoTs = new TimeSeries("");

		final TimeSeries seriesHl2Ts = new TimeSeries("HL2");
		for(int key=0; key<data.getItemCount(); key++) {
			seriesHl2Ts.add(
				data.getPeriod(key),
				(
					((OHLCItem)data.getDataItem(key)).getHighValue() +
					((OHLCItem)data.getDataItem(key)).getLowValue()
				) / 2.0
			);
		}

		final TimeSeries sma5	= SMA.run(seriesHl2Ts, 5);
		final TimeSeries sma34	= SMA.run(seriesHl2Ts, 34);

		for(int key=0; key<data.getItemCount(); key++) {
			aoTs.add(
				data.getPeriod(key),
				sma5.getDataItem(key).getValue().doubleValue() - sma34.getDataItem(key).getValue().doubleValue()
			);
		}

		final TimeSeries aoWmaTs = WMA.run(aoTs, 15);

		final TimeSeriesCollection result = new TimeSeriesCollection();

		result.addSeries(aoTs);
		result.addSeries(aoWmaTs);

		return result;
	}
}
