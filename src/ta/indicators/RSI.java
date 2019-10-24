package ta.indicators;

import java.util.*;
import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

// https://github.com/hurdad/doo-forex/blob/master/protected/class/Technical%20Indicators/RSI.php
public class RSI {
	public static TimeSeries run(OHLCSeries data, int period) {
		final TimeSeries result = new TimeSeries("RSI");
		List<Double> changeArray = new ArrayList<>();

		for(int key=0; key < data.getItemCount(); key++) {
			if(key >= 0 && key < period) {
				result.add(((OHLCItem)data.getDataItem(key)).getPeriod(), 0);
			}

			if(key > 1) {
				double change = ((OHLCItem)data.getDataItem(key)).getCloseValue() - ((OHLCItem)data.getDataItem(key-1)).getCloseValue();

				changeArray.add(0, change);

				if(changeArray.size() > period) changeArray.remove(changeArray.size()-1);
			}

			if(key >= period) {
				double sumGain = 0;
				double sumLoss = 0;

				for(int i=0; i<changeArray.size(); i++) {
					if(changeArray.get(i) >= 0) sumGain += changeArray.get(i);
					else if(changeArray.get(i) < 0) sumLoss += Math.abs(changeArray.get(i));
				}

				double avg_gain = sumGain / period;
				double avg_loss = sumLoss / period;

				double rsi = 0;
				if(avg_loss == 0) rsi = 100;
				else rsi = 100 - (100 / ( 1 + (avg_gain / avg_loss)));

				result.add(((OHLCItem)data.getDataItem(key)).getPeriod(), rsi);
			}
		}

		return result;
	}

	public static TimeSeries runSmooth(OHLCSeries data, int period) {
		final TimeSeries result = new TimeSeries("RSI_"+period);

		List<Double> changes 	= new ArrayList<>();
		List<Double> gains 		= new ArrayList<>();
		List<Double> losses 	= new ArrayList<>();
		changes.add(-1d);
		gains.add(-1d);
		losses.add(-1d);

		final double alpha = 	//2.0 / (1 + (double)period);
								1/(double)period;

		double avg_gain = 0, avg_loss = 0, rs = 0, rsi;
		double prev_avg_gain = 0, prev_avg_loss = 0;
		//System.out.println("KEY: 0; CL: "+Utils.round(((OHLCItem)data.getDataItem(0)).getCloseValue(), 8));
		for(int key=0; key<data.getItemCount(); key++) {
			//System.out.print("KEY: "+key+"; CL: "+((OHLCItem)data.getDataItem(key)).getCloseValue());

			if(key == 0) {
				result.add(data.getPeriod(key), 0);
				continue;
			}

			changes.add(
				((OHLCItem)data.getDataItem(key)).getCloseValue() -
				((OHLCItem)data.getDataItem(key-1)).getCloseValue()
			);

			//System.out.print("; CH: "+String.format("%.8f", changes.get(key)));

			gains.add(Utils.round(changes.get(key) > 0 ? changes.get(key) : 0, 8));	
			//System.out.print("; GAIN: "+String.format("%.8f", gains.get(key)));

			losses.add(Utils.round(changes.get(key) < 0 ? Math.abs(changes.get(key)) : 0, 8));
			//System.out.print("; LOSS: "+String.format("%.8f", losses.get(key)));

			if(key < period) {
				result.add(data.getPeriod(key), 0);
			}
			else if(key == period) {
				for(int i=key; i>=(key-period+1); i--) {
					avg_gain += gains.get(i);
					avg_loss += losses.get(i);
				}
				avg_gain = Utils.round(avg_gain/period, 8);
				avg_loss = Utils.round(avg_loss/period, 8);

				//System.out.print("; AVG_G_: "+String.format("%.8f", avg_gain));
				//System.out.print("; AVG_L_: "+String.format("%.8f", avg_loss));

				prev_avg_gain = avg_gain;
				prev_avg_loss = avg_loss;

				result.add(data.getPeriod(key), 0);
			}
			else if(key > period) {
				avg_gain = 0;
				avg_loss = 0;

				//System.out.print("; PREV_AVG_G: "+String.format("%.8f", prev_avg_gain)+"; GAIN_AGAIN: "+gains.get(key)+"; ALP: "+alpha);

				avg_gain = gains.get(key)*alpha+(1-alpha)*prev_avg_gain;
				avg_loss = losses.get(key)*alpha+(1-alpha)*prev_avg_loss;

				//System.out.print("; AVG_G_1: "+String.format("%.8f", avg_gain));
				//System.out.print("; AVG_L_1: "+String.format("%.8f", avg_loss));

				prev_avg_gain = avg_gain;
				prev_avg_loss = avg_loss;

				rs = avg_gain / avg_loss;
				rsi = avg_loss == 0 ? 100 : 100-(100/(1+rs));
				//System.out.print("; RS: "+rs+"; RSI: "+rsi);

				result.addOrUpdate(data.getPeriod(key), rsi);
			}

			//System.out.println("");
		}

		return result;
	}

