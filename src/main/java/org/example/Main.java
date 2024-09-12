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
        String nameFile = "frutas.yaml";
        String filePath = System.getProperty("user.dir") + File.separator + nameFile;

        // Cargar el archivo YAML de OpenAPI
        Yaml yaml = new Yaml();
        Map<String, Object> swaggerDocument = null;

        try (FileInputStream fis = new FileInputStream(new File(filePath))) {
            swaggerDocument = yaml.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Definir si es local o remoto
        boolean isLocal = false; // Cambiar a false si es remoto

        // Generar el código del servidor o los endpoints
        String serverCode = generateServerCode(swaggerDocument, isLocal);

        // Asegurarse de que el directorio 'outputsApis' exista
        File routesDir = new File(System.getProperty("user.dir") + File.separator + "outputsApis");
        if (!routesDir.exists()) {
            routesDir.mkdir();
        }

        // Escribir el archivo del servidor como JavaScript
        File serverFile = new File(routesDir, nameFile.replace(".yaml", ".js"));
        try (FileWriter fileWriter = new FileWriter(serverFile)) {
                fileWriter.write(serverCode);
            System.out.println("Archivo de servidor dinámico generado con éxito: " + serverFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateServerCode(Map<String, Object> swaggerDoc, boolean isLocal) throws URISyntaxException {
        Map<String, Object> components = (Map<String, Object>) swaggerDoc.getOrDefault("components", new HashMap<>());
        Map<String, Object> schemas = (Map<String, Object>) components.getOrDefault("schemas", new HashMap<>());
        List<Map<String, String>> servers = (List<Map<String, String>>) swaggerDoc.getOrDefault("servers", new ArrayList<>());
        Map<String, Object> info = (Map<String, Object>) swaggerDoc.get("info");
        String url = servers.size() > 0 ? servers.get(0).get("url") : null;
        URI uri = new URI(url);
        int port = uri.getPort();

        // Crear la representación en memoria para los esquemas y las propiedades
        StringBuilder handlers = new StringBuilder();
        StringBuilder schemasInfo = new StringBuilder();

        // Mensaje inicial que se muestra solo una vez
        StringBuilder initialMessage = new StringBuilder();
        initialMessage.append("console.log('Bienvenido a la API');\n\n"+
                "//----INFORMACION GENERAL DE LA API----\n"+
                "//Title: "+info.get("title")+"\n" +
                "//Description: "+info.get("description")+"\n" +
                "//Version: "+info.get("version")+"\n\n"
        );


        for (Map.Entry<String, Object> entry : schemas.entrySet()) {
            String schemaName = entry.getKey();
            Map<String, Object> schema = (Map<String, Object>) entry.getValue();
            Map<String, Object> properties = (Map<String, Object>) schema.get("properties");

            // Crear una variable que guarde múltiples objetos con datos aleatorios
            schemasInfo.append(String.format("//----DATA PARA EL OBJETO %1$s----\n"+
                    "const %1$sList = [", schemaName.toLowerCase()
            ));

            // Generar tres instancias con datos aleatorios
            for (int i = 1; i <= 5; i++) {
                schemasInfo.append("{");

                // Asignar id manualmente y evitar duplicar
                schemasInfo.append(String.format("id: %d,", i));
                int size = properties.size();

                for (Map.Entry<String, Object> propEntry : properties.entrySet()) {
                    String propName = propEntry.getKey();

                    // Evitar agregar "id" si ya se ha añadido manualmente
                    if (!propName.equalsIgnoreCase("id")) {
                        Map<String, Object> propertyDetails = (Map<String, Object>) propEntry.getValue();
                        String type = (String) propertyDetails.get("type");
                        String randomValue = generateRandomValue(type);

                        schemasInfo.append(String.format("%s: %s, ", propName, randomValue));

                    }
                }
                if (schemasInfo.length() > 2) {
                    schemasInfo.setLength(schemasInfo.length() - 2);  // Elimina solo la última coma y espacio
                }
                schemasInfo.append("},");
            }
            // Eliminar la última coma después del último objeto
            if (schemasInfo.charAt(schemasInfo.length() - 1) == ',') {
                schemasInfo.setLength(schemasInfo.length() - 1);  // Elimina la última coma
            }

            schemasInfo.append("];\n\n");

            // Crear o actualizar handlers
            handlers.append(String.format(
                    "\n//----ENDPOINTS DEL OBJETO /%1$s----\n" +
                            "//Se hace un GET general sobre '/%1$ss'\n" +
                            "app.get('/%1$ss', (req, res) => {\n" +
                            "    res.status(200).json(%1$sList);\n" +
                            "});\n\n" +
                            "//Se hace un GET individual sobre '/%1$s'\n" +
                            "app.get('/%1$s/:id', (req, res) => {\n" +
                            "    const item = %1$sList.find(i => i.id === parseInt(req.params.id));\n" +
                            "    if (item) res.status(200).json(item);\n" +
                            "    else res.status(404).json({ message: '%1$s no encontrado' });\n" +
                            "});\n\n" +
                            "//Se hace un POST sobre '/%1$s'\n" +
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
                            "//Se hace un PUT individual sobre '/%1$s'\n" +
                            "app.put('/%1$s/:id', (req, res) => {\n" +
                            "    const itemIndex = %1$sList.findIndex(i => i.id === parseInt(req.params.id));\n" +
                            "    if (itemIndex !== -1) {\n" +
                            "        %1$sList[itemIndex] = { id: %1$sList[itemIndex].id, ...req.body };\n" +
                            "        res.json(%1$sList[itemIndex]);\n" +
                            "    } else {\n" +
                            "        res.status(404).json({ message: '%1$s no encontrado' });\n" +
                            "    }\n" +
                            "});\n\n" +
                            "//Se hace un DELETE individual sobre '/%1$ss'\n" +
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

        // Verificar si es local o remoto
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

    // Método para generar un valor aleatorio según el tipo de dato
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
