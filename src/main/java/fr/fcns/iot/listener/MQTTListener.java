package fr.fcns.iot.listener;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 * Created by lmarchau on 28/06/2016.
 */
@Component
public class MQTTListener implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(MQTTListener.class);

    private int count = 0;

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        LOG.info("Nombre de messages traités: {}", count);
    }

    @Override
    public void onMessage(Message message) {
        ++count;
        if (message instanceof TextMessage) {
            try {
                LOG.info("Message: {}", ((TextMessage) message).getText());
            } catch (JMSException e) {
                LOG.error("Error Text message, e");
                e.printStackTrace();
            }
        }
        else if (message instanceof ActiveMQObjectMessage) {
//            LOG.info("Message: {}", new String(((ActiveMQObjectMessage) message).getContent().getData()));
            LOG.info("NB MSG: {}", count);
        }
        else {
            LOG.info("Message: {}", message);
        }
    }
}
