package com.teletrac.test;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

import com.teletrac.ApiVerticle;


@ExtendWith(VertxExtension.class)
public class ClassIntegrationTest {

	String deploymentNane;

	@BeforeEach
	@DisplayName("Deploy a verticle")
	void prepare(Vertx vertx, VertxTestContext testContext) throws InterruptedException {
        
        vertx.deployVerticle(new ApiVerticle(), testContext.succeedingThenComplete());
		
	}


	@Test
	@DisplayName("🚀 test echo")
	void testPostEcho(Vertx vertx, VertxTestContext testContext) {

		JsonObject joModel = new JsonObject();
		joModel.put("RecordType", "rectype");
		joModel.put("DeviceId", "9495856867970");
		joModel.put("EventDateTime", "2014-05-12T05:09:48Z");
		joModel.put("FieldA", 11);
		joModel.put("FieldB", "fieldb");
		joModel.put("FieldC", 20.88);

		WebClient webClient = WebClient.create(vertx);
		webClient.post(8080, "localhost", "/api/echo").as(BodyCodec.string()).sendJson(joModel,
				testContext.succeeding(response -> testContext.verify(() -> {
					assertThat(response.statusCode()).isEqualTo(401);
					testContext.completeNow();
				})));
	}

	@Test
	@DisplayName("🚀 test device")
	void testPostDevice(Vertx vertx, VertxTestContext testContext) {
		// final Checkpoint checkpoint = testContext.checkpoint(2);
		JsonObject joModel = new JsonObject();
		joModel.put("RecordType", "rectype");
		joModel.put("DeviceId", "9495856867970");
		joModel.put("EventDateTime", "2014-05-12T05:09:48Z");
		joModel.put("FieldA", 11);
		joModel.put("FieldB", "fieldb");
		joModel.put("FieldC", 20.88);

		WebClient webClient = WebClient.create(vertx);
		webClient.post(8080, "localhost", "/api/device").as(BodyCodec.string()).sendJson(joModel,
				testContext.succeeding(response -> testContext.verify(() -> {
					assertThat(response.statusCode()).isEqualTo(401);
					testContext.completeNow();
				})));
	}

	@Test
	@DisplayName("🚀 test nocontent")
	void testPostNoContent(VertxTestContext testContext, WebClient webClient) {

		webClient.post(8080, "localhost", "/api/nocontent").as(BodyCodec.string())
				.send(testContext.succeeding(response -> testContext.verify(() -> {
					assertThat(response.statusCode()).isEqualTo(401);
					testContext.completeNow();
				})));
	}

}