	public static TimeSeries runSmooth(TimeSeries data, int period) {
		final TimeSeries result = new TimeSeries("RSI_"+period);

		List<Double> changes 	= new ArrayList<>();
		List<Double> gains 		= new ArrayList<>();
		List<Double> losses 	= new ArrayList<>();
		changes.add(-1d);
		gains.add(-1d);
		losses.add(-1d);

		final double alpha = 	//2.0 / (1 + (double)period);
								1 / (double)period;

		double avg_gain = 0, avg_loss = 0, rs = 0, rsi;
		double prev_avg_gain = 0, prev_avg_loss = 0;
		//System.out.println("KEY: 0; CL: "+Utils.round(((OHLCItem)data.getDataItem(0)).getCloseValue(), 8));
		for(int key=0; key<data.getItemCount(); key++) {
			//System.out.print("KEY: "+key+"; CL: "+((OHLCItem)data.getDataItem(key)).getCloseValue());

			if(key == 0) {
				result.add(data.getDataItem(key).getPeriod(), 0);
				continue;
			}

			changes.add(
				data.getDataItem(key).getValue().doubleValue() -
				data.getDataItem(key-1).getValue().doubleValue()
			);

			//System.out.print("; CH: "+String.format("%.8f", changes.get(key)));

			gains.add(Utils.round(changes.get(key) > 0 ? changes.get(key) : 0, 8));	
			//System.out.print("; GAIN: "+String.format("%.8f", gains.get(key)));

			losses.add(Utils.round(changes.get(key) < 0 ? Math.abs(changes.get(key)) : 0, 8));
			//System.out.print("; LOSS: "+String.format("%.8f", losses.get(key)));

			if(key < period) {
				result.add(data.getDataItem(key).getPeriod(), 0);
			}
			else if(key == period) {
				for(int i=key; i>=(key-period+1); i--) {
					avg_gain += gains.get(i);
					avg_loss += losses.get(i);
				}
				avg_gain = Utils.round(avg_gain/period, 8);
				avg_loss = Utils.round(avg_loss/period, 8);

				//System.out.print("; AVG_G_: "+String.format("%.8f", avg_gain));
				//System.out.print("; AVG_L_: "+String.format("%.8f", avg_loss));

				prev_avg_gain = avg_gain;
				prev_avg_loss = avg_loss;

				result.add(data.getDataItem(key).getPeriod(), 0);
			}
			else if(key > period) {
				avg_gain = 0;
				avg_loss = 0;

				//System.out.print("; PREV_AVG_G: "+String.format("%.8f", prev_avg_gain)+"; GAIN_AGAIN: "+gains.get(key)+"; ALP: "+alpha);

				avg_gain = gains.get(key)*alpha+(1-alpha)*prev_avg_gain;
				avg_loss = losses.get(key)*alpha+(1-alpha)*prev_avg_loss;

				//System.out.print("; AVG_G_1: "+String.format("%.8f", avg_gain));
				//System.out.print("; AVG_L_1: "+String.format("%.8f", avg_loss));

				prev_avg_gain = avg_gain;
				prev_avg_loss = avg_loss;

				rs = avg_gain / avg_loss;
				rsi = avg_loss == 0 ? 100 : 100-(100/(1+rs));
				//System.out.print("; RS: "+rs+"; RSI: "+rsi);

				result.addOrUpdate(data.getDataItem(key).getPeriod(), rsi);
			}

			//System.out.println("");
		}

		return result;
	}
}
