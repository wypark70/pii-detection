package com.atsoft.piidetection.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilTest {

    @Test
    void findFilesByNames() throws IOException {
        List<Path> paths = FileUtil.findFilesByNames("D:\\DevHome\\projects\\pii-detection", List.of("FileUtilTest.java", "FileUtil.java"));
        paths.forEach(System.out::println);
    }

    @Test
    void findFilesByName() throws IOException {
        List<Path> paths = FileUtil.findFilesByName("D:\\DevHome\\projects\\pii-detection", "FileUtilTest.java");
        paths.forEach(System.out::println);
    }

    @Test
    void deleteFolderContents() {
    }

    @Test
    void copyFilesToTarget() {
    }
}