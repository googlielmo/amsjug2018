package neil.demo.amsjug2018;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Topic logging listener.
 * </p>
 * <p>If something is published to a topic, and this class is
 * registered as a listener, it'll do a {@code toString()}
 * to print the message payload.
 * </p>
 */
@SuppressWarnings("rawtypes")
@Slf4j
public class MyTopicListener implements MessageListener {

	/**
	 * <p>Dump a received message to the logger.
	 * </p>
	 *
	 * @param message From the topic
	 */
	@Override
	public void onMessage(Message message) {
		String payload = message.getMessageObject().toString();
		log.info("");
		log.info("***********************************************");
		log.info("Topic '{}' : '{}'", message.getSource(), payload);
		log.info("***********************************************");
		log.info("");
	}

}
