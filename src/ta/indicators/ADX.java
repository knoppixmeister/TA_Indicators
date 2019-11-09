package ta.indicators;

import org.jfree.chart.util.Args;
import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

public class ADX {
	public static TimeSeriesCollection run(final OHLCSeries data, final int period) {
		Args.nullNotPermitted(data, "data");
		Args.requireNonNegative(period, "period");

		final TimeSeriesCollection resultTsc = new TimeSeriesCollection();

		TimeSeries trueRangeTs 							= new TimeSeries("TRUE_RANGE");
		TimeSeries DirectionalMovementPlusTs 			= new TimeSeries("");
		TimeSeries DirectionalMovementMinusTs 			= new TimeSeries("");
		TimeSeries SmoothedTrueRangeTs 					= new TimeSeries("");
		TimeSeries SmoothedDirectionalMovementPlusTs 	= new TimeSeries("");
		TimeSeries SmoothedDirectionalMovementMinusTs 	= new TimeSeries("");

		final TimeSeries DIPlusTs 	= new TimeSeries("");
		final TimeSeries DIMinusTs 	= new TimeSeries("");
		final TimeSeries DXTs 		= new TimeSeries("");

		for(int key=0; key<data.getItemCount(); key++) {
			double high = ((OHLCItem)data.getDataItem(key)).getHighValue();
			double low = ((OHLCItem)data.getDataItem(key)).getLowValue();
			//double close = ((OHLCItem)data.getDataItem(key)).getCloseValue();
			double close1 = key == 0 ?	0 :
										((OHLCItem)data.getDataItem(key-1)).getCloseValue();
			double high1 = key == 0 ? 	0 :
										((OHLCItem)data.getDataItem(key-1)).getHighValue();
			double low1 = key == 0 ? 	0:
										((OHLCItem)data.getDataItem(key-1)).getLowValue();

			trueRangeTs.add(
				data.getPeriod(key),
				Math.max(Math.max(high-low, Math.abs(high-close1)), Math.abs(low-close1))
			);

			DirectionalMovementPlusTs.add(
				data.getPeriod(key),
				high - high1 > low1-low ? Math.max(high-high1, 0) : 0
			);

			DirectionalMovementMinusTs.add(
				data.getPeriod(key),
				low1-low > high - high1 ? Math.max(low1-low, 0) : 0
			);

			SmoothedTrueRangeTs.add(
				data.getPeriod(key),
				key == 	0 ?
						trueRangeTs.getDataItem(key).getValue().doubleValue() :
						SmoothedTrueRangeTs.getDataItem(key-1).getValue().doubleValue() - 
						(
							SmoothedTrueRangeTs.getDataItem(key-1).getValue().doubleValue() /
							(double)period
						) +
						trueRangeTs.getDataItem(key).getValue().doubleValue()
			);

			SmoothedDirectionalMovementPlusTs.add(
				data.getPeriod(key),
				key == 	0 ?
						DirectionalMovementPlusTs.getDataItem(key).getValue().doubleValue() :
						SmoothedDirectionalMovementPlusTs.getDataItem(key-1).getValue().doubleValue() -
						(
							SmoothedDirectionalMovementPlusTs.getDataItem(key-1).getValue().doubleValue() /
							(double)period
						) +
						DirectionalMovementPlusTs.getDataItem(key).getValue().doubleValue()
			);

			SmoothedDirectionalMovementMinusTs.add(
				data.getPeriod(key),
				key == 	0 ?
						DirectionalMovementMinusTs.getDataItem(key).getValue().doubleValue() :
						SmoothedDirectionalMovementMinusTs.getDataItem(key-1).getValue().doubleValue() -
						(
							SmoothedDirectionalMovementMinusTs.getDataItem(key-1).getValue().doubleValue() /
							(double)period
						) +
						DirectionalMovementMinusTs.getDataItem(key).getValue().doubleValue()
			);

			DIPlusTs.add(
				data.getPeriod(key),
				SmoothedDirectionalMovementPlusTs.getDataItem(key).getValue().doubleValue() /
				SmoothedTrueRangeTs.getDataItem(key).getValue().doubleValue() 
				* 100
			);

			DIMinusTs.add(
				data.getPeriod(key),
				SmoothedDirectionalMovementMinusTs.getDataItem(key).getValue().doubleValue() /
				SmoothedTrueRangeTs.getDataItem(key).getValue().doubleValue() 
				* 100
			);

			DXTs.add(
				data.getPeriod(key),
				Math.abs(
					DIPlusTs.getDataItem(key).getValue().doubleValue() -
					DIMinusTs.getDataItem(key).getValue().doubleValue()
				) /
				(
					DIPlusTs.getDataItem(key).getValue().doubleValue() +
					DIMinusTs.getDataItem(key).getValue().doubleValue()
				)
				* 100
			);
		}

		final TimeSeries ADXTs = ta.indicators.SMA.run(DXTs, period);

		resultTsc.addSeries(ADXTs);
		resultTsc.addSeries(DIPlusTs);
		resultTsc.addSeries(DIMinusTs);

		return resultTsc;
	}
}
