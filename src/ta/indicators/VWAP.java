package ta.indicators;

import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

/*
	Существует пять шагов для расчета VWAP:

	1. Вычислите Типичную цену за период. [(High + Low + Close)/3)]
	2. Умножьте Типичную цену на период Объем (Typical Price x Volume)
	3. Создайте совокупную (кумулятивную) сумму типичной цены.  Cumulative(Typical Price x Volume)
	4. Создайте совокупное общее количество. Cumulative(Volume)
	5. Разделите кумулятивные итоги. 

	VWAP = Cumulative(Typical Price x Volume) / Cumulative(Volume)

	https://www.tradingview.com/wiki/Volume_Weighted_Average_Price_(VWAP)/ru
*/
public class VWAP {
	public static TimeSeries run(final OHLCSeries data, final int period) {
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
