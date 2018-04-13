package neil.demo.amsjug2018;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Write file contents to a Kafka topic for later processing.
 * For real we would expect this to be a stream where content
 * arrives continuously rather than all arrive at once.
 * </p>
 * <p>This version assumes one "<i>end of day</p>" price for
 * Bitcoin against the US Dollars. This is a bit fictitious
 * as these currency markets have no close, so the end of date
 * price is just at the instant before midnight.
 * </p>
 * <p>Also, we would really expect more than just Bitcoin
 * to US Dollars prices on the input feed.
 * </p>
 * <p><b>Note:</b> The Kafka topic is partitioned. The
 * "{@code BTC/USD}" prices go to one of the partitions.
 * The other partitions are empty unless other currency
 * pairs are written.
 * </p>
 */
@Component
@Slf4j
public class KafkaWriter implements CommandLineRunner {

	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private KafkaTemplate<CurrencyPairKey, CurrencyPairValue> kafkaTemplate;

	/**
	 * <p>Read one input file, format into {@link CurrencyPairKey} / {@link CurrencyPairValue}
	 * combos and write to a Kafka topic. As this input file omly contains "{@code BTC/USD}"
	 * prices only one Kafka partition is used.
	 * </p>
	 * <p>The purpose of this is just to load up a Kafka topic with test data to process.
	 * We could make it better by injecting prices from multiple currencies into Kafka
	 * concurrently (not one file at a time).
	 * </p>
	 * <p>We could make it better still by getting a real feed of Bitcoing prices and
	 * inkecting in to the Kafka topic.
	 * </p>
	 * <p>As Hazelcast is reading from the Kafka topic, it has no knowledge of whether
	 * the topic has been loaded up in a batch or has a true stream of prices, nor
	 * what prices are in it.
	 * </p>
	 */
	@Override
	public void run(String... args) throws Exception {
		/* Bitcoin to US Dollars
		 */
		CurrencyPairKey currencyPairKey = new CurrencyPairKey();
		currencyPairKey.setBase("BTC");
		currencyPairKey.setQuote("USD");

		// Make sure partition count is same as in "printTopic.sh" script
		int partition = currencyPairKey.hashCode() % Constants.TOPIC_CURRENCY_PARTITIONS;
		
		// "BTC/USD" currency pairs
		LocalDate fileDate = LocalDate.parse(Constants.BTC_USD_FILENAME_FROM_DATE);
		List<CurrencyPairValue> currencyPairValues = this.loadPriceFile(Constants.BTC_USD_FILENAME_PREFIX, fileDate);

		AtomicLong onFailureCount = new AtomicLong(0);
		AtomicLong onSuccessCount = new AtomicLong(0);
		CountDownLatch countDownLatch = new CountDownLatch(currencyPairValues.size());

		// Iterate through sending, using async counters for success
		for (int i = 0 ; i < currencyPairValues.size() ; i++) {
			CurrencyPairValue currencyPairValue = currencyPairValues.get(i);

			ListenableFuture<SendResult<CurrencyPairKey, CurrencyPairValue>> sendResult =
					 this.kafkaTemplate.sendDefault(partition, currencyPairKey, currencyPairValue);

			sendResult.addCallback(new ListenableFutureCallback<SendResult<CurrencyPairKey, CurrencyPairValue>>() {
                @Override
                public void onSuccess(SendResult<CurrencyPairKey, CurrencyPairValue> sendResult) {
                	onSuccessCount.incrementAndGet();
                    ProducerRecord<CurrencyPairKey, CurrencyPairValue> producerRecord = 
                        		sendResult.getProducerRecord();
                    RecordMetadata recordMetadata = sendResult.getRecordMetadata();
                    countDownLatch.countDown();
                    log.info("onSuccess(), offset {} partition {} timestamp {} for '{}'=='{}'",
                    		recordMetadata.offset(),
                    		recordMetadata.partition(), recordMetadata.timestamp(), 
                    		producerRecord.key(), producerRecord.value());
                }

                @Override
                public void onFailure(Throwable t) {
                	onFailureCount.incrementAndGet();
                    countDownLatch.countDown();
                    log.error("onFailure()", t);
                }
        });}

		// Await callbacks, all sends complete
		countDownLatch.await();
		
		if (onFailureCount.get() > 0) {
			throw new RuntimeException(onFailureCount.get() + " failures writing to Kafka");
		} else {
			log.info("Wrote {} price{}", onSuccessCount.get(), (onSuccessCount.get() == 1 ? "" : "s"));
		}
		
	}


	/**
	 * <p>Produce a sequence of {@link CurrencyPairValue} from a file
	 * specified by the parameters. This version assumes there is one
	 * rate per day, there is one price per day, and each consecutive
	 * day has a price.
	 * </p>
	 * 
	 * @param filePrefix Something like "{@code btcusd}" for Bitcoin to US Dollars
	 * @param fileDate Something like "{@code 2017-01-01}". 
	 * @return A list of dated prices
	 * @throws IOException If file not found
	 * @throws ParseException If line is correct
	 */
	private List<CurrencyPairValue> loadPriceFile(String filePrefix, LocalDate fileDate) 
			throws IOException, ParseException {
		
		List<CurrencyPairValue> result = new ArrayList<>();

		DecimalFormat decimalFormat = new DecimalFormat("#####0.00");
		decimalFormat.setParseBigDecimal(true);

		String fileName = filePrefix + "-from-" + fileDate + ".txt";
		
		Resource resource = this.applicationContext.getResource("classpath:" + fileName);

		try (InputStream inputStream = resource.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);) {

			String line;
			LocalDate rateDate = fileDate;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("#")) {
					log.info(line);
				} else {
					CurrencyPairValue currencyPairValue = new CurrencyPairValue();

					// Assume next line is next calendar day
					currencyPairValue.setDay(rateDate);
					rateDate = rateDate.plusDays(1);
					
					BigDecimal rate = (BigDecimal) decimalFormat.parse(line);
					currencyPairValue.setRate(rate);
					
					result.add(currencyPairValue);
				}
			}
		}

		return result;
	}
}
