/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 */
package org.ocelotds.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ocelotds.Constants;
import java.util.Objects;

/**
 * Message to Client, for response after message from client. Server send this response asynchronous
 *
 * @author hhfrancois
 */
public class MessageToClient {

	private static final long serialVersionUID = -834697863344344854L;
	/**
	 * Type of message
	 */
	protected MessageType type = null;

	/**
	 * Id of request, compute from hash of packageName, classname, methodName, arguments
	 */
	protected String id;
	/**
	 * The result of request
	 */
	protected Object response = null;

	/**
	 * The result of request in json format
	 */
	protected String json = null;

	/**
	 * execution time
	 */
	protected long time = 0L;

	/**
	 * validity limit
	 */
	protected long deadline = 0L;

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getResponse() {
		return response;
	}

	private void setResponse(Object response, MessageType type) {
		this.type = type;
		this.response = response;
	}

	public void setConstraints(ConstraintViolation[] response) {
		setResponse(response, MessageType.CONSTRAINT);
	}

	public void setResult(Object response) {
		setResponse(response, MessageType.RESULT);
	}

	public void setFault(Object response) {
		setResponse(response, MessageType.FAULT);
	}

	public void setResponse(Object response) {
		this.response = response;
	}

	public long getDeadline() {
		return deadline;
	}

	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + Objects.hashCode(this.id);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final MessageToClient other = (MessageToClient) obj;
		return Objects.equals(this.id, other.id);
	}

	public String toJson() {
		ObjectMapper mapper = getObjectMapper();
		String jsonResponse = this.json;
		try {
			if (null == jsonResponse) {
				if(MessageType.FAULT.equals(this.getType())) {
					jsonResponse = ((Fault) this.getResponse()).toJson();
				} else {
					jsonResponse = mapper.writeValueAsString(this.getResponse());
				}
			}
		} catch (JsonProcessingException ex) {
			jsonResponse = new Fault(ex, 0).toJson();
		}
		return String.format("{\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":%s,\"%s\":%s,\"%s\":%s}",
				  Constants.Message.TYPE, this.getType(), Constants.Message.ID, this.getId(), Constants.Message.TIME, this.getTime(),
				  Constants.Message.DEADLINE, this.getDeadline(), Constants.Message.RESPONSE, jsonResponse);
	}

	ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}

	@Override
	public String toString() {
		return toJson();
	}
}
