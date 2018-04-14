package neil.demo.amsjug2018;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Capture various values about a currency pair,
 * the current price and some moving averages.
 * </p>
 * <p>Ideally these will be for the same date, but
 * the moving averages don't start to produce data
 * until enough input arrives whereas the current
 * price is (unsurprisingly) output for each input.
 * </p>
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyPairInfo implements Serializable {

	private CurrencyPairValue currentActual;
	private CurrencyPairValue averageOf50;
	private CurrencyPairValue averageOf200;

}
