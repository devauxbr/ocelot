/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 */
package fr.hhdev.ocelot.encoders;

import fr.hhdev.ocelot.messaging.MessageToClient;
import java.util.Collection;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.ws.rs.core.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decoder for class MessageClient for webSocket endpoint
 * @author hhfrancois
 */
public class MessageToClientEncoder implements Encoder.Text<MessageToClient> {

	private static final Logger logger = LoggerFactory.getLogger(MessageToClientEncoder.class);

	private EndpointConfig config;
	
	@Override
	public String encode(MessageToClient object) throws EncodeException {
		Collection<String> acceptLanguages = (Collection<String>) config.getUserProperties().get(HttpHeaders.ACCEPT_LANGUAGE);
		if(acceptLanguages!=null) {
			logger.debug("Encode MessageToClientEncoder : accept-language : {}", String.join(" - ", acceptLanguages));
		}
		return object.toJson();
	}

	@Override
	public void init(EndpointConfig config) {
		this.config = config;
	}

	@Override
	public void destroy() {
	}

}
