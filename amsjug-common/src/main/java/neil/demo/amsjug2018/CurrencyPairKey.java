package neil.demo.amsjug2018;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>A currency pair.
 * </p>
 * <p>"{@code USD}/{@code EUR}" represents the conversion <u>from</u> (the "base")
 * US Dollars to Euros (the "quote").
 * </p>
 */
@SuppressWarnings("serial")
@Data
public class CurrencyPairKey implements Serializable {
	
	private String base;
	private String quote;
}
