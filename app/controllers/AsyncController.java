package controllers;

import org.apache.commons.lang.exception.ExceptionUtils;
import play.Logger;
import play.libs.F.EventStream;
import play.mvc.WebSocketController;

/**
 * Created by IntelliJ IDEA.
 * User: Loic
 * Date: 21/05/11
 * Time: 17:40
 */
public class AsyncController extends WebSocketController {

    public static EventStream<String> liveStream = new EventStream<String>();

    public static void testWebSocket() {
        while (inbound.isOpen()) {
            String message = await(liveStream.nextEvent());
            if (message != null) {
                outbound.send(message);
            }
        }
    }

}
