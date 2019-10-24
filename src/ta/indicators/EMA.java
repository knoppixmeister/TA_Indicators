package ta.indicators;

import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.util.Args;
import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

public class EMA {
	public static enum EMA_DATA_SOURCE {
		OPEN,
		HIGH,
		LOW,
		CLOSE,

		/*
		HL2,
		HLC3,
		OHLC4,
		*/

		VOLUME,
	}

	public static TimeSeries run(OHLCSeries data, int period) {
		return run(data, period, EMA_DATA_SOURCE.CLOSE);
	}

	public static TimeSeries run(OHLCSeries data, int period, EMA_DATA_SOURCE emaDataSource) {
		Args.nullNotPermitted(data, "data");
		Args.requireNonNegative(period, "period");

		if(period < 1) period = 1;

		final TimeSeries result = new TimeSeries("EMA_"+period);

		final double smoothing_constant	= 2.0 / ((double)period + 1.0);
		Double previous_EMA 			= null;

		double calcValue;
		for(int key=0; key < data.getItemCount(); key++) {
			if(key < period-1) {
	 			result.add(((OHLCItem)data.getDataItem(key)).getPeriod(), 0);
			}
			else if(key >= period-1) {
	 			if(previous_EMA == null) {
	 				double sum = 0;

	 				for(int i = key - (period-1); i <= key; i++) {
	 					if(emaDataSource == EMA_DATA_SOURCE.OPEN) {
	 						calcValue = ((OHLCItem)data.getDataItem(i)).getOpenValue();
	 					}
	 					else if(emaDataSource == EMA_DATA_SOURCE.HIGH) {
	 						calcValue = ((OHLCItem)data.getDataItem(i)).getHighValue();
	 					}
	 					else if(emaDataSource == EMA_DATA_SOURCE.LOW) {
	 						calcValue = ((OHLCItem)data.getDataItem(i)).getLowValue();
	 					}
	 					else if(emaDataSource == EMA_DATA_SOURCE.CLOSE) {
	 						calcValue = ((OHLCItem)data.getDataItem(i)).getCloseValue();
	 					}
	 					else if(emaDataSource == EMA_DATA_SOURCE.VOLUME) {
	 						calcValue = ((OHLCItem)data.getDataItem(i)).getVolume();
	 					}
	 					else continue;

	 					sum += calcValue;
	 				}

	 				double sma = sum / (double)period;

	 				result.add(data.getPeriod(key), sma);
	 				previous_EMA = sma;
	 			}
	 			else {
	 				if(emaDataSource == EMA_DATA_SOURCE.OPEN) {
 						calcValue = ((OHLCItem)data.getDataItem(key)).getOpenValue();
 					}
	 				else if(emaDataSource == EMA_DATA_SOURCE.HIGH) {
	 					calcValue = ((OHLCItem)data.getDataItem(key)).getHighValue();
	 				}
	 				else if(emaDataSource == EMA_DATA_SOURCE.LOW) {
	 					calcValue = ((OHLCItem)data.getDataItem(key)).getLowValue();
	 				}
	 				else if(emaDataSource == EMA_DATA_SOURCE.CLOSE) {
 						calcValue = ((OHLCItem)data.getDataItem(key)).getCloseValue();
 					}
	 				else if(emaDataSource == EMA_DATA_SOURCE.VOLUME) {
 						calcValue = ((OHLCItem)data.getDataItem(key)).getVolume();
 					}
 					else continue;

	 				double ema = (calcValue - previous_EMA) * smoothing_constant + previous_EMA;

	 				result.add(data.getPeriod(key), ema);
	 				previous_EMA = ema;
	 			}
			}
		}

		return result;
	}

	public static TimeSeries run(final TimeSeries data, final int period) {
		final TimeSeries result = new TimeSeries("EMA_"+period);

		final double smoothing_constant	=	2.0 / ((double)period + 1.0);
		Double previous_EMA				=	null;

		double sum = 0, sma = 0, ema = 0;
		for(int key=0; key<data.getItemCount(); key++) {
	 		if(key < period-1) result.add(data.getTimePeriod(key), 0);
	 		else if(key >= period-1) {
	 			if(previous_EMA == null) {
	 				sum = 0;
	 				for(int i = key-(period-1); i <= key; i++) {
	 					sum += data.getDataItem(i).getValue().doubleValue();
	 				}

	 				sma = sum / (double)period;

	 				result.add(data.getDataItem(key).getPeriod(), sma);
	 				previous_EMA = sma;
	 			}
	 			else {
	 				ema = (data.getDataItem(key).getValue().doubleValue() - previous_EMA) * smoothing_constant + previous_EMA;

	 				result.add(data.getTimePeriod(key), ema);
	 				previous_EMA = ema;
	 			}
	 		}
		}

		return result;
	}

	public static List<Double> run(List<Double> data, int period) {
		List<Double> result = new ArrayList<>();

		double smoothing_constant 	= 2 / ((double)period + 1);
	 	double previous_EMA 		= Double.MIN_VALUE;
	
		for(int key=0; key < data.size(); key++) {
	 		if(key >= period) {
	 			if(previous_EMA == Double.MIN_VALUE) {
	 				double sum = 0;
	 				for(int i = key - (period-1); i <= key; i++) {
	 					sum +=	data.get(i);
	 				}

	 				double sma = sum / (double)period;

	 				result.add(sma);
	 				previous_EMA = sma;
	 			}
	 			else {
	 				double ema = (data.get(key) - previous_EMA) * smoothing_constant + previous_EMA;

	 				result.add(ema);
	 				previous_EMA = ema;
	 			}
	 		}
	 	}

		return result;
	}
}
