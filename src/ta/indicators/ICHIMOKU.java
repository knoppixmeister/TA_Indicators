package ta.indicators;

import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

public class ICHIMOKU {
	/*
		middleDonchian(Length) => avg(highest(Length), lowest(Length))

		conversionPeriods 	= input(9, minval=1)
		basePeriods			= input(26, minval=1)
		laggingSpan2Periods = input(52, minval=1)
		displacement 		= input(26, minval=1)

		Tenkan 	= middleDonchian(conversionPeriods)
		Kijun 	= middleDonchian(basePeriods)

		xChikou = close
		SenkouA = middleDonchian(laggingSpan2Periods)

		SenkouB = (Tenkan[basePeriods] + Kijun[basePeriods]) / 2

		plot(Tenkan, color=red, title="Tenkan")
		plot(Kijun, color=blue, title="Kijun")
		//plot(xChikou, color=teal, title="Chikou", offset = -displacement)

		A = plot(SenkouA[displacement], color=purple, title="SenkouA")
		B = plot(SenkouB, color=green, title="SenkouB")

		fill(A, B, color=green)
	*/

	public static TimeSeriesCollection run(
		OHLCSeries data,
		int conversionPeriod,
		int basePeriod,
		int laggingSpan2Period,
		int displacement)
	{
		if(basePeriod < 1) basePeriod = 1;
		if(displacement < 1) displacement = 1;

		final TimeSeriesCollection result = new TimeSeriesCollection();

		final TimeSeries TenkanTs	=	middleDonchian(data, conversionPeriod);
		TenkanTs.setKey("Tenkan_Conversion");

		final TimeSeries KijunTs 	=	middleDonchian(data, basePeriod);
		KijunTs.setKey("Kijun_Base");

		final TimeSeries SenkouATs	=	middleDonchian(data, laggingSpan2Period);
		SenkouATs.setKey("SenkouA_LaggngSpan2");

		final TimeSeries SenkouBTs = new TimeSeries("SenkouB_GreenCloud");
		for(int key=TenkanTs.getItemCount()-1; key >= 0; key--) {
			if(key >= basePeriod) {
				SenkouBTs.addOrUpdate(
					data.getPeriod(key),
					(
						TenkanTs.getDataItem(key-basePeriod).getValue().doubleValue() +
						KijunTs.getDataItem(key-basePeriod).getValue().doubleValue()
					) / 2.0
				);
			}
			else SenkouBTs.addOrUpdate(data.getPeriod(key), 0);
		}

		result.addSeries(TenkanTs);
		result.addSeries(KijunTs);

		final TimeSeries aTs = new TimeSeries("SenkouA_Lead2_RedCloud");
		for(int key=SenkouATs.getItemCount()-1; key >= 0; key--) {
			if(key >= displacement) {
				aTs.add(
					data.getPeriod(key),
					SenkouATs.getDataItem(key-displacement).getValue().doubleValue()
				);
			}
			else aTs.add(data.getPeriod(key), 0);
		}
		result.addSeries(aTs);
		result.addSeries(SenkouBTs);

		return result;
	}

	private static TimeSeries middleDonchian(final OHLCSeries data, final int length) {
		final TimeSeries highTs = new TimeSeries("HIGHs");
		final TimeSeries lowTs = new TimeSeries("LOWs");

		for(int key=data.getItemCount()-1; key >= 0; key--) {
			highTs.add(data.getPeriod(key), ((OHLCItem)data.getDataItem(key)).getHighValue());
			lowTs.add(data.getPeriod(key), ((OHLCItem)data.getDataItem(key)).getLowValue());
		}

		final TimeSeries highestHighTs 	= Utils.highest(highTs, length);
		final TimeSeries lowestLowTs 	= Utils.lowest(lowTs, length);

		final TimeSeries avgTs = new TimeSeries("");
		for(int key=highestHighTs.getItemCount()-1; key >= 0; key--) {
			avgTs.add(
				highestHighTs.getTimePeriod(key),
				(
					highestHighTs.getDataItem(key).getValue().doubleValue() + 
					lowestLowTs.getDataItem(key).getValue().doubleValue()
				) / 2.0
			);
		}

		return avgTs;
	}
}
