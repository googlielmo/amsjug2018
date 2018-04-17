package neil.demo.amsjug2018;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;

/**
 * <p>Activate some extra stuff once the Hazelcast client
 * is up and running.
 * </p>
 * <ol>
 * <li>Listen for messages on the "{@code alert}" topic.
 * </li>
 * <li>Add a graph window to display visually changes to
 * the "{@code price}" map.
 * </li>
 * </ol>
 */
@Component
public class ApplicationInitialiser implements CommandLineRunner {

	private static final boolean INCLUDE_VALUE = true;
	
	@Autowired
	private HazelcastInstance hazelcastInstance;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void run(String... args) throws Exception {
		
        // Initialise all topics, and listener on each
        for (String iTopicName : Constants.ITOPIC_NAMES) {
            ITopic iTopic = this.hazelcastInstance.getTopic(iTopicName);
            iTopic.addMessageListener(new MyTopicListener());
        }
		
        // Connect IMap changes to the panel
        IMap<?, ?> btcUsdMap = this.hazelcastInstance.getMap(Constants.IMAP_NAME_BTC_USD);
        btcUsdMap.addEntryListener(new PricePanelListener(), INCLUDE_VALUE);
	}
	
}
