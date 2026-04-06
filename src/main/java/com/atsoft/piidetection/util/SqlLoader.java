package com.atsoft.piidetection.util;

import org.springframework.core.io.ClassPathResource;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SqlLoader {
    public static String loadSql(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource(path);
        return new String(Files.readAllBytes(Paths.get(resource.getURI())));
    }
}