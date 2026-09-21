package com.klu.config;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Copies the caller's Bearer token onto outgoing service-to-service calls, so
 * the downstream service can authenticate the request the same way the gateway
 * did. Without this the sibling service would answer 401.
 */
public class AuthForwardingInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {

		if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes();
			if (attributes != null) {
				String authorization = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
				if (authorization != null && !authorization.isBlank()) {
					request.getHeaders().set(HttpHeaders.AUTHORIZATION, authorization);
				}
			}
		}
		return execution.execute(request, body);
	}
}
