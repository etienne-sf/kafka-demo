package org.etienne.kafka_demo.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @See https://codenotfound.com/spring-kafka-apache-avro-serializer-deserializer-example.html
 *
 * @param <T> La classe source de la sérialisation
 */
@Slf4j
public class AvroSerializer<T extends SpecificRecordBase> implements Serializer<T> {

	@Override
	public void close() {
		// No-op
	}

	@Override
	public void configure(Map<String, ?> arg0, boolean arg1) {
		// No-op
	}

	@Override
	public byte[] serialize(String topic, T data) {
		try {
			byte[] result = null;

			if (data != null) {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				BinaryEncoder binaryEncoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null);

				DatumWriter<SpecificRecord> datumWriter = new SpecificDatumWriter<>(data.getSchema());
				datumWriter.write(data, binaryEncoder);

				binaryEncoder.flush();
				byteArrayOutputStream.close();

				result = byteArrayOutputStream.toByteArray();
			}
			return result;
		} catch (IOException ex) {
			throw new SerializationException("Can't serialize data='" + data + "' for topic='" + topic + "'", ex);
		}
	}
}