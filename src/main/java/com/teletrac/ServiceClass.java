package com.teletrac;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServiceClass {
	JDBCClient client;
	private final Logger LOG = LogManager.getLogger(ServiceClass.class);

	public ServiceClass(Vertx vertx, JDBCClient client) {
		this.client = client;

	}

	public Future<ModelClass> addModel(ModelClass mdClass) {
		Promise<ModelClass> promise = Promise.promise();

		client.getConnection(conn -> {
			if (conn.failed()) {
				LOG.error(conn.cause().getMessage());
				return;
			}
			
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String formattedDate = null;

			try {
				Date srcDate = df.parse(mdClass.getEventDateTime());
				DateFormat outputFormatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				formattedDate = outputFormatter1.format(srcDate); //
			} catch (ParseException e1) {
				LOG.error("Dateformat error !! {}", e1.getMessage());
				e1.printStackTrace();
			}


			conn.result().execute("insert into modelTable values (" + "'" + mdClass.getRecordType() + "','" + mdClass.getDeviceId() + "','" + formattedDate + "'," + mdClass.getFieldA() + ",'" + mdClass.getFieldB() + "'," + mdClass.getFieldC() + ")",
					exRes -> {

						if (exRes.failed()) {
							LOG.error(exRes.cause());
						}

						if (exRes.succeeded()) {
							promise.complete(mdClass);
						}

					});

		});
		return promise.future();

	}
	
	public Future<List<ModelClass>> retrieveAll() {
		
		Promise<List<ModelClass>> promiseList = Promise.promise();
		List<ModelClass> rawList = new ArrayList<>();
		
		client.getConnection(conn -> {
			if (conn.failed()) {
				LOG.error(conn.cause().getMessage());
				return;
			}

			conn.result().query("SELECT * FROM modelTable ",
					qRes -> {

						if (qRes.failed()) {
							LOG.error(qRes.cause());
						}

						if (qRes.succeeded()) {
							
							for (JsonArray line : qRes.result().getResults()) {
								ModelClass mdClass = new ModelClass();
								mdClass.setRecordType(line.getString(0));
								mdClass.setDeviceId(line.getString(1));
								mdClass.setEventDateTime(line.getValue(2).toString());
  							    mdClass.setFieldA(line.getInteger(3));
								mdClass.setFieldB(line.getString(4));
								mdClass.setFieldC(line.getDouble(5));
								
								rawList.add(mdClass);
															
							}
							
							promiseList.complete(rawList);
							
						}

					});

		});
		
		return promiseList.future();
		
	}

	public void initializeDB() {
		client.getConnection(conn -> {
			if (conn.failed()) {
				LOG.error(conn.cause().getMessage());
				return;
			}

			// create a table
			execute(conn.result(), "CREATE table modelTable(RecordType varchar(50), DeviceId varchar(50), EventDateTime DATETIME, FieldA int, FieldB varchar(10), FieldC real)", create -> {
						
				conn.result().close(done -> {
					if (done.failed()) {
						throw new RuntimeException(done.cause());
					}
				});
			});
		});
	}
	
	private void execute(SQLConnection conn, String sql, Handler<Void> rs) {
		conn.execute(sql, res -> {
			if (res.failed()) {
				LOG.error("Execute failed !!");
				throw new RuntimeException(res.cause());
			}

			rs.handle(null);
		});

	}

}
