package neil.demo.amsjug2018;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import lombok.Data;

/**
 * <p>A specific exchange rate associated with a currency pair.
 * The source currency converts to the target currency at the
 * given time and at the given rate.
 * </p>
 * <p>In more realistic examples the time is more of an instant
 * in time than a date, and the exchange rate might have a
 * spread of exchange rates depending on various factors such
 * as if buying or selling, the amount, etc.
 * </p>
 */
@SuppressWarnings("serial")
@Data
public class CurrencyPairValue implements Serializable {
	
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate day;
	private BigDecimal rate;
}
