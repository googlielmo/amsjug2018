package demo.btcdx2018;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>A currency pair.
 * </p>
 * <p>"{@code USD}/{@code EUR}" represents the conversion <u>from</u> (the "base")
 * US Dollars to Euros (the "quote").
 * </p>
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyPairKey implements Serializable {
	
	private String base;
	private String quote;
}
