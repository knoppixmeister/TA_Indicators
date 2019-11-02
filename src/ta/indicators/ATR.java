package ta.indicators;

import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

public class ATR {
	public static TimeSeries run(final OHLCSeries data, final int period) {
		final TimeSeries result = new TimeSeries("ATR");

		double high_minus_low = 0;
		double high_minus_close_past = -1;
		double low_minus_close_past = -1;
		double tr = 0;
		double tr_sum = 0;
		double previous_atr = 0;

		for(int key=0; key < data.getItemCount(); key++) {
			high_minus_low = ((OHLCItem)data.getDataItem(key)).getHighValue() - ((OHLCItem)data.getDataItem(key)).getLowValue();

			if(key >= 0 && key < period-1) result.add(data.getPeriod(key), 0);

			if(key >= 1) {
				high_minus_close_past = Math.abs(
											((OHLCItem)data.getDataItem(key)).getHighValue() - 
											((OHLCItem)data.getDataItem(key-1)).getCloseValue()
										);
				low_minus_close_past = 	Math.abs(
											((OHLCItem)data.getDataItem(key)).getLowValue() - 
											((OHLCItem)data.getDataItem(key-1)).getCloseValue()
										);
			}

			if(key == 0) {
				tr = high_minus_low;
				tr_sum += tr;
			}
			if(high_minus_close_past > -1 && low_minus_close_past > -1) {
				tr = Math.max(high_minus_low, Math.max(high_minus_close_past, low_minus_close_past));

				if(key <= period) tr_sum += tr;
			}

			//first ATR
			double atr;
			if(key == period-1) {
				atr = tr_sum / (double)period;
				previous_atr = atr;

				result.add(data.getPeriod(key), atr);
			}

			//remaining ATR
			if(key >= period) {
				atr = ((previous_atr * ((double)period-1)) + tr) / (double)period;				
				previous_atr = atr;

				result.add(data.getPeriod(key), atr);
			}
		}

		return result;
	}
}
