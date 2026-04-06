# PII Detection System

## 개요
이 프로젝트는 개인정보(PII: Personally Identifiable Information)를 검출하고, 검출된 결과를 API를 통해 획득하여 데이터베이스에 저장하는 기능을 제공합니다.

## 주요 기능
- 🔍 **개인정보 검출 결과 획득**  
  외부 PII Detection API를 호출하여 텍스트/문서 내 개인정보 검출 결과를 가져옵니다.

- 💾 **데이터베이스 저장**  
  획득한 개인정보 검출 결과를 DB에 저장하여 후속 분석 및 관리에 활용할 수 있습니다.

## 실행 방법
1. 애플리케이션 실행
   ```bash
   java -jar pii-detection.jar

## 외부 설정 파일로 실행
```shell
java -jar .\pii-detection-0.0.1.jar --spring.config.location=file:./application.yaml
```