package neil.demo.amsjug2018;

/**
 * <p>Utility constants for the application as a whole.
 * </p>
 */
public class Constants {

	// Currencies
	public static final String BITCOIN_CODE = "BTC";
	public static final String US_DOLLARS_CODE = "USD";
	
	// Data
	public static final CurrencyPairKey BTC_USD_CURRENCY_PAIR_KEY
		= new CurrencyPairKey(BITCOIN_CODE, US_DOLLARS_CODE);
	
	// Input file
	public static final String BTC_USD_FILENAME_PREFIX 
		= BITCOIN_CODE.toLowerCase() + US_DOLLARS_CODE.toLowerCase();
	public static final String BTC_USD_FILENAME_FROM_DATE = "2017-01-01";
	
	// Hazelcast
	public static final String COMMAND_NOUN_MOVING_AVERAGE = "moving_average";
	public static final String COMMAND_VERB_START = "start";
	public static final String COMMAND_VERB_STOP = "stop";

	public static final String IMAP_NAME_ALERT = "alert";
	public static final String IMAP_NAME_COMMAND = "command";
	public static final String IMAP_NAME_PRICE = "price";
	public static final String[] IMAP_NAMES = new String[] { 
			IMAP_NAME_ALERT, IMAP_NAME_COMMAND, IMAP_NAME_PRICE
			};

	// Kafka
	public static final int TOPIC_CURRENCY_PARTITIONS = 3;
	public static final String TOPIC_CURRENCY_NAME = "currency";
}