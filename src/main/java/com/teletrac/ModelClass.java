package com.teletrac;

import io.vertx.core.json.JsonObject;

public class ModelClass {
	private String RecordType;
	private String DeviceId;
	private String EventDateTime;
	private Integer FieldA;
	private String FieldB;
	private Double FieldC;
	
	
	public ModelClass() {
		
	}
	
	/**
	 * @param mcJson
	 */
	public ModelClass(JsonObject mcJson) {
		this.setRecordType(mcJson.getString("RecordType"));
		this.setDeviceId(mcJson.getString("DeviceId"));
		this.setEventDateTime(mcJson.getString("EventDateTime"));
		this.setFieldA(mcJson.getInteger("FieldA"));
		this.setFieldB(mcJson.getString("FieldB"));
		this.setFieldC(mcJson.getDouble("FieldC"));	
		
	}

	/**
	 * @return the recordType
	 */
	public String getRecordType() {
		return RecordType;
	}

	/**
	 * @param recordType the recordType to set
	 */
	public void setRecordType(String recordType) {
		RecordType = recordType;
	}

	

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return DeviceId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		DeviceId = deviceId;
	}

	/**
	 * @return the eventDateTime
	 */
	public String getEventDateTime() {
		return EventDateTime;
	}

	/**
	 * @param eventDateTime the eventDateTime to set
	 */
	public void setEventDateTime(String eventDateTime) {
		EventDateTime = eventDateTime;
	}


	/**
	 * @return the fieldA
	 */
	public int getFieldA() {
		return FieldA;
	}

	/**
	 * @param fieldA the fieldA to set
	 */
	public void setFieldA(int fieldA) {
		FieldA = fieldA;
	}

	/**
	 * @return the fieldB
	 */
	public String getFieldB() {
		return FieldB;
	}

	/**
	 * @param fieldB the fieldB to set
	 */
	public void setFieldB(String fieldB) {
		FieldB = fieldB;
	}

	/**
	 * @return the fieldC
	 */
	public double getFieldC() {
		return FieldC;
	}

	/**
	 * @param fieldC the fieldC to set
	 */
	public void setFieldC(double fieldC) {
		FieldC = fieldC;
	}

	
}
