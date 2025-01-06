package org.etienne.kafka_demo.utils;

import java.util.Arrays;
import java.util.Map;

import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.etienne.kafka_demo.KafkaDemoApplication;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @See https://codenotfound.com/spring-kafka-apache-avro-serializer-deserializer-example.html
 *
 * @param <T> La classe cible de la sérialisation
 */
@Slf4j
public class AvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

	protected final Class<T> targetType;

	public AvroDeserializer(Class<T> targetType) {
		this.targetType = targetType;
	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		// no op
	}

	@Override
	public void close() {
		// Aucun nettoyage requis
	}

	@Override
	public T deserialize(String topic, byte[] data) {
		return deserialize(topic, null, data);
	}

	@Override
	public T deserialize(String topic, Headers headers, byte[] data) {
		try {
			if (data == null) {
				return null;
			} else {
				String schemaVersion = extractVersionFromHeaders(headers);
				DatumReader<SpecificRecord> datumReader = new SpecificDatumReader<>(//
						// targetType.getConstructor().newInstance().getSchema()
						KafkaDemoApplication.schemasMonitoringCpu.get(schemaVersion));
				Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
				@SuppressWarnings("unchecked")
				T result = (T) datumReader.read(null, decoder);
				log.trace("Réception d'un message de version {}: ", schemaVersion, result);
				return result;
			}
		} catch (Exception ex) {
			throw new SerializationException(
					"Can't deserialize data '" + Arrays.toString(data) + "' from topic '" + topic + "'", ex);
		}
	}

	private String extractVersionFromHeaders(Headers headers) {
		// Extract the version from the headers
		if (headers != null && headers.lastHeader("X-Version") != null) {
			return new String(headers.lastHeader("X-Version").value());
		}
		return "v1"; // Default version
	}

}
