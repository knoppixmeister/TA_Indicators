package ta.indicators;

import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;

public class ALMA {
	/*
	alma(series, length, offset, sigma) → series

	plot(alma(close, 9, 0.85, 6))

	// same on pine, but much less efficient
	pine_alma(series, windowsize, offset, sigma) =>
	    m = floor(offset * (windowsize - 1))
	    s = windowsize / sigma
	    norm = 0.0
	    sum = 0.0
	    for i = 0 to windowsize - 1
	        weight 	= 	exp(-1 * pow(i - m, 2) / (2 * pow(s, 2)))
	        norm 	:= 	norm + weight
	        sum 	:= 	sum + series[windowsize - i - 1] * weight
	    sum / norm

	plot(pine_alma(close, 9, 0.85, 6))

	*/

	public static TimeSeries run(OHLCSeries data, int windowsize, double offset, int sigma) {
		final TimeSeries result = new TimeSeries("");

		double m = Math.floor(offset * ((double)windowsize - 1));
		double s = (double) windowsize / (double)sigma;

		// System.out.println("M: "+m);
		// System.out.println("S: "+s);

		for(int key=data.getItemCount()-1; key >= 0; key--) {
			if(key <= windowsize*2) result.add(data.getPeriod(key), 0);
			else {
				double norm = 0;
				double sum = 0;

				double weight = 0;
				for(int i=windowsize-1; i >= 0; i--) {
					weight = Math.exp(-1 * Math.pow((double)i-m, 2)/(2*Math.pow(s, 2)));

					/*
					if(key == data.getItemCount()-1) {
						System.out.println(i+") wg: "+String.format("%.8f", weight).replace(",", "."));
						
						System.out.println("------------------------------------------------------------------");
					}
					*/

					norm += weight;

					/*
					if(key == data.getItemCount()-1) {
						System.out.println("cl: "+String.format("%.8f", ((OHLCItem)data.getDataItem(key-i)).getCloseValue()).replace(",", "."));
					}
					*/

					sum += ((OHLCItem)data.getDataItem(key-i)).getCloseValue() * weight;
				}

				if(key == data.getItemCount()-1) {
					System.out.println("sum: "+String.format("%.8f", sum).replace(",", "."));
				}

				//result.add(data.getPeriod(key), );// sum/norm
			}
		}

		return result;
	}
}
