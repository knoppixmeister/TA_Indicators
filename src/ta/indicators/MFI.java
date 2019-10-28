package ta.indicators;

import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

public class MFI {
	public static TimeSeries run(final OHLCSeries data, final int length) {
		final TimeSeries result = new TimeSeries("MFI");

		final TimeSeries tpTs = new TimeSeries("TP");
		for(int key=0; key<data.getItemCount(); key++) {
			tpTs.add(
				data.getPeriod(key),
				(
					((OHLCItem)data.getDataItem(key)).getHighValue()+
					((OHLCItem)data.getDataItem(key)).getLowValue()+
					((OHLCItem)data.getDataItem(key)).getCloseValue()
				) / 3.0
			);
		}

		final TimeSeries changeTs = Utils.change(tpTs);

		final TimeSeries period1PosMFTs = new TimeSeries("");
		final TimeSeries period1NegMFTs = new TimeSeries("");
		for(int key=0; key<data.getItemCount(); key++) {
			if(key == 0) {
				period1PosMFTs.add(data.getPeriod(key), 0);
				period1NegMFTs.add(data.getPeriod(key), 0);
			}
			else {
				if(changeTs.getDataItem(key).getValue().doubleValue() > 0) {
					period1PosMFTs.add(
						data.getPeriod(key),
						tpTs.getDataItem(key).getValue().doubleValue() *
						((OHLCItem)data.getDataItem(key)).getVolume()
					);
				}
				else period1PosMFTs.add(data.getPeriod(key), 0);

				if(changeTs.getDataItem(key).getValue().doubleValue() < 0) {
					period1NegMFTs.add(
						data.getPeriod(key),
						tpTs.getDataItem(key).getValue().doubleValue() *
						((OHLCItem)data.getDataItem(key)).getVolume()
					);
				}
				else period1NegMFTs.add(data.getPeriod(key), 0);
			}
		}

		for(int key=0; key<data.getItemCount(); key++) {
			if(key < length) result.add(data.getPeriod(key), 0);
			else {
				double periodPosMFSum = 0;
				double periodNegMFSum = 0;

				for(int i=key; i>key-length; i--) {
					periodPosMFSum += period1PosMFTs.getDataItem(i).getValue().doubleValue();
					periodNegMFSum += period1NegMFTs.getDataItem(i).getValue().doubleValue();
				}

				double MFI = 100 - (100 / (1 + (periodPosMFSum / periodNegMFSum)));

				result.add(data.getPeriod(key), MFI);
			}
		}

		return result;
	}
}
