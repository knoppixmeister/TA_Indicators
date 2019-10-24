package ta.indicators;

import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

public class SMA {
	public static enum SMA_DATA_SOURCE {
		OPEN,
		HIGH,
		LOW,
		CLOSE,

		/*
		HL2,
		HLC3,
		*/
		OHLC4,

		VOLUME,
	}

	public static TimeSeries run(OHLCSeries data, int period) {
		return run(data, period, SMA_DATA_SOURCE.CLOSE);
	}

	public static TimeSeries run(final OHLCSeries data, final int period, final SMA.SMA_DATA_SOURCE dataSource) {
		final TimeSeries result = new TimeSeries("SMA_"+period+"_"+dataSource);

		double sum = 0, sma = 0;
		for(int key=0; key < data.getItemCount(); key++) {
			if(key < period-1) {
				result.add(((OHLCItem)data.getDataItem(key)).getPeriod(), 0);
			}

			if(key >= period-1) {
				sum = 0;
				sma = 0;

				for(int i = key-(period-1); i <= key; i++) {
					if(dataSource == SMA_DATA_SOURCE.OPEN) {
						sum += ((OHLCItem) data.getDataItem(i)).getOpenValue();
					}
					else if(dataSource == SMA_DATA_SOURCE.HIGH) {
						sum += ((OHLCItem) data.getDataItem(i)).getHighValue();
					}
					else if(dataSource == SMA_DATA_SOURCE.LOW) {
						sum += ((OHLCItem) data.getDataItem(i)).getLowValue();
					}
					else if(dataSource == SMA_DATA_SOURCE.CLOSE) {
						sum += ((OHLCItem) data.getDataItem(i)).getCloseValue();
					}
					else if(dataSource == SMA_DATA_SOURCE.VOLUME) {
						sum += ((OHLCItem) data.getDataItem(i)).getVolume();
					}
					else if(dataSource == SMA_DATA_SOURCE.OHLC4) {
						sum += (
							((OHLCItem) data.getDataItem(i)).getOpenValue() +
							((OHLCItem) data.getDataItem(i)).getHighValue() +
							((OHLCItem) data.getDataItem(i)).getLowValue() +
							((OHLCItem) data.getDataItem(i)).getCloseValue()
						) / 4.0;
					}
				}

				sma = sum / (double)period;

				result.add(
					((OHLCItem)data.getDataItem(key)).getPeriod(),
					sma
				);
			}
		}

		return result;
	}

	public static TimeSeries run(TimeSeries data, int period) {
		TimeSeries result = new TimeSeries("");

		double sum = 0, sma = 0;
		for(int key=0; key < data.getItemCount(); key++) {
			if(key < period-1) {
				result.add(data.getDataItem(key).getPeriod(), 0);
			}

			if(key >= period-1) {
				sum = 0;
				sma = 0;

				for(int i = key-(period-1); i <= key; i++) {
					sum += data.getDataItem(i).getValue().doubleValue();
				}

				sma = sum / (double)period;

				result.add(
					data.getDataItem(key).getPeriod(),
					sma
				);
			}
		}

		return result;
	}
}
