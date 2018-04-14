package neil.demo.amsjug2018.jet;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Map;

import com.hazelcast.jet.core.AbstractProcessor;
import com.hazelcast.jet.datamodel.Tuple2;

import lombok.extern.slf4j.Slf4j;
import neil.demo.amsjug2018.Constants;
import neil.demo.amsjug2018.CurrencyPairInfo;
import neil.demo.amsjug2018.CurrencyPairKey;
import neil.demo.amsjug2018.CurrencyPairValue;

/**
 * <p>A processor to combine the calculated moving average prices
 * into a single object, holding the current price (average of 1)
 * and the 50 point and 200 point moving averages.
 * </p>
 * <p>This processor has three inputs, one from each of the
 * average producers. When it receives a price from any of
 * these three inputs it updates the working copy of the
 * combined priced object and writes it as output.
 * </p>
 * <p>So this processor produces one output for one input.
 * Given each Bitcoin price is fed into three averagers
 * which feed into this processor, one change to the Bitcoin
 * price results in three inputs to this processor and
 * three outputs of the combined.
 * </p>
 * <p>We <i>could</i> wait for one input from each channel
 * before producing output, and this would be a form of
 * reduction. However, we will receive 199 Bitcoin current
 * prices before we can receive any 200 point average.
 * It could still be worth doing, it just complicates the
 * input handling.
 * </p>
 */
@Slf4j
public class PriceCombinerProcessor extends AbstractProcessor {

	private LocalDate lastCurrentActualDay = null;
	private BigDecimal lastCurrentActualRate = null;
	private LocalDate lastAverageOf50Day = null;
	private BigDecimal lastAverageOf50Rate = null;
	private LocalDate lastAverageOf200Day = null;
	private BigDecimal lastAverageOf200Rate = null;
	
	/**
	 * <p>All inputs are the same format of data, a {@link Tuple2}
	 * where the first field is the date of the price and the second
	 * field is the price itself. For averages, the date is the date
	 * of the last price.
	 * </p>
	 * <p>Processing here assumes the {@link com.hazelcast.jet.core.Edge Edge}
	 * connections have been set up so that the first one ({@code ordinal 0})
	 * is from the current price stream, the second ({@code ordinal 1}) is
	 * from the 50 point average, and the third ({@code ordinal 2}) from
	 * the 200 point average.
	 * </p>
	 * <p>Output is a map entry, since we'll ultimately save this
	 * data to a Hazelcast {@link com.hazelcast.core.IMap IMap}.
	 * Earlier processing has stripped out the key "{@code BTC/USD}"
	 * so add it back in again.
	 * </p>
	 * <p>We use {@link #tryEmit} to send out. This might fail if
	 * the output queue is full and {@link PriceCombinerProcessor#tryProcess}
	 * gets called again for the same data. Not a problem, as we just
	 * overwrite the {@code currencyPairInfo} field with the same value.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected boolean tryProcess(int ordinal, Object item) {

		// Re-cast input
		Tuple2<LocalDate, BigDecimal> tuple2 = 
			(Tuple2<LocalDate, BigDecimal>) item;

		// Three input edges, one from each kind of average calculation
		switch (ordinal) {
			case 0:
				this.lastCurrentActualDay = tuple2.getKey();
				this.lastCurrentActualRate = tuple2.getValue();
				break;
			case 1:
				this.lastAverageOf50Day = tuple2.getKey();
				this.lastAverageOf50Rate = tuple2.getValue();
				break;
			case 2:
				this.lastAverageOf200Day = tuple2.getKey();
				this.lastAverageOf200Rate = tuple2.getValue();
				break;
			default:
				log.error("Ordinal {}", ordinal);
				return true;
		}

		// Prepare output #1 - Map.Entry.Value
		CurrencyPairValue currentActual = new CurrencyPairValue(this.lastCurrentActualDay, this.lastCurrentActualRate);
		CurrencyPairValue averageOf50 = new CurrencyPairValue(this.lastAverageOf50Day, this.lastAverageOf50Rate);
		CurrencyPairValue averageOf200 = new CurrencyPairValue(this.lastAverageOf200Day, this.lastAverageOf200Rate);
		CurrencyPairInfo currencyPairInfo = new CurrencyPairInfo(currentActual, averageOf50, averageOf200);
		
		// Prepare output #1 - Map.Entry
		Map.Entry<CurrencyPairKey, CurrencyPairInfo> entry =
				new AbstractMap.SimpleImmutableEntry<>(Constants.BTC_USD_CURRENCY_PAIR_KEY, currencyPairInfo);
		return super.tryEmit(entry);
	}
	
}
