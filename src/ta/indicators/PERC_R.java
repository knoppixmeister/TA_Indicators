package ta.indicators;

import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

public class PERC_R {
	public static TimeSeries run(final OHLCSeries data, final int period) {
		final TimeSeries result = new TimeSeries("OHLC_%R");

		final TimeSeries hsTs = new TimeSeries("");
		final TimeSeries lsTs = new TimeSeries("");

		for(int key=0; key<data.getItemCount(); key++) {
			hsTs.add(data.getPeriod(key), ((OHLCItem)data.getDataItem(key)).getHighValue());
			lsTs.add(data.getPeriod(key), ((OHLCItem)data.getDataItem(key)).getLowValue());
		}

		final TimeSeries highsTs	=	Utils.highest(hsTs, period);
		final TimeSeries lowsTs		=	Utils.lowest(lsTs, period);

		// (highest(high, 14)-close)/(highest(high, 14)-lowest(low, 14))*-100
		for(int key=0; key < data.getItemCount(); key++) {
			result.add(
				data.getPeriod(key),
				(
					highsTs.getDataItem(key).getValue().doubleValue() -
					((OHLCItem)data.getDataItem(key)).getCloseValue()
				)
				/
				(
					highsTs.getDataItem(key).getValue().doubleValue() -
					lowsTs.getDataItem(key).getValue().doubleValue()
				) *
				-100.0
			);
		}

		return result;
	}
}
