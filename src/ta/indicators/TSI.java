package ta.indicators;

import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

public class TSI {
	public static TimeSeries run(OHLCSeries data, int longLength, int shortLength) {
		final TimeSeries result = new TimeSeries("TSI");

		final TimeSeries closesTs = new TimeSeries("");
		for(int key=data.getItemCount()-1; key >= 0; key--) {
			closesTs.add(
				data.getPeriod(key),
				((OHLCItem) data.getDataItem(key)).getCloseValue()
			);
		}

		final TimeSeries pcTs = Utils.change(closesTs);

		final TimeSeries doubleSmoothedPcTs = doubleSmooth(pcTs, longLength, shortLength);

		final TimeSeries absPcTs = new TimeSeries("ABS_PC");
		for(int i=pcTs.getItemCount()-1; i >= 0; i--) {
			absPcTs.add(
				pcTs.getTimePeriod(i),
				Math.abs(pcTs.getDataItem(i).getValue().doubleValue())
			);
		}

		final TimeSeries doubleSmoothedAbsPcTs = doubleSmooth(absPcTs, longLength, shortLength);
		for(int key = doubleSmoothedPcTs.getItemCount()-1; key >= 0; key--) {
			result.add(
				doubleSmoothedPcTs.getTimePeriod(key),
				100.0 *
				(
					doubleSmoothedPcTs.getDataItem(key).getValue().doubleValue() /
					doubleSmoothedAbsPcTs.getDataItem(key).getValue().doubleValue()
				)
			);
		}

		return result;
	}

	private static TimeSeries doubleSmooth(final TimeSeries data, final int longLength, final int shortLength) {
		return EMA.run(EMA.run(data, longLength), shortLength);
	}
}
