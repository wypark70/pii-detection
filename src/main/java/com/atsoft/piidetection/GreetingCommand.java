package com.atsoft.piidetection;

import com.atsoft.piidetection.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable; // run() 메서드를 Callable로 변경하여 사용합니다.

/**
 * --greet 명령어의 로직을 담당하며, Picocli가 처리하는 구조로 변경되었습니다.
 */
@Component
@Command(name = "greet",
        description = "사용자에게 환영 메시지를 출력하고 PII 시스템 사용 시뮬레이션을 수행합니다.",
        mixinStandardHelpOptions = true) // -h 또는 --help 자동 지원 추가
public class GreetingCommand implements Callable<Object> {

    // 필요한 의존성을 주입받습니다.
    private final UserRepository userRepository;
    // Picocli의 @Parameters를 사용하여 CLI 인자 중 첫 번째 값을 name 필드에 자동으로 바인딩합니다.
    @Parameters(index = "0", description = "환영 메시지에 사용할 사람의 이름 (필수)")
    private String name;

    @Autowired
    public GreetingCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 명령어 실행 로직입니다. run() 메서드가 핵심 역할을 합니다.
     */
    @Override
    public Object call() throws Exception {
        if (this.name == null || this.name.trim().isEmpty()) {
            System.out.println("🚨 오류: greet 명령어는 반드시 이름을 인자로 받아야 합니다. 사용법: greet [이름]");
            return null; // 에러를 반환하거나 예외 처리할 수 있습니다.
        }

        String nameValue = this.name;
        System.out.printf("\n🎉 환영합니다, %s님! PII Detection System에 오신 것을 환영합니다.\n", nameValue);

        // DB 조회 시뮬레이션 (기존 로직 유지)
        try {
            var users = userRepository.findAll();
            System.out.println(users);
            System.out.printf("💾 [DB 조회] 사용자 데이터 로드 성공. (총 %d명)\n", users.size());
        } catch (Exception e) {
            System.err.println("🚨 DB 접근 중 오류가 발생했습니다.");
        }
        return null;
    }
}