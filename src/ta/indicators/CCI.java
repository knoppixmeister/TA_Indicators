package ta.indicators;

import java.util.*;
import java.util.List;
import org.jfree.chart.util.Args;
import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

public class CCI {
	public static enum CCI_DATA_SOURCE {
		OPEN,
		HIGH,
		LOW,
		CLOSE,

		HL2,
		HLC3,
		OHLC4,
	}

	public static TimeSeries run(final OHLCSeries data, final int period) {
		return run(data, period, CCI_DATA_SOURCE.HLC3);
	}

	public static TimeSeries run(final OHLCSeries data, int period, final CCI_DATA_SOURCE dataSource) {
		Args.nullNotPermitted(data, "data");

		if(period < 1) period = 1;

		try {
			CCI_DATA_SOURCE tst = CCI_DATA_SOURCE.valueOf(dataSource.toString());
			if(tst != dataSource) return null;
		}
		catch(Exception e) {
			return null;
		}

		final TimeSeries result = new TimeSeries("CCI");

		final List<Double> tpArray = new ArrayList<>();

		double tp = 0;
		for(int key=0; key < data.getItemCount(); key++) {
			if(dataSource == CCI_DATA_SOURCE.CLOSE) {
				tp = ((OHLCItem)data.getDataItem(key)).getCloseValue();
			}
			else if(dataSource == CCI_DATA_SOURCE.HLC3) {
				tp = (
						((OHLCItem)data.getDataItem(key)).getHighValue() +
						((OHLCItem)data.getDataItem(key)).getLowValue() +
						((OHLCItem)data.getDataItem(key)).getCloseValue()
					 ) / 3.0;
			}
			else continue;

			tpArray.add(key, tp);

			if(key < period-1) {
				result.add(data.getPeriod(key), 0);
			}

			if(key >= period-1) {
				// TP moving average 
				double sum = 0;
				for(int i=key; i>=key-period+1; i--) {
					sum += tpArray.get(i);
				}

				double tp_sma = sum / (double)period;

				//calc mean dev
				double sum_abs = 0;
				for(int i=key; i>=key-period+1; i--) {
					sum_abs += Math.abs(tp_sma - tpArray.get(i));
				}

				double mean_dev = sum_abs / (double)period;

				// calc cci
				double cci = (tp - tp_sma) / (0.015 * mean_dev);

				result.add(data.getPeriod(key), cci);
			}
		}

		return result;
	}
}
