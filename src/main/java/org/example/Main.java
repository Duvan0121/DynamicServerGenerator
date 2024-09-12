package org.example;

import org.yaml.snakeyaml.Yaml;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws URISyntaxException {
        // Nombre del archivo YAML de OpenAPI que se cargará.
        String nameFile = "frutas.yaml";
        // Obtener la ruta completa del archivo YAML.
        String filePath = System.getProperty("user.dir") + File.separator + nameFile;

        // Crear una instancia de Yaml para cargar el archivo YAML.
        Yaml yaml = new Yaml();
        // Mapa para almacenar el documento Swagger cargado.
        Map<String, Object> swaggerDocument = null;

        // Cargar el archivo YAML utilizando FileInputStream.
        try (FileInputStream fis = new FileInputStream(new File(filePath))) {
            swaggerDocument = yaml.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Variable para definir si el servidor será local o remoto.
        boolean isLocal = true; // Cambiar a false si es remoto.

        // Generar el código del servidor o endpoints a partir del documento Swagger.
        String serverCode = generateServerCode(swaggerDocument, isLocal);

        // Crear el directorio 'outputsApis' si no existe.
        File routesDir = new File(System.getProperty("user.dir") + File.separator + "outputsApis");
        if (!routesDir.exists()) {
            routesDir.mkdir();
        }

        // Crear y escribir el archivo JavaScript que contendrá el servidor.
        File serverFile = new File(routesDir, nameFile.replace(".yaml", ".js"));
        try (FileWriter fileWriter = new FileWriter(serverFile)) {
            fileWriter.write(serverCode);
            System.out.println("Archivo de servidor dinámico generado con éxito: " + serverFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Genera el código del servidor en JavaScript basado en el documento Swagger y si es local o remoto.
     *
     * @param swaggerDoc Mapa del documento Swagger cargado desde el archivo YAML.
     * @param isLocal Indica si el servidor será ejecutado localmente o no.
     * @return String con el código del servidor en JavaScript.
     * @throws URISyntaxException Excepción si la URI no es válida.
     */
    public static String generateServerCode(Map<String, Object> swaggerDoc, boolean isLocal) throws URISyntaxException {
        // Extraer las secciones 'components', 'schemas' y 'servers' del documento Swagger.
        Map<String, Object> components = (Map<String, Object>) swaggerDoc.getOrDefault("components", new HashMap<>());
        Map<String, Object> schemas = (Map<String, Object>) components.getOrDefault("schemas", new HashMap<>());
        List<Map<String, String>> servers = (List<Map<String, String>>) swaggerDoc.getOrDefault("servers", new ArrayList<>());
        Map<String, Object> info = (Map<String, Object>) swaggerDoc.get("info");

        // Obtener la URL del servidor y extraer el puerto de la misma.
        String url = servers.size() > 0 ? servers.get(0).get("url") : null;
        URI uri = new URI(url);
        int port = uri.getPort();

        // StringBuilders para construir el código dinámico de los esquemas y handlers.
        StringBuilder handlers = new StringBuilder();
        StringBuilder schemasInfo = new StringBuilder();

        // Mensaje inicial con la información general de la API.
        StringBuilder initialMessage = new StringBuilder();
        initialMessage.append("console.log('Bienvenido a la API');\n\n"+
                "//----INFORMACION GENERAL DE LA API----\n"+
                "//Title: "+info.get("title")+"\n" +
                "//Description: "+info.get("description")+"\n" +
                "//Version: "+info.get("version")+"\n\n"
        );

        // Recorrer los esquemas definidos en el archivo YAML.
        for (Map.Entry<String, Object> entry : schemas.entrySet()) {
            String schemaName = entry.getKey();
            Map<String, Object> schema = (Map<String, Object>) entry.getValue();
            Map<String, Object> properties = (Map<String, Object>) schema.get("properties");

            // Crear un arreglo de objetos con datos simulados para el esquema actual.
            schemasInfo.append(String.format("//----DATA PARA EL OBJETO %1$s----\n"+
                    "const %1$sList = [", schemaName.toLowerCase()
            ));

            // Generar cinco instancias con datos aleatorios para cada esquema.
            for (int i = 1; i <= 5; i++) {
                schemasInfo.append("{");

                // Asignar un ID único manualmente para cada instancia.
                schemasInfo.append(String.format("id: %d,", i));
                int size = properties.size();

                // Recorrer las propiedades del esquema para asignarles un valor aleatorio.
                for (Map.Entry<String, Object> propEntry : properties.entrySet()) {
                    String propName = propEntry.getKey();

                    // Evitar duplicar la propiedad 'id'.
                    if (!propName.equalsIgnoreCase("id")) {
                        Map<String, Object> propertyDetails = (Map<String, Object>) propEntry.getValue();
                        String type = (String) propertyDetails.get("type");
                        String randomValue = generateRandomValue(type);

                        schemasInfo.append(String.format("%s: %s, ", propName, randomValue));
                    }
                }
                // Eliminar la última coma y espacio sobrante.
                if (schemasInfo.length() > 2) {
                    schemasInfo.setLength(schemasInfo.length() - 2);
                }
                schemasInfo.append("},");
            }
            // Eliminar la coma final sobrante.
            if (schemasInfo.charAt(schemasInfo.length() - 1) == ',') {
                schemasInfo.setLength(schemasInfo.length() - 1);
            }

            schemasInfo.append("];\n\n");

            // Generar los handlers (endpoints) para cada esquema.
            handlers.append(String.format(
                    "\n//----ENDPOINTS DEL OBJETO /%1$s----\n" +
                            "// GET para obtener todos los objetos '%1$s'\n" +
                            "app.get('/%1$ss', (req, res) => {\n" +
                            "    res.status(200).json(%1$sList);\n" +
                            "});\n\n" +
                            "// GET para obtener un objeto '%1$s' por ID\n" +
                            "app.get('/%1$s/:id', (req, res) => {\n" +
                            "    const item = %1$sList.find(i => i.id === parseInt(req.params.id));\n" +
                            "    if (item) res.status(200).json(item);\n" +
                            "    else res.status(404).json({ message: '%1$s no encontrado' });\n" +
                            "});\n\n" +
                            "// POST para crear un nuevo '%1$s'\n" +
                            "app.post('/%1$s', (req, res) => {\n" +
                            "    delete req.body.id;\n" +
                            "    const newItem = { id: %1$sList.length + 1, ...req.body };\n" +
                            "    if (Object.keys(newItem).length < %2$d) {\n" +
                            "        res.status(400).json('Faltan campos requeridos');\n" +
                            "    } else {\n" +
                            "        %1$sList.push(newItem);\n" +
                            "        res.status(201).json(newItem);\n" +
                            "    }\n" +
                            "});\n\n" +
                            "// PUT para actualizar un '%1$s' existente\n" +
                            "app.put('/%1$s/:id', (req, res) => {\n" +
                            "    const itemIndex = %1$sList.findIndex(i => i.id === parseInt(req.params.id));\n" +
                            "    if (itemIndex !== -1) {\n" +
                            "        %1$sList[itemIndex] = { id: %1$sList[itemIndex].id, ...req.body };\n" +
                            "        res.json(%1$sList[itemIndex]);\n" +
                            "    } else {\n" +
                            "        res.status(404).json({ message: '%1$s no encontrado' });\n" +
                            "    }\n" +
                            "});\n\n" +
                            "// DELETE para eliminar un '%1$s' por ID\n" +
                            "app.delete('/%1$s/:id', (req, res) => {\n" +
                            "    const itemIndex = %1$sList.findIndex(i => i.id === parseInt(req.params.id));\n" +
                            "    if (itemIndex !== -1) {\n" +
                            "        %1$sList.splice(itemIndex, 1);\n" +
                            "        res.status(200).json({message: '%1$s Eliminado'});\n" +
                            "    } else {\n" +
                            "        res.status(404).json({ message: '%1$s no encontrado' });\n" +
                            "    }\n" +
                            "});\n\n",
                    schemaName.toLowerCase(), properties.size()
            ));
        }

        // Retornar el código del servidor en función de si es local o remoto.
        if (isLocal) {
            return initialMessage.toString() +
                    "//----INFORMACION SERVIDOR LOCAL CON NODE Y EXPRESS----\n" +
                    "const express = require('express');\n" +
                    "const app = express();\n" +
                    "app.use(express.json());\n\n" +
                    schemasInfo.toString() +
                    handlers.toString() +
                    "\napp.listen(" + port + ", () => console.log('Servidor corriendo en " + url + "'));\n";
        } else {
            return initialMessage.toString() + schemasInfo.toString() + handlers.toString();
        }
    }

    /**
     * Genera un valor aleatorio basado en el tipo de dato.
     *
     * @param type Tipo de dato (string, integer, boolean, etc.).
     * @return String con un valor aleatorio adecuado para el tipo de dato.
     */
    public static String generateRandomValue(String type) {
        Random random = new Random();
        switch (type) {
            case "string":
                return "\"" + UUID.randomUUID().toString().substring(0, 8) + "\"";
            case "integer":
                return String.valueOf(random.nextInt(100));
            case "boolean":
                return String.valueOf(random.nextBoolean());
            case "number":
                return String.valueOf(random.nextDouble() * 100);
            default:
                return "\"unknown\"";
        }
    }
}
