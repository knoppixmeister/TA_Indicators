package ta.indicators;

import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.OHLCSeries;

public class BB_TSI {
	/*
		length 		= input(20, minval=1, title="BB Periods")
		deviations 	= input(2, minval=0.0001, title="Deviations")
		long 		= input(9, minval=1, title="TSI Long")
		short 		= input(3, minval=1, title="TSI Short")
		average 	= input(50, minval=2, title="SMA Length")

		TSI 	= tsi(close, short, long)
		Std 	= stdev(TSI, length)
		Upper 	= (Std * deviations + (sma(TSI, length)))
		Lower 	= sma(TSI, length) - (Std * deviations)
		SMA		= sma(TSI, average)

		plot(TSI, color=blue, style=line, linewidth=1)
		B1 = plot(Upper, color=black, style=line, linewidth=1)
		B2 = plot(Lower, color=black, style=line, linewidth=1)
		plot(SMA, color=red, style=line, linewidth=1)
		fill(B1, B2, color=black, transp=90)
	*/

	public static TimeSeriesCollection run(OHLCSeries data, int bbLength, double deviations, int tsiLong, int tsiShort, int average) {
		TimeSeriesCollection result = new TimeSeriesCollection();

		TimeSeries tsiTs = TSI.run(data, tsiLong, tsiShort);
		TimeSeries stdTs = BB.standardDev(tsiTs, bbLength);

		// TimeSeries tsiTs = new TimeSeries("TSI");

		TimeSeries smaTs = SMA.run(tsiTs, bbLength);

		// upper = Std * deviations + sma(TSI, length)
		// Lower = sma(TSI, length) - (Std * deviations)
		TimeSeries upperTs = new TimeSeries("UPPER");
		TimeSeries lowerTs = new TimeSeries("LOWER"); 
		for(int key=data.getItemCount()-1; key >= 0; key--) {
			upperTs.add(
				data.getPeriod(key),
				(stdTs.getDataItem(key).getValue().doubleValue() * deviations + smaTs.getDataItem(key).getValue().doubleValue())/100d
			);
			lowerTs.add(
				data.getPeriod(key),
				(
					smaTs.getDataItem(key).getValue().doubleValue() -
					(
						stdTs.getDataItem(key).getValue().doubleValue() * deviations 
					)
				) / 100d
			);

			stdTs.addOrUpdate(
				data.getPeriod(key),
				stdTs.getDataItem(key).getValue().doubleValue() / 100.0
			);
			/*
			tsiTs.add(
				data.getPeriod(key),
				tsiTs.getDataItem(key).getValue().doubleValue() / 100.0
			);
			*/
		}

		result.addSeries(tsiTs);
		result.addSeries(upperTs);
		result.addSeries(lowerTs);
		result.addSeries(SMA.run(tsiTs, average));

		/*
		System.out.println(
			"TSI: "+tsiTs.getDataItem(tsiTs.getItemCount()-1).getValue()
		);
		System.out.println(
			"STD: "+stdTs.getDataItem(stdTs.getItemCount()-1).getValue().doubleValue()
		);
		System.out.println(
			"UPPER: "+upperTs.getDataItem(upperTs.getItemCount()-1).getValue().doubleValue()
		);
		System.out.println(
			"LOWER: "+lowerTs.getDataItem(lowerTs.getItemCount()-1).getValue().doubleValue()
		);
		System.out.println(
			"SMA: "+result.getSeries(3).getDataItem(result.getSeries(3).getItemCount()-1).getValue()
		);
		System.out.println("-----------------------------------------------------------------------------------------");
		*/

		return result;
	}
}
