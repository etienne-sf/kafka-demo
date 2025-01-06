/**
 * 
 */
package org.etienne.kafka_demo.utils;

import java.io.InputStream;

import org.apache.avro.Schema;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Différents utilitaires
 * 
 * @author etienne-sf
 */
@Component
@Slf4j
public class Util {

	/**
	 * Chargement d'un schéma avro depuis un fichier accessible dans le jar.
	 * 
	 * @param resourcePath Path vers le fichier qui contient le schéma (ex:
	 *                     avro/monitoring.cpu_v01.avsc)
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

}
