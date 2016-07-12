package fr.fcns.iot.transformer;

import fr.fcns.iot.model.SensorMessage;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by lmarchau on 12/07/2016.
 */
@Component
public class InputTransformer implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(InputTransformer.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        String body = in.getBody(String.class);
        LOG.debug("In Message: {}", body);
        in.setBody(new SensorMessage(body));
    }
}
