package neil.demo.amsjus2018.jet;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Test;

import com.hazelcast.jet.core.test.TestSupport;
import com.hazelcast.jet.datamodel.Tuple2;

import neil.demo.amsjug2018.Constants;
import neil.demo.amsjug2018.CurrencyPairInfo;
import neil.demo.amsjug2018.CurrencyPairKey;
import neil.demo.amsjug2018.CurrencyPairValue;
import neil.demo.amsjug2018.jet.PriceCombinerProcessor;

public class PriceCombinerProcessorTest {
	
	/**
	 * Current price input, ordinal 0
	 */
	@Test
	public void ordinal_0_only() {
		Tuple2<LocalDate, BigDecimal> input1
			= Tuple2.tuple2(TestConstants.AMS_JUG_EVENT, TestConstants.ONES);

		List<Tuple2<LocalDate, BigDecimal>> ordinal0 = Arrays.asList(input1);
		List<Tuple2<LocalDate, BigDecimal>> ordinal1 = Collections.emptyList();
		List<Tuple2<LocalDate, BigDecimal>> ordinal2 = Collections.emptyList();

		CurrencyPairInfo output1 = new CurrencyPairInfo();
		CurrencyPairValue expected1 = new CurrencyPairValue();
		expected1.setDay(LocalDate.parse("2018-04-19"));
		expected1.setRate(new BigDecimal("1.11"));
		output1.setCurrentActual(expected1);
		output1.setAverageOf50(new CurrencyPairValue());
		output1.setAverageOf200(new CurrencyPairValue());
		
		Entry<CurrencyPairKey, CurrencyPairInfo> entry1
			= new SimpleImmutableEntry<>(Constants.BTC_USD_CURRENCY_PAIR_KEY, output1);
		
		TestSupport
		.verifyProcessor(PriceCombinerProcessor::new)
		.disableSnapshots()
		.inputs(Arrays.asList(ordinal0, ordinal1, ordinal2))
		.expectOutput(Collections.singletonList(entry1));
	}

	/**
	 * Average of 50 prices input, ordinal 1
	 */
	@Test
	public void ordinal_1_only() {
		Tuple2<LocalDate, BigDecimal> input1
			= Tuple2.tuple2(TestConstants.AMS_JUG_EVENT, TestConstants.ONES);
		Tuple2<LocalDate, BigDecimal> input2
			= Tuple2.tuple2(TestConstants.AMS_JUG_EVENT, TestConstants.TWOS);

		List<Tuple2<LocalDate, BigDecimal>> ordinal0 = Collections.emptyList();
		List<Tuple2<LocalDate, BigDecimal>> ordinal1 = Arrays.asList(input1, input2);
		List<Tuple2<LocalDate, BigDecimal>> ordinal2 = Collections.emptyList();

		CurrencyPairInfo output1 = new CurrencyPairInfo();
		CurrencyPairValue expected1 = new CurrencyPairValue();
		expected1.setDay(LocalDate.parse("2018-04-19"));
		expected1.setRate(new BigDecimal("1.11"));
		output1.setCurrentActual(new CurrencyPairValue());
		output1.setAverageOf50(expected1);
		output1.setAverageOf200(new CurrencyPairValue());

		CurrencyPairInfo output2 = new CurrencyPairInfo();
		CurrencyPairValue expected2 = new CurrencyPairValue();
		expected2.setDay(LocalDate.parse("2018-04-19"));
		expected2.setRate(new BigDecimal("2.22"));
		output2.setCurrentActual(new CurrencyPairValue());
		output2.setAverageOf50(expected2);
		output2.setAverageOf200(new CurrencyPairValue());

		Entry<CurrencyPairKey, CurrencyPairInfo> entry1
			= new SimpleImmutableEntry<>(Constants.BTC_USD_CURRENCY_PAIR_KEY, output1);
		Entry<CurrencyPairKey, CurrencyPairInfo> entry2
			= new SimpleImmutableEntry<>(Constants.BTC_USD_CURRENCY_PAIR_KEY, output2);

		TestSupport
		.verifyProcessor(PriceCombinerProcessor::new)
		.disableSnapshots()
		.inputs(Arrays.asList(ordinal0, ordinal1, ordinal2))
		.expectOutput(Arrays.asList(entry1, entry2));
	}

