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

import neil.demo.amsjug2018.Constants;
import neil.demo.amsjug2018.TimePrice;
import neil.demo.amsjug2018.jet.PriceFormatterProcessor;

public class PriceFomatterProcessorTest {
	
	/**
	 * Current price input, ordinal 0
	 */
	@Test
	public void ordinal_0_only() {
		TimePrice input1
			= new TimePrice(TestConstants.AMS_JUG_EVENT, TestConstants.ONES);

		List<TimePrice> ordinal0 = Arrays.asList(input1);
		List<TimePrice> ordinal1 = Collections.emptyList();
		List<TimePrice> ordinal2 = Collections.emptyList();

		TimePrice output1 = new TimePrice();
		output1.setDate(TimePrice.convert(LocalDate.parse("2018-04-19")));
		output1.setRate(new BigDecimal("1.11"));
		
		Entry<String, TimePrice> entry1
			= new SimpleImmutableEntry<>(Constants.KEY_CURRENT, output1);
		
		TestSupport
		.verifyProcessor(PriceFormatterProcessor::new)
		.disableSnapshots()
		.inputs(Arrays.asList(ordinal0, ordinal1, ordinal2))
		.expectOutput(Collections.singletonList(entry1));
	}
	
	/**
	 * Average of 50 prices input, ordinal 1
	 */
	@Test
	public void ordinal_1_only() {
		TimePrice input1
			= new TimePrice(TestConstants.AMS_JUG_EVENT, TestConstants.ONES);
		TimePrice input2
			= new TimePrice(TestConstants.AMS_JUG_EVENT, TestConstants.TWOS);

		List<TimePrice> ordinal0 = Collections.emptyList();
		List<TimePrice> ordinal1 = Arrays.asList(input1, input2);
		List<TimePrice> ordinal2 = Collections.emptyList();

		TimePrice output1 = new TimePrice();
		output1.setDate(TimePrice.convert(LocalDate.parse("2018-04-19")));
		output1.setRate(new BigDecimal("1.11"));
		TimePrice output2 = new TimePrice();
		output2.setDate(TimePrice.convert(LocalDate.parse("2018-04-19")));
		output2.setRate(new BigDecimal("2.22"));
		
		Entry<String, TimePrice> entry1
			= new SimpleImmutableEntry<>(Constants.KEY_50_POINT, output1);
		Entry<String, TimePrice> entry2
			= new SimpleImmutableEntry<>(Constants.KEY_50_POINT, output2);

		TestSupport
		.verifyProcessor(PriceFormatterProcessor::new)
		.disableSnapshots()
		.inputs(Arrays.asList(ordinal0, ordinal1, ordinal2))
		.expectOutput(Arrays.asList(entry1, entry2));
	}

	/**
	 * Average of 200 prices input, ordinal 2
	 */
	@Test
	public void ordinal_2_only() {
		TimePrice input1
			= new TimePrice(TestConstants.AMS_JUG_EVENT, TestConstants.ONES);
		TimePrice input2
			= new TimePrice(TestConstants.AMS_JUG_EVENT, TestConstants.TWOS);
		TimePrice input3
			= new TimePrice(TestConstants.AMS_JUG_EVENT, TestConstants.THREES);

		List<TimePrice> ordinal0 = Collections.emptyList();
		List<TimePrice> ordinal1 = Collections.emptyList();
		List<TimePrice> ordinal2 = Arrays.asList(input1, input2, input3);

		TimePrice output1 = new TimePrice();
		output1.setDate(TimePrice.convert(LocalDate.parse("2018-04-19")));
		output1.setRate(new BigDecimal("1.11"));
		TimePrice output2 = new TimePrice();
		output2.setDate(TimePrice.convert(LocalDate.parse("2018-04-19")));
		output2.setRate(new BigDecimal("2.22"));
		TimePrice output3 = new TimePrice();
		output3.setDate(TimePrice.convert(LocalDate.parse("2018-04-19")));
		output3.setRate(new BigDecimal("3.33"));
		
		Entry<String, TimePrice> entry1
			= new SimpleImmutableEntry<>(Constants.KEY_200_POINT, output1);
		Entry<String, TimePrice> entry2
			= new SimpleImmutableEntry<>(Constants.KEY_200_POINT, output2);
		Entry<String, TimePrice> entry3
			= new SimpleImmutableEntry<>(Constants.KEY_200_POINT, output3);

		TestSupport
		.verifyProcessor(PriceFormatterProcessor::new)
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
		TimePrice input1
			= new TimePrice(TestConstants.AMS_JUG_EVENT, TestConstants.ONES);
		TimePrice input2
			= new TimePrice(TestConstants.AMS_JUG_EVENT, TestConstants.TWOS);
		TimePrice input3
			= new TimePrice(TestConstants.AMS_JUG_EVENT, TestConstants.THREES);
		TimePrice input4
			= new TimePrice(TestConstants.AMS_JUG_EVENT, TestConstants.FOURS);
		TimePrice input5
			= new TimePrice(TestConstants.AMS_JUG_EVENT, TestConstants.FIVES);
		TimePrice input6
			= new TimePrice(TestConstants.AMS_JUG_EVENT, TestConstants.SIXES);
		TimePrice input7
			= new TimePrice(TestConstants.AMS_JUG_EVENT, TestConstants.SEVENS);
		TimePrice input8
			= new TimePrice(TestConstants.AMS_JUG_EVENT, TestConstants.EIGHTS);
		TimePrice input9
			= new TimePrice(TestConstants.AMS_JUG_EVENT, TestConstants.NINES);
		TimePrice input10
			= new TimePrice(TestConstants.AMS_JUG_EVENT, TestConstants.ZEROES);

		List<TimePrice> ordinal0 = Arrays.asList(input1, input2, input3);
		List<TimePrice> ordinal1 = Arrays.asList(input4, input5, input6);
		List<TimePrice> ordinal2 = Arrays.asList(input7, input8, input9, input10);

		/* All we can expect is that the last item output contains the 
		 * last input value from each ordinal.
		 */
		TimePrice output10 = new TimePrice();
		output10.setDate(TimePrice.convert(LocalDate.parse("2018-04-19")));
		output10.setRate(new BigDecimal("0.00"));
		
		Entry<String, TimePrice> entry10
			= new SimpleImmutableEntry<>(Constants.KEY_200_POINT, output10);

		TestSupport
		.verifyProcessor(PriceFormatterProcessor::new)
		.disableSnapshots()
		.outputChecker(TestConstants.LAST_ITEM_MATCHES)
		.inputs(Arrays.asList(ordinal0, ordinal1, ordinal2))
		.expectOutput(Collections.singletonList(entry10));
	}

}
