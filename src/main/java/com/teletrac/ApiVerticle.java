package com.teletrac;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

import io.vertx.ext.auth.PubSecKeyOptions;

import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.json.schema.NoSyncValidationException;
import io.vertx.json.schema.Schema;
import io.vertx.json.schema.SchemaParser;
import io.vertx.json.schema.SchemaRouter;
import io.vertx.json.schema.SchemaRouterOptions;
import io.vertx.json.schema.ValidationException;
import static io.vertx.json.schema.common.dsl.Schemas.stringSchema;
import static io.vertx.json.schema.common.dsl.Schemas.objectSchema;

import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;


/**
 * @author charl
 *
 */
public class ApiVerticle extends AbstractVerticle {

	ServiceClass service;
	private static Gson gson;
	private static final Logger LOG = LogManager.getLogger(ApiVerticle.class);

	@Override
	public void start(Promise<Void> startPromise) {

		GsonBuilder gsonBuilder = new GsonBuilder();

		gson = gsonBuilder.disableHtmlEscaping().setPrettyPrinting().create();

		final JDBCClient client = JDBCClient.createShared(vertx,
				new JsonObject().put("url", "jdbc:hsqldb:mem:test?shutdown=true")
						.put("driver_class", "org.hsqldb.jdbcDriver").put("max_pool_size", 30).put("user", "SA")
						.put("password", ""));

		service = new ServiceClass(vertx, client);
		service.initializeDB();

		JWTAuth jwtAuth = JWTAuth.create(vertx, new JWTAuthOptions()
				.addPubSecKey(new PubSecKeyOptions().setAlgorithm("HS256").setBuffer("keyboard cat")));

		//Generate token for demo
		String token = jwtAuth.generateToken(new JsonObject());

		LOG.warn("Your token is {} ", token);

		// Create a router object.
		Router router = Router.router(vertx);

		Set<String> allowHeaders = new HashSet<>();
		allowHeaders.add("Access-Control-Allow-Origin");
		allowHeaders.add("Access-Control-Allow-Method");
		allowHeaders.add("Access-Control-Allow-Credentials");
		allowHeaders.add("Origin");
		allowHeaders.add("Bearer");
		allowHeaders.add("Authorization");
		allowHeaders.add("Content-Type");
		allowHeaders.add("Accept");
		allowHeaders.add("x-requested-with");
		allowHeaders.add("x-xsrf-token");
		allowHeaders.add("content-encoding");
		allowHeaders.add("X-PINGARUNER");

		Set<HttpMethod> allowMethods = new HashSet<>();
		allowMethods.add(HttpMethod.GET);
		allowMethods.add(HttpMethod.POST);
		allowMethods.add(HttpMethod.PUT);
		allowMethods.add(HttpMethod.OPTIONS);

		// CORSHANDLER
		router.route("/api*").handler(CorsHandler.create(".*.").allowedHeaders(allowHeaders)
				.allowedMethods(allowMethods).allowCredentials(true));

		// ensure all post request have a body handler so we can access the body
		router.route("/api*").handler(BodyHandler.create());

		router.route("/api*").handler((handle) -> {
			
			LOG.warn("Request for {} came in at {}", handle.request().uri(), new Timestamp(System.currentTimeMillis()));
			

			HttpServerRequest request = handle.request();

			String authorization = request.headers().get(HttpHeaders.AUTHORIZATION);

			if (authorization == null) {
				handle.fail(401);
			} else {

				jwtAuth.authenticate(new JsonObject().put("token", authorization).put("options",
						new JsonObject().put("ignoreExpiration", true))).onSuccess(user -> {
							LOG.info("Successful authentication");
							handle.next();
						}).onFailure(err -> {
							handle.fail(401);
						});

			}

		});

		router.post("/api/nocontent").handler((handler) -> {
			handler.fail(204);
		});
		router.post("/api/echo").handler(this::handleEcho);
		router.post("/api/device").handler(this::handlePostDevice);
		router.get("/api/devices").handler(this::handleGetDevices);
		
		//If routes does not exist return 400
		router.route().handler(res -> {
			res.fail(400);
		});

		vertx.createHttpServer().requestHandler(router).listen(8080).onSuccess(server -> {
			System.out.println("HTTP serever started on port " + server.actualPort());
			startPromise.complete();
			

		}).onFailure(server -> {
			System.out.println("Couldn't start server");
			startPromise.fail(server.getCause());
		});

	}

	private void handleEcho(RoutingContext context) {
		
		JsonObject inputObject = context.getBodyAsJson();
				
		if(this.validateJsonFields(inputObject)) {
			ModelClass mdClass = gson.fromJson(inputObject.toString(), ModelClass.class);

			this.service.addModel(mdClass).onComplete(res -> {
				if(res.succeeded()) {
					ModelClass newClassObj = res.result() ;
					 context.response()
	                    .putHeader("content-type", "application/json")
	                    .end(gson.toJson(newClassObj));	
				}

			});
		} else {
			context.response().setStatusCode(400).end();
		}

	}

	/**
	 * @param context
	 */
	private void handlePostDevice(RoutingContext context) {

		JsonObject inputObject = context.getBodyAsJson();
		
		if(this.validateJsonFields(inputObject)) {
			ModelClass mdClass = gson.fromJson(inputObject.toString(), ModelClass.class);

			this.service.addModel(mdClass).onComplete(res -> {
				if(res.succeeded()) {
					ModelClass newClassObj = new ModelClass() ;
					newClassObj.setDeviceId(res.result().getDeviceId());
					context.response()
	                    .putHeader("content-type", "application/json")
	                    .end(gson.toJson(newClassObj));	
				}

			});
		} else {
			context.response().setStatusCode(400).end();
		}
	
	}
	
	/**
	 * @param context
	 */
	private void handleGetDevices(RoutingContext context) {
		service.retrieveAll().onComplete(allRes -> {
			if(allRes.succeeded()) {			
				context.response()
                .putHeader("content-type", "application/json")
                .end(gson.toJson(allRes.result()));	
			} else {
				context.response().setStatusCode(400).end();
			}
			
		});
	}

	/**
	 * @param json
	 * @return if json has all mandatory fields
	 */
	private boolean validateJsonFields(JsonObject jsonInput) {
		try {
			SchemaRouter schemaRouter = SchemaRouter.create(vertx, new SchemaRouterOptions());
			SchemaParser schemaParser = SchemaParser.createDraft201909SchemaParser(schemaRouter);

			Schema inputSchema = objectSchema().requiredProperty("DeviceId", stringSchema()).build(schemaParser);

			inputSchema.validateSync(jsonInput);

			return true;
		} catch (ValidationException e) {
			LOG.info(" ValidationException Err !! {}", e.getMessage());
			return false;
		} catch (NoSyncValidationException e) {
			LOG.info("NoSyncValidationException Err !! {}" , e.getMessage());
			return false;
		} catch (Exception e) {
			return false;
		}

	}

}
