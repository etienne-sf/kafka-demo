package org.etienne.kafka_demo.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

/**
 * 
 * @See https://codenotfound.com/spring-kafka-apache-avro-serializer-deserializer-example.html
 *
 * @param <T>
 *            La classe cible de la sérialisation
 */
public class AvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

	// private static final Logger LOGGER = LoggerFactory.getLogger(AvroDeserializer.class);

	protected final Class<T> targetType;
	protected final String schemaVersion;
	protected DatumReader<GenericRecord> datumReader;

	// public AvroDeserializer() {
	// targetType = (Class<T>) CpuUsage.class;
	// schemaVersion = "v1";
	// }

	public AvroDeserializer(Class<T> targetType, String schemaVersion) {
		this.targetType = targetType;
		this.schemaVersion = schemaVersion;
	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		try {
			datumReader = new SpecificDatumReader<>(//
					targetType.getConstructor().newInstance().getSchema()
			// KafkaDemoApplication.schemasMonitoringCpu.get(schemaVersion)
			);
		} catch (IllegalArgumentException | SecurityException | InstantiationException | IllegalAccessException
				| InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		// Aucun nettoyage requis
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(String topic, byte[] data) {
		try {
			T result = null;

			if (data != null) {
				Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);

				result = (T) datumReader.read(null, decoder);
			}
			return result;
		} catch (Exception ex) {
			throw new SerializationException(
					"Can't deserialize data '" + Arrays.toString(data) + "' from topic '" + topic + "'", ex);
		}
	}
}
