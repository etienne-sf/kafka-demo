/**
 * 
 */
package org.etienne.kafka_demo.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.springframework.stereotype.Component;

/**
 * Différents utilitaires
 * 
 * @author etienne-sf
 */
@Component
public class Util {

	/**
	 * Chargement d'un schéma avro depuis un fichier accessible dans le jar.
	 * 
	 * @param resourcePath
	 *            Path vers le fichier qui contient le schéma (ex: avro/monitoring.cpu_v01.avsc)
	 * @return
	 */
	public Schema loadSchema(String resourcePath) {
		try (InputStream schemaStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath)) {
			if (schemaStream == null) {
				throw new IllegalArgumentException("Le fichier de schéma Avro n'a pas été trouvé : " + resourcePath);
			}
			return new Schema.Parser().parse(schemaStream);
		} catch (Exception e) {
			throw new RuntimeException(
					"Erreur lors du chargement du schéma Avro depuis les ressources : " + resourcePath, e);
		}
	}

	public GenericRecord deserializeAvroMessage(byte[] message, Schema schema) throws IOException {
		GenericDatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(message);
		Decoder decoder = DecoderFactory.get().binaryDecoder(inputStream, null);
		return reader.read(null, decoder);
	}
}
