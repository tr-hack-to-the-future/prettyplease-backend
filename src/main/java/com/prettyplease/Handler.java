package com.prettyplease;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.prettyplease.model.Sponsor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger LOG = LogManager.getLogger(Handler.class);

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		LOG.info("received: {}", input);

		Sponsor alfa = new Sponsor(1, "alfa", "Asset finance software");
		Sponsor blackrock = new Sponsor(1, "blackrock", "Asset management");

		List<Sponsor> sponsors = new ArrayList<>();
		sponsors.add(alfa);
		sponsors.add(blackrock);

		return ApiGatewayResponse.builder()
				.setStatusCode(200)
				.setObjectBody(sponsors)
				.build();
	}
}