	/**
	 * Average of 200 prices input, ordinal 2
	 */
	@Test
	public void ordinal_2_only() {
		Tuple2<LocalDate, BigDecimal> input1
			= Tuple2.tuple2(TestConstants.AMS_JUG_EVENT, TestConstants.ONES);
		Tuple2<LocalDate, BigDecimal> input2
			= Tuple2.tuple2(TestConstants.AMS_JUG_EVENT, TestConstants.TWOS);
		Tuple2<LocalDate, BigDecimal> input3
			= Tuple2.tuple2(TestConstants.AMS_JUG_EVENT, TestConstants.THREES);

		List<Tuple2<LocalDate, BigDecimal>> ordinal0 = Collections.emptyList();
		List<Tuple2<LocalDate, BigDecimal>> ordinal1 = Collections.emptyList();
		List<Tuple2<LocalDate, BigDecimal>> ordinal2 = Arrays.asList(input1, input2, input3);

		CurrencyPairInfo output1 = new CurrencyPairInfo();
		CurrencyPairValue expected1 = new CurrencyPairValue();
		expected1.setDay(LocalDate.parse("2018-04-19"));
		expected1.setRate(new BigDecimal("1.11"));
		output1.setCurrentActual(new CurrencyPairValue());
		output1.setAverageOf50(new CurrencyPairValue());
		output1.setAverageOf200(expected1);
		CurrencyPairInfo output2 = new CurrencyPairInfo();
		CurrencyPairValue expected2 = new CurrencyPairValue();
		expected2.setDay(LocalDate.parse("2018-04-19"));
		expected2.setRate(new BigDecimal("2.22"));
		output2.setCurrentActual(new CurrencyPairValue());
		output2.setAverageOf50(new CurrencyPairValue());
		output2.setAverageOf200(expected2);
		CurrencyPairInfo output3 = new CurrencyPairInfo();
		CurrencyPairValue expected3 = new CurrencyPairValue();
		expected3.setDay(LocalDate.parse("2018-04-19"));
		expected3.setRate(new BigDecimal("3.33"));
		output3.setCurrentActual(new CurrencyPairValue());
		output3.setAverageOf50(new CurrencyPairValue());
		output3.setAverageOf200(expected3);

		Entry<CurrencyPairKey, CurrencyPairInfo> entry1
			= new SimpleImmutableEntry<>(Constants.BTC_USD_CURRENCY_PAIR_KEY, output1);
		Entry<CurrencyPairKey, CurrencyPairInfo> entry2
			= new SimpleImmutableEntry<>(Constants.BTC_USD_CURRENCY_PAIR_KEY, output2);
		Entry<CurrencyPairKey, CurrencyPairInfo> entry3
			= new SimpleImmutableEntry<>(Constants.BTC_USD_CURRENCY_PAIR_KEY, output3);

		TestSupport
		.verifyProcessor(PriceCombinerProcessor::new)
		.disableSnapshots()
		.inputs(Arrays.asList(ordinal0, ordinal1, ordinal2))
		.expectOutput(Arrays.asList(entry1, entry2, entry3));
	}

	/**
	 * Input on all 3 input ordinals, order these are handled is undefined
	 * so output sequence cannot be guaranteed.
	 */
	@Test
	public void ordinal_0_1_and_2() {
		Tuple2<LocalDate, BigDecimal> input1
			= Tuple2.tuple2(TestConstants.AMS_JUG_EVENT, TestConstants.ONES);
		Tuple2<LocalDate, BigDecimal> input2
			= Tuple2.tuple2(TestConstants.AMS_JUG_EVENT, TestConstants.TWOS);
		Tuple2<LocalDate, BigDecimal> input3
			= Tuple2.tuple2(TestConstants.AMS_JUG_EVENT, TestConstants.THREES);
		Tuple2<LocalDate, BigDecimal> input4
			= Tuple2.tuple2(TestConstants.AMS_JUG_EVENT, TestConstants.FOURS);
		Tuple2<LocalDate, BigDecimal> input5
			= Tuple2.tuple2(TestConstants.AMS_JUG_EVENT, TestConstants.FIVES);
		Tuple2<LocalDate, BigDecimal> input6
			= Tuple2.tuple2(TestConstants.AMS_JUG_EVENT, TestConstants.SIXES);
		Tuple2<LocalDate, BigDecimal> input7
			= Tuple2.tuple2(TestConstants.AMS_JUG_EVENT, TestConstants.SEVENS);
		Tuple2<LocalDate, BigDecimal> input8
			= Tuple2.tuple2(TestConstants.AMS_JUG_EVENT, TestConstants.EIGHTS);
		Tuple2<LocalDate, BigDecimal> input9
			= Tuple2.tuple2(TestConstants.AMS_JUG_EVENT, TestConstants.NINES);
		Tuple2<LocalDate, BigDecimal> input10
			= Tuple2.tuple2(TestConstants.AMS_JUG_EVENT, TestConstants.ZEROES);

		List<Tuple2<LocalDate, BigDecimal>> ordinal0 = Arrays.asList(input1, input2, input3);
		List<Tuple2<LocalDate, BigDecimal>> ordinal1 = Arrays.asList(input4, input5, input6);
		List<Tuple2<LocalDate, BigDecimal>> ordinal2 = Arrays.asList(input7, input8, input9, input10);

		/* All we can expect is that the last item output contains the 
		 * last input value from each ordinal.
		 */
		CurrencyPairValue expected1 = new CurrencyPairValue();
		expected1.setDay(LocalDate.parse("2018-04-19"));
		expected1.setRate(new BigDecimal("3.33"));
		CurrencyPairValue expected2 = new CurrencyPairValue();
		expected2.setDay(LocalDate.parse("2018-04-19"));
		expected2.setRate(new BigDecimal("6.66"));
		CurrencyPairValue expected3 = new CurrencyPairValue();
		expected3.setDay(LocalDate.parse("2018-04-19"));
		expected3.setRate(new BigDecimal("0.00"));
		CurrencyPairInfo output10 = new CurrencyPairInfo(expected1, expected2, expected3);
		
		Entry<CurrencyPairKey, CurrencyPairInfo> entry10
			= new SimpleImmutableEntry<>(Constants.BTC_USD_CURRENCY_PAIR_KEY, output10);

		TestSupport
		.verifyProcessor(PriceCombinerProcessor::new)
		.disableSnapshots()
		.outputChecker(TestConstants.LAST_ITEM_MATCHES)
		.inputs(Arrays.asList(ordinal0, ordinal1, ordinal2))
		.expectOutput(Collections.singletonList(entry10));
	}

}
