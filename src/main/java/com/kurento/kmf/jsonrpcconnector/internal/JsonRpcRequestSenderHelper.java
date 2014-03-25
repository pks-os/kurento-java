/*
 * (C) Copyright 2013 Kurento (http://kurento.org/)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package com.kurento.kmf.jsonrpcconnector.internal;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kurento.kmf.jsonrpcconnector.client.Continuation;
import com.kurento.kmf.jsonrpcconnector.client.JsonRpcErrorException;
import com.kurento.kmf.jsonrpcconnector.internal.message.Request;
import com.kurento.kmf.jsonrpcconnector.internal.message.Response;
import com.kurento.kmf.jsonrpcconnector.internal.server.config.JsonRpcConfiguration;

public abstract class JsonRpcRequestSenderHelper implements
		JsonRpcRequestSender {

	private static Logger LOG = LoggerFactory
			.getLogger(JsonRpcRequestSenderHelper.class);

	protected AtomicInteger id = new AtomicInteger();
	protected String sessionId;

	public JsonRpcRequestSenderHelper() {
	}

	public JsonRpcRequestSenderHelper(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public <R> R sendRequest(String method, Class<R> resultClass)
			throws IOException {
		return sendRequest(method, null, resultClass);
	}

	@Override
	public <R> R sendRequest(String method, Object params, Class<R> resultClass)
			throws IOException {

		Request<Object> request = new Request<Object>(id.incrementAndGet(),
				method, params);

		if (JsonRpcConfiguration.INJECT_SESSION_ID) {
			request.setSessionId(sessionId);
		}

		return sendRequest(request, resultClass);
	}

	public <P, R> R sendRequest(Request<P> request, Class<R> resultClass)
			throws JsonRpcErrorException, IOException {

		// LOG.info("--> " + request);

		Response<R> response = internalSendRequest(request, resultClass);

		// LOG.info("<-- " + response);

		if (response == null) {
			return null;
		}

		if (response.getSessionId() != null) {
			sessionId = response.getSessionId();
		}

		if (response.getError() != null) {
			throw new JsonRpcErrorException(response.getError());
		} else {
			return response.getResult();
		}
	}

	@Override
	public JsonElement sendRequest(String method) throws IOException {
		return sendRequest(method, JsonElement.class);
	}

	@Override
	public JsonElement sendRequest(String method, Object params)
			throws IOException {
		return sendRequest(method, params, JsonElement.class);
	}

	@Override
	public void sendRequest(String method, JsonObject params,
			final Continuation<JsonElement> continuation) {

		Request<Object> request = new Request<Object>(id.incrementAndGet(),
				method, params);

		if (JsonRpcConfiguration.INJECT_SESSION_ID) {
			request.setSessionId(sessionId);
		}

		LOG.info("[Server] Message sent: " + request);

		internalSendRequest(request, JsonElement.class,
				new Continuation<Response<JsonElement>>() {

					@Override
					public void onSuccess(Response<JsonElement> response) {
						LOG.info("[Server] Message received: " + response);

						if (response == null) {
							continuation.onSuccess(null);
						}

						if (response.getSessionId() != null) {
							sessionId = response.getSessionId();
						}

						if (response.getError() != null) {
							continuation.onError(new JsonRpcErrorException(
									response.getError()));
						} else {
							continuation.onSuccess(response.getResult());
						}
					}

					@Override
					public void onError(Throwable cause) {
						continuation.onError(cause);
					}
				});

	}

	@Override
	public void sendNotification(String method) throws IOException {
		sendNotification(method, null);
	}

	@Override
	public void sendNotification(String method, Object params)
			throws IOException {

		Request<Object> request = new Request<Object>(null, method, params);

		if (JsonRpcConfiguration.INJECT_SESSION_ID) {
			request.setSessionId(sessionId);
		}

		sendRequest(request, Void.class);
	}

	@Override
	public void sendNotification(String method, Object params,
			final Continuation<JsonElement> continuation) throws IOException {

		throw new UnsupportedOperationException();
	}

	protected abstract <P, R> Response<R> internalSendRequest(
			Request<P> request, Class<R> resultClass) throws IOException;

	protected abstract void internalSendRequest(Request<Object> request,
			Class<JsonElement> class1,
			Continuation<Response<JsonElement>> continuation);

}