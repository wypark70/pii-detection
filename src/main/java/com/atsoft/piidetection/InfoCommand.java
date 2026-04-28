package com.atsoft.piidetection;

import com.atsoft.piidetection.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * --info 명령어의 로직을 담당하며, Picocli가 처리하는 구조로 변경되었습니다.
 */
@Component
@Command(name = "info",
        description = "애플리케이션의 버전 및 기본 정보를 표시합니다.",
        mixinStandardHelpOptions = true) // -h 또는 --help 자동 지원 추가
public class InfoCommand implements Runnable {

    private final AppConfig appConfig;

    @Autowired
    public InfoCommand(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    // Picocli를 사용하므로 @Override getCommandName() 등 인터페이스 구현은 제거합니다. 
    // 대신, run() 메소드가 실행됩니다.

    @Override
    public void run() { // Runnable 인터페이스의 run() 메서드를 구현합니다.
        System.out.println("\n===============================================");
        System.out.printf("🌐 PII Detection System CLI (버전 %s)\n", appConfig.getVersion());
        System.out.println("   - 시스템 목적: 개인 식별 정보 검출 및 관리");
        System.out.println("-----------------------------------------------");
    }
}