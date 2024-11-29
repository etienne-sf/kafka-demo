package org.etienne.kafka_demo.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

/**
 * 
 * @See https://codenotfound.com/spring-kafka-apache-avro-serializer-deserializer-example.html
 *
 * @param <T>
 *            La classe source de la sérialisation
 */
public class AvroSerializer<T extends SpecificRecordBase> implements Serializer<T> {

	// private static final Logger LOGGER = LoggerFactory.getLogger(AvroSerializer.class);

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

				DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(data.getSchema());
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