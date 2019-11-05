package ta.indicators;

import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

public class CCTBBO {
	/*
		// CCTBBO_LB

		study("CCT Bollinger Band Oscillator", shorttitle="CCTBBO_LB", precision=8)

		length      = input(21)
		lengthMA    = input(13)
		src = close
		cctbbo = 100 * (src + 2*stdev(src, length) - sma(src, length)) / (4 * stdev(src, length))

		ul = hline(100, color=gray)
		ll = hline(0, color=gray)
		hline(50, color=gray)
		fill(ul,ll, color=blue)

		plot(cctbbo, color=blue, linewidth=2)
		plot(ema(cctbbo, lengthMA), color=red)
	*/
	public static TimeSeries run(OHLCSeries data, int length, int lengthMa) {
		final TimeSeries result = new TimeSeries("CCTBBO");

		final TimeSeries srcTs = new TimeSeries("");
		for(int i=0; i<data.getItemCount(); i++) {
			srcTs.add(data.getPeriod(i), ((OHLCItem)data.getDataItem(i)).getCloseValue());
		}

		final TimeSeries smaTs = SMA.run(srcTs, length);
		final TimeSeries stDevTs = BB.standardDev(srcTs, length);

		for(int i=0; i<data.getItemCount(); i++) {
			result.add(
				data.getPeriod(i),
				ta.indicators.Utils.round(
					100 * (
						srcTs.getDataItem(i).getValue().doubleValue() + 2 * stDevTs.getDataItem(i).getValue().doubleValue() -
						smaTs.getDataItem(i).getValue().doubleValue()
					)
					/
					(4 * stDevTs.getDataItem(i).getValue().doubleValue()),
					8
				)
			);
		}

		return result;
	}
}
