package ta.indicators;

import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

public class MACD {
	public static enum MACD_DATA_SOURCE {
		OPEN,
		HIGH,
		LOW,
		CLOSE,

		HL2,
		HLC3,
		OHLC4,
	}

	public static TimeSeriesCollection run(OHLCSeries data, int ema1_length, int ema2_length, int signal_length) {
		return run(data, ema1_length, ema2_length, signal_length, true);
	}

	public static TimeSeriesCollection run(OHLCSeries data, int ema1_length, int ema2_length, int signal_length, boolean useSma) {
		return run(data, ema1_length, ema2_length, signal_length, MACD_DATA_SOURCE.CLOSE, useSma);
	}

	public static TimeSeriesCollection run(OHLCSeries data, int ema1_length, int ema2_length, int signal_length, MACD_DATA_SOURCE macdDataSource, boolean useSma) {
		final TimeSeriesCollection result = new TimeSeriesCollection();

		final TimeSeries maSrcTs = new TimeSeries("");
		double val = 0;
		for(int key=0; key<data.getItemCount(); key++) {
			if(macdDataSource == MACD_DATA_SOURCE.OPEN) {
				val = ((OHLCItem)data.getDataItem(key)).getOpenValue();
			}
			else if(macdDataSource == MACD_DATA_SOURCE.HIGH) {
				val = ((OHLCItem)data.getDataItem(key)).getHighValue();
			}
			else if(macdDataSource == MACD_DATA_SOURCE.LOW) {
				val = ((OHLCItem)data.getDataItem(key)).getLowValue();
			}
			else if(macdDataSource == MACD_DATA_SOURCE.CLOSE) {
				val = ((OHLCItem)data.getDataItem(key)).getCloseValue();
			}
			else if(macdDataSource == MACD_DATA_SOURCE.HL2) {
				val = (((OHLCItem)data.getDataItem(key)).getHighValue() + ((OHLCItem)data.getDataItem(key)).getLowValue()) / 2d;
			}
			else if(macdDataSource == MACD_DATA_SOURCE.HLC3) {
				val =	(
							((OHLCItem)data.getDataItem(key)).getHighValue() +
							((OHLCItem)data.getDataItem(key)).getLowValue() +
							((OHLCItem)data.getDataItem(key)).getCloseValue()
						) / 3.0;
			}
			else if(macdDataSource == MACD_DATA_SOURCE.OHLC4) {
				val =	(
							((OHLCItem)data.getDataItem(key)).getOpenValue() +
							((OHLCItem)data.getDataItem(key)).getHighValue() +
							((OHLCItem)data.getDataItem(key)).getLowValue() +
							((OHLCItem)data.getDataItem(key)).getCloseValue()
						) / 4.0;
			}
			else return null;

			maSrcTs.add(data.getPeriod(key), val);
		}

		final TimeSeries fastMATs = useSma ? SMA.run(maSrcTs, ema1_length) : EMA.run(maSrcTs, ema1_length);
		final TimeSeries slowMATs = useSma ? SMA.run(maSrcTs, ema2_length) : EMA.run(maSrcTs, ema2_length);

		result.addSeries(new TimeSeries("MACD"));

		double fastMA, slowMA;
		for(int i=data.getItemCount()-1; i>=0; i--) {
			fastMA = fastMATs.getDataItem(i).getValue().doubleValue();
			slowMA = slowMATs.getDataItem(i).getValue().doubleValue();

			// macd
			result.getSeries(0).add(
				data.getPeriod(i),
				i > Math.max(ema1_length, ema2_length) ? fastMA-slowMA : 0
			);
		}

		// signal
		result.addSeries(SMA.run(result.getSeries(0), signal_length));

		// histogram
		result.addSeries(new TimeSeries("HISTGR"));

		for(int i=data.getItemCount()-1; i >= 0; i--) {
			result.getSeries(2).add(
				data.getPeriod(i),
				result.getSeries(0).getDataItem(i).getValue().doubleValue() -
				result.getSeries(1).getDataItem(i).getValue().doubleValue()
			);
		}

		return result;
	}
}
