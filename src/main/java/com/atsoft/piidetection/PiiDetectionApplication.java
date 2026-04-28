package com.atsoft.piidetection;

import com.atsoft.piidetection.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import picocli.CommandLine; // Picocli 라이브러리를 사용합니다.
import picocli.CommandLine.Command;


// Picocli의 Callable 구조를 활용하기 위해 추가
@SpringBootApplication
@Slf4j
// PiiDetectionApplication 클래스 자체를 Root Command로 정의하여 Subcommand들을 포함시킵니다.
@Command(name = "pii-detection",
        description = "PII Detection System CLI Tool. 개인 식별 정보 검색 및 분석 도구.",
        version = "1.0.0",
        mixinStandardHelpOptions = true) // -h 또는 --help를 Root Command 수준에서 지원
public class PiiDetectionApplication {

    // main 메서드는 그대로 유지
    public static void main(String[] args) {
        SpringApplication.run(PiiDetectionApplication.class, args);
    }

    /**
     * [핵심 로직] 이 CommandLineRunner 빈이 CLI 쉘 엔진 역할을 수행합니다.
     */
    @Bean
    CommandLineRunner run(AppConfig config, InfoCommand infoCommand, GreetingCommand greetingCommand) {
        // Picocli의 Root Command를 사용하여 SubCommand들을 등록합니다.
        // new CommandLine(this)는 PiiDetectionApplication 인스턴스를 Root Command로 지정합니다.
        CommandLine cmd = new CommandLine(this);
        cmd.addSubcommand("info", infoCommand);
        cmd.addSubcommand("greet", greetingCommand);

        return args -> {
            if (args.length == 0) {
                // [CASE A: 대화형 쉘 모드 시작] - 인터랙티브 루프
                System.out.println("\n===============================================");
                System.out.println("🚀 PII Detection System CLI 쉘이 시작되었습니다.");
                System.out.printf("✨ 시스템 정보: %s\n", config.getName());
                System.out.println("-----------------------------------------------");

                // Picocli가 생성한 통합된 도움말을 출력합니다.
                cmd.usage(System.out);


                java.util.Scanner scanner = new java.util.Scanner(System.in);
                String prompt = "\n\nEPrM-PII-Detection> ";
                while (true) {
                    System.out.print(prompt);
                    if (!scanner.hasNextLine()) break;

                    String input = scanner.nextLine().trim();
                    if (input.isEmpty()) continue;

                    // 1. 종료 명령어 처리
                    if ("quit".equalsIgnoreCase(input) || "exit".equalsIgnoreCase(input)) {
                        System.out.println("\n✅ 프로그램을 종료합니다.");
                        break;
                    }

                    // 2. Picocli에게 실행을 위임 (가장 큰 변화):
                    try {
                        // cmd.execute()는 입력된 인자를 분석하여 적절한 subCommand를 찾고,
                        // 그 커맨드의 call() 메서드를 자동으로 호출해 줍니다.
                        cmd.execute(input.split("\\s+"));

                    } catch (Exception e) {
                        // Picocli가 자체적으로 에러 처리와 도움말 출력을 하므로, 별도의 예외 처리는 최소화합니다.
                        System.err.println("\n❌ 명령어 실행 중 알 수 없는 오류가 발생했습니다.");
                    }
                }
                scanner.close();

            } else {
                // [CASE B: 일회성 명령 실행 모드] - Non-interactive Mode
                System.out.println("===============================================");
                System.out.printf("🚀 PII Detection System CLI를 초기화된 인자 [%s]로 시작합니다.\n", java.util.Arrays.toString(args));

                try {
                    // Picocli가 파싱하고 필요한 명령어를 찾아 자동으로 execute()를 호출합니다.
                    cmd.execute(args);
                } catch (Exception e) {
                    System.err.println("\n❌ 프로그램 실행 중 오류가 발생했습니다.");
                }

                System.out.println("-----------------------------------------------");
                System.out.println("\n✅ 프로그램이 요청된 명령을 모두 실행하고 종료됩니다.");
            }
        };
    }
}