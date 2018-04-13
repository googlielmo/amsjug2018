package neil.demo.amsjug2018;

import org.jline.utils.AttributedString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

/**
 * <p>Set the prompt used by Spring Shell
 */
@Component
@Order(Integer.MIN_VALUE)
public class MyPrompt implements PromptProvider {

    @Value("${my.prompt}")
    private String prompt;

    @Override
    public AttributedString getPrompt() {
    	String prompt = this.prompt.replaceAll("\"", "") + "$ ";
    	return new AttributedString(prompt);
    }
}
