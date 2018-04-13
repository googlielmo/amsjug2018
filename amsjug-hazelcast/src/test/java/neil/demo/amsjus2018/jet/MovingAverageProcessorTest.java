package neil.demo.amsjus2018.jet;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import com.hazelcast.jet.core.test.TestSupport;
import com.hazelcast.jet.datamodel.Tuple2;

import neil.demo.amsjug2018.CurrencyPairKey;
import neil.demo.amsjug2018.CurrencyPairValue;
import neil.demo.amsjug2018.jet.MovingAverageProcessorSupplier;

public class MovingAverageProcessorTest {
	
	private static final LocalDate AMS_JUG_EVENT = LocalDate.of(2018, 4, 19);
	private static final BigDecimal ones = new BigDecimal("1.11");
	private static final BigDecimal twos = new BigDecimal("2.22");
	private static final BigDecimal threes = new BigDecimal("3.33");
	private static final BigDecimal fours = new BigDecimal("4.44");

	/**
	 * "1.11" in isolation has the average of "1.11".
	 */
	@Test
	public void the_average_of_one_number_is_that_number() {
		CurrencyPairValue currencyPairValue1 = new CurrencyPairValue();
		currencyPairValue1.setDay(LocalDate.parse("2018-04-19"));
		currencyPairValue1.setRate(new BigDecimal("1.11"));
		
		Map.Entry<CurrencyPairKey, CurrencyPairValue> input1
			= new AbstractMap.SimpleEntry<>(null, currencyPairValue1);

		Tuple2<LocalDate, BigDecimal> output1
			= Tuple2.tuple2(AMS_JUG_EVENT, ones);

		TestSupport
		.verifyProcessor(new MovingAverageProcessorSupplier(1))
		.disableSnapshots()
		.input(Collections.singletonList(input1))
		.expectOutput(Collections.singletonList(output1));
	}
	
	/**
	 * "1.11", "2.22" and "3.33" have the average of "2.22".
	 */
	@Test
	public void the_average_of_three_is_the_sum_divided_by_three() {
		CurrencyPairValue currencyPairValue1 = new CurrencyPairValue();
		currencyPairValue1.setDay(LocalDate.parse("2018-04-19"));
		currencyPairValue1.setRate(new BigDecimal("1.11"));
		CurrencyPairValue currencyPairValue2 = new CurrencyPairValue();
		currencyPairValue2.setDay(LocalDate.parse("2018-04-19"));
		currencyPairValue2.setRate(new BigDecimal("2.22"));
		CurrencyPairValue currencyPairValue3 = new CurrencyPairValue();
		currencyPairValue3.setDay(LocalDate.parse("2018-04-19"));
		currencyPairValue3.setRate(new BigDecimal("3.33"));
		
		Map.Entry<CurrencyPairKey, CurrencyPairValue> input1
			= new AbstractMap.SimpleEntry<>(null, currencyPairValue1);
		Map.Entry<CurrencyPairKey, CurrencyPairValue> input2
			= new AbstractMap.SimpleEntry<>(null, currencyPairValue2);
		Map.Entry<CurrencyPairKey, CurrencyPairValue> input3
			= new AbstractMap.SimpleEntry<>(null, currencyPairValue3);

		Tuple2<LocalDate, BigDecimal> output1
			= Tuple2.tuple2(AMS_JUG_EVENT, twos);

		TestSupport
		.verifyProcessor(new MovingAverageProcessorSupplier(3))
		.disableSnapshots()
		.input(Arrays.asList(input1,input2,input3))
		.expectOutput(Collections.singletonList(output1));
	}
	
	/**
	 * Taking the moving average of three inputs,
	 * <pre>
	 * "1.11", "2.22", "3.33", "4.44", "5.55"
	 *   \              /
	 *    +-- "2.22" --+
	 * </pre>
	 * then
	 * <pre>
	 * "1.11", "2.22", "3.33", "4.44", "5.55"
	 *           \              /
	 *            +-- "2.22" --+
	 * </pre>
	 * and then
	 * <pre>
	 * "1.11", "2.22", "3.33", "4.44", "5.55"
	 *                   \              /
	 *                    +-- "4.44" --+
	 * </pre>
	 */
	@Test
	public void moving_average_of_five_input_in_windows_of_three() {
		CurrencyPairValue currencyPairValue1 = new CurrencyPairValue();
		currencyPairValue1.setDay(LocalDate.parse("2018-04-19"));
		currencyPairValue1.setRate(new BigDecimal("1.11"));
		CurrencyPairValue currencyPairValue2 = new CurrencyPairValue();
		currencyPairValue2.setDay(LocalDate.parse("2018-04-19"));
		currencyPairValue2.setRate(new BigDecimal("2.22"));
		CurrencyPairValue currencyPairValue3 = new CurrencyPairValue();
		currencyPairValue3.setDay(LocalDate.parse("2018-04-19"));
		currencyPairValue3.setRate(new BigDecimal("3.33"));
		CurrencyPairValue currencyPairValue4 = new CurrencyPairValue();
		currencyPairValue4.setDay(LocalDate.parse("2018-04-19"));
		currencyPairValue4.setRate(new BigDecimal("4.44"));
		CurrencyPairValue currencyPairValue5 = new CurrencyPairValue();
		currencyPairValue5.setDay(LocalDate.parse("2018-04-19"));
		currencyPairValue5.setRate(new BigDecimal("5.55"));

		Map.Entry<CurrencyPairKey, CurrencyPairValue> input1
			= new AbstractMap.SimpleEntry<>(null, currencyPairValue1);
		Map.Entry<CurrencyPairKey, CurrencyPairValue> input2
			= new AbstractMap.SimpleEntry<>(null, currencyPairValue2);
		Map.Entry<CurrencyPairKey, CurrencyPairValue> input3
			= new AbstractMap.SimpleEntry<>(null, currencyPairValue3);
		Map.Entry<CurrencyPairKey, CurrencyPairValue> input4
			= new AbstractMap.SimpleEntry<>(null, currencyPairValue4);
		Map.Entry<CurrencyPairKey, CurrencyPairValue> input5
			= new AbstractMap.SimpleEntry<>(null, currencyPairValue5);

		Tuple2<LocalDate, BigDecimal> output1
			= Tuple2.tuple2(AMS_JUG_EVENT, twos);
		Tuple2<LocalDate, BigDecimal> output2
			= Tuple2.tuple2(AMS_JUG_EVENT, threes);
		Tuple2<LocalDate, BigDecimal> output3
			= Tuple2.tuple2(AMS_JUG_EVENT, fours);
		
		TestSupport
		.verifyProcessor(new MovingAverageProcessorSupplier(3))
		.disableSnapshots()
		.input(Arrays.asList(input1,input2,input3,input4,input5))
		.expectOutput(Arrays.asList(output1,output2,output3));
	}
}