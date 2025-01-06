package org.etienne.kafka_demo.ksql;

import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KsqlController {

	private final String topic;

	private final String CREATE_CPU_STREAM;
	private final String CREATE_CPU_TABLE;

	public KsqlController(@Value("${kafka.topic.monitoring.cpu}") String topic) {
		this.topic = topic;
		CREATE_CPU_STREAM = ""
				+ " CREATE STREAM cpu (de00a20 DOUBLE, de20a40 DOUBLE, de40a60 DOUBLE, de60a80 DOUBLE, de80a100 DOUBLE) "
				+ " WITH (KAFKA-TOPIC = '" + this.topic + "'," //
				+ "       VALUE_FORMAT = 'JSON',"//
				+ "        TIMESTAMP = 'timestamp',"//
				+ "        TIMESTAMP_FORMAT = 'yyyy-MM-dd HH:mm:ss',"//
				+ "        PARTITIONS = 1);";
		CREATE_CPU_TABLE = "" + " CREATE TABLE cpu AS "//
				+ " SELECT " //
				+ "    de00a20 DOUBLE," //
				+ "    de20a40 DOUBLE,"//
				+ "    de40a60 DOUBLE," //
				+ "    de60a80 DOUBLE," //
				+ "    de80a100 DOUBLE " //
				+ " FROM " + this.topic //
				+ "       VALUE_FORMAT = 'JSON',"//
				+ "        TIMESTAMP = 'timestamp',"//
				+ "        TIMESTAMP_FORMAT = 'yyyy-MM-dd HH:mm:ss',"//
				+ "        PARTITIONS = 1);";

	}
}
