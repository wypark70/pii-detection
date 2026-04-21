package com.atsoft.piidetection.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class FileUtil {
    private FileUtil() {
    }

    /**
     * 시작 폴더와 파일 이름 목록을 받아서 하위 모든 폴더에서 파일을 재귀적으로 검색하여 찾아낸 경로 목록을 반환합니다.
     * 여러 파일 이름을 동시에 검색할 때 효율적입니다.
     *
     * @param startFolderPath 검색을 시작할 루트 디렉토리의 경로 (String 형식)
     * @param fileNames       찾고자 하는 파일 이름 목록 (예: "report.pdf", "config.xml")
     * @return 발견된 모든 파일의 Path 객체 리스트
     * @throws IOException 폴더 접근 중 I/O 오류가 발생했을 경우
     */
    public static List<Path> findFilesByNames(String startFolderPath, List<String> fileNames) throws IOException {
        if (fileNames == null || fileNames.isEmpty()) {
            System.err.println("경고: 검색할 파일 이름 목록이 비어 있습니다.");
            return new ArrayList<>();
        }

        List<Path> foundPaths = new ArrayList<>();

        // 1. 시작 경로를 Path 객체로 변환합니다.
        Path startPath = Paths.get(startFolderPath);

        // 2. 시작 폴더가 실제로 존재하는지 확인합니다.
        if (!Files.exists(startPath) || !Files.isDirectory(startPath)) {
            System.err.println("오류: 지정된 시작 폴더 경로가 유효하지 않거나 디렉토리가 아닙니다. " + startFolderPath);
            return foundPaths; // 빈 리스트 반환
        }

        // 검색 효율을 위해 파일 이름 목록을 Set<String>으로 변환하여 O(1) 시간 복잡도로 조회합니다.
        Set<String> targetFileNames = new HashSet<>(fileNames);


        // 3. Files.walk()를 사용하여 모든 하위 디렉토리와 파일을 순회하고 필터링합니다.
        try (Stream<Path> walk = Files.walk(startPath)) {
            walk.filter(Files::isRegularFile) // 파일만 필터링
                    .forEach(path -> {
                        String currentFileName = path.getFileName().toString();

                        // 4. 현재 파일의 이름이 목표 목록에 포함되어 있는지 확인합니다. (Set 검색 사용)
                        if (targetFileNames.contains(currentFileName)) {
                            foundPaths.add(path);
                        }
                    });
        }

        return foundPaths;
    }

    /**
     * 지정된 폴더 경로 내의 모든 파일과 하위 폴더를 재귀적으로 삭제합니다.
     * 이 함수는 안전한 처리를 위해 깊은 곳의 내용부터 순차적으로 삭제하는 bottom-up 방식으로 동작합니다.
     *
     * @param startFolderPath 삭제할 폴더의 루트 디렉토리 경로 (String 형식)
     * @return 성공적으로 처리된 경우 true, 실패하거나 경고가 발생한 경우 false를 반환합니다.
     */
    public static boolean deleteFolderContents(String startFolderPath) {
        if (startFolderPath == null || startFolderPath.trim().isEmpty()) {
            System.err.println("경고: 삭제할 폴더 경로가 제공되지 않았습니다.");
            return false;
        }

        Path startPath = Paths.get(startFolderPath);

        // 1. 시작 폴더가 실제로 존재하고 디렉토리인지 확인합니다.
        if (!Files.exists(startPath) || !Files.isDirectory(startPath)) {
            System.err.println("오류: 지정된 경로가 유효하지 않거나 디렉토리가 아닙니다. " + startFolderPath);
            return false;
        }

        System.out.println("--- 폴더 및 하위 내용을 삭제하는 중: " + startPath.toAbsolutePath());

        try {
            // 2. Files.walk()를 사용하여 시작 경로와 모든 하위 항목을 포함한 스트림을 얻습니다.
            List<Path> pathsToDelete = new ArrayList<>();
            try (Stream<Path> walk = Files.walk(startPath)) {
                pathsToDelete = walk.collect(java.util.stream.Collectors.toList());
            }

            // 3. 삭제는 반드시 가장 깊은 경로부터 시작해야 하므로, 리스트를 역순으로 정렬합니다.
            pathsToDelete.sort(Comparator.reverseOrder());

            int deletedCount = 0;
            for (Path path : pathsToDelete) {
                try {
                    // 4. 파일 또는 디렉토리를 삭제합니다.
                    Files.delete(path);
                    deletedCount++;
                } catch (IOException e) {
                    System.err.println("경고: [" + path.getFileName() + "] 삭제 중 실패했습니다. 권한 문제일 수 있습니다. " + e.getMessage());
                    // 삭제가 불가능한 파일이나 폴더는 건너뜁니다.
                }
            }

            if (deletedCount > 0) {
                System.out.println("성공: 총 " + deletedCount + "개의 항목(파일/폴더)을 성공적으로 삭제했습니다.");
            } else {
                System.out.println("알림: 삭제할 항목이 없거나 모든 항목의 삭제에 실패했습니다.");
            }

        } catch (IOException e) {
            System.err.println("치명적 오류: 폴더 내용을 순회하는 중 예기치 않은 I/O 오류가 발생했습니다. " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * 소스 파일 경로 목록에 있는 모든 파일을 지정된 타겟 폴더로 복사합니다.
     * 대상 폴더가 존재하지 않으면 자동으로 생성합니다.
     *
     * @param sourceFilePaths 원본 파일들의 경로 배열 (String 형식)
     * @param targetDirPath   복사할 목표 디렉토리의 경로 (String 형식)
     * @return 모든 복사가 성공했거나 일부라도 성공하여 처리가 끝난 경우 true, 실패하거나 경고가 발생한 경우 false를 반환합니다.
     */
    public static boolean copyFilesToTarget(String[] sourceFilePaths, String targetDirPath) {
        if (sourceFilePaths == null || sourceFilePaths.length == 0) {
            System.err.println("경고: 복사할 소스 파일 경로 목록이 제공되지 않았습니다.");
            return false;
        }

        if (targetDirPath == null || targetDirPath.trim().isEmpty()) {
            System.err.println("오류: 타겟 디렉토리 경로가 제공되지 않았습니다.");
            return false;
        }

        Path targetPath = Paths.get(targetDirPath);

        // 1. 타겟 폴더가 없으면 생성합니다.
        try {
            Files.createDirectories(targetPath);
            System.out.println("--- 파일을 복사할 타겟 디렉토리를 확인/생성했습니다: " + targetPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("치명적 오류: 타겟 디렉토리 [" + targetDirPath + "]를 생성하거나 접근할 수 없습니다. 권한을 확인해주세요. " + e.getMessage());
            return false;
        }

        int copiedCount = 0;
        boolean success = true;

        System.out.println("--- 파일 복사를 시작합니다...");
        for (String sourcePathStr : sourceFilePaths) {
            if (sourcePathStr == null || sourcePathStr.trim().isEmpty()) continue;

            Path sourcePath = Paths.get(sourcePathStr);

            // 2. 소스 경로 유효성 검사
            if (!Files.exists(sourcePath)) {
                System.err.println("경고: [" + sourcePath.getFileName() + "] 소스 파일이 존재하지 않아 건너뜁니다.");
                success = false; // 실패 카운트는 아니지만, 처리된 항목 중 오류가 있으므로 false 반환에 기여할 수 있음.
                continue;
            }

            if (!Files.isRegularFile(sourcePath)) {
                System.err.println("경고: [" + sourcePath.getFileName() + "] 파일이 아닌 디렉토리 또는 기타 항목이라 복사하지 않습니다.");
                // 이 경우에도 성공 여부에 큰 영향을 주진 않지만, 경고 로그를 남깁니다.
            } else {
                try {
                    // 3. 타겟 경로 설정 (원본 파일 이름을 유지)
                    String fileName = sourcePath.getFileName().toString();
                    Path destinationPath = targetPath.resolve(fileName);

                    // 4. 복사 실행 (덮어쓰기 허용: StandardCopyOption.REPLACE_EXISTING)
                    Files.copy(sourcePath, destinationPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("  [성공] " + sourcePath.getFileName() + " -> " + destinationPath.toAbsolutePath());
                    copiedCount++;

                } catch (IOException e) {
                    System.err.println("오류: [" + sourcePath.getFileName() + "] 파일을 복사하는 중 I/O 오류가 발생했습니다. (" + e.getMessage() + ")");
                    success = false; // 치명적 실패로 간주
                }
            }
        }

        if (copiedCount > 0) {
            System.out.println("--- 파일 복사 완료: 총 " + copiedCount + "개 파일을 성공적으로 복사했습니다.");
        } else {
            System.out.println("--- 파일 복사 완료: 복사된 파일이 없습니다.");
        }

        return success;
    }

    /**
     * 기존의 단일 파일 이름 검색 메서드를 유지하기 위한 오버로딩 메서드입니다.
     */
    public static List<Path> findFilesByName(String startFolderPath, String fileName) throws IOException {
        return findFilesByNames(startFolderPath, List.of(fileName));
    }
}
