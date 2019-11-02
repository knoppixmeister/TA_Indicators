package ta.indicators;

import org.jfree.data.time.ohlc.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class VWAP {
	public static double run(final OHLCSeries data) {
		final DateTime dt = new DateTime();
		final DateTime SDT = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(dt.toString().substring(0, dt.toString().indexOf("T")));

		double cumTpVol = 0;
		double cumVol = 0;

		// System.out.println("DAY_START_TM: "+SDT.getMillis()+" | "+SDT.toString().replaceAll("T", " "));

		OHLCItem cndl;
		double tp;
		for(int key=0; key<data.getItemCount(); key++) {
			cndl = (OHLCItem) data.getDataItem(key);

			if(cndl.getPeriod().getFirstMillisecond() < SDT.getMillis()) continue;

			tp = (cndl.getHighValue() + cndl.getLowValue() + cndl.getCloseValue())/3.0;
			cumTpVol += tp * cndl.getVolume();

			cumVol += cndl.getVolume();
		}

		return cumTpVol / cumVol;
	}
}


/*
	Существует пять шагов для расчета VWAP:

	1. Вычислите Типичную цену за период. [(High + Low + Close)/3)]
	2. Умножьте Типичную цену на период Объем (Typical Price x Volume)
	3. Создайте совокупную (кумулятивную) сумму типичной цены.  Cumulative(Typical Price x Volume)
	4. Создайте совокупное общее количество. Cumulative(Volume)
	5. Разделите кумулятивные итоги. 

	VWAP = Cumulative(Typical Price x Volume) / Cumulative(Volume)

	https://www.tradingview.com/wiki/Volume_Weighted_Average_Price_(VWAP)/ru
*
public class VWAP {
	public static TimeSeries run(final OHLCSeries data) {
		final TimeSeries result = new TimeSeries("VWAP");

		OHLCItem cndl;
		final TimeSeries hlc3Ts = new TimeSeries("HLC3");
		for(int key=0; key < data.getItemCount(); key++) {
			cndl = (OHLCItem) data.getDataItem(key);

			hlc3Ts.add(
				data.getPeriod(key),
				(cndl.getHighValue() + cndl.getLowValue() + cndl.getCloseValue()) / 3.0
			);
		}

		return result;
	}
}
*/