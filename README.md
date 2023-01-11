# geo-data-extractor
## 개요
[Kakao Local API](https://developers.kakao.com/docs/latest/ko/local/common)를 활용하여 대한민국 주요 시설 PIA 정보를 수집하여 CSV 파일로 출력한다.

## Prerequisite
Java 17

## 빌드
Project Home Directory로 이동하여 본 프로젝트를 빌드하여 jar를 생성한다.
### Windows
```shell
./gradle.bat bootJar
```
### Mac/Linux
```shell
./gradlew bootJar
```
명령어를 실행하면 프로젝트 `/build/libs` 경로 아래에 `geo-data-extractor-0.0.1-SNAPSHOT.jar`가 생성된다.
해당 파일을 앞으로 사용해서 PIA 데이터를 생성한다.

## 실행
Java로 빌드한 jar를 실행한다.
```shell
./java -jar geo-data-extractor-0.0.1-SNAPSHOT.jar
```

## 사용법
- help : 명령어 설명
  - help {명령어} : 명령어 상세 설명
  
```shell
shell:>help
AVAILABLE COMMANDS

Api Key Commands
       register-key: KAKAO REST API키를 신규로 등록한다.
       list-keys: 등록되어 있는 KAKAO REST API키를 조회한다.
       activate-key: 등록되어 있는 KAKAO REST API키를 주키로 활성화한다.
       remove-key: 등록되어 있는 KAKAO REST API키를 삭제한다.

Built-In Commands
       help: Display help about available commands
       stacktrace: Display the full stacktrace of the last error.
       clear: Clear the shell screen.
       quit, exit: Exit the shell.
       history: Display or save the history of previously run commands
       version: Show version info
       script: Read and execute commands from a file.

Geo Data Create Commands
       create: 위치 데이터 CSV 파일생성 명령
       list-code: 요청 가능한 PIA 시설코드를 조회한다
```

### Rest API Key 등록
본 프로그램은 Kakao Local API를 호출하여 PIA 데이터를 수집한다. Kakao Local API를 사용하기 위해서는 Rest API Key를 발급 받아야 한다.
Key별로 하루 호출 할당량 (100,000)과 월 할당량이 있어서 할당량을 소진한 경우 API를 호출할 수 없다.
따라 전국 데이터를 추출하는 경우 실행 도중 할당량을 모두 소진할 수 있기 때문에 다수의 Key를 등록해서 할당량을 늘릴 수 있다.

- register-key : 키를 등록한다. 다수 키를 등록할 수 있다.
```shell
shell:> register-key test_key1,test_key2,test_key3,test_key4
Registered REST API KEY: [test_key1, test_key2, test_key3, test_key4]
```
- list-keys : 등록된 키를 조회한다. 현재 사용중인 'Main Key'는 *로 표시되어 있다.
```shell
shell:> list-keys
test_key2 - active
test_key3 - active
test_key4 - active
* test_key1 - active
```
위 예시에서는 총 4개의 유효한 키가 등록되어 있고 test_key1이 Main Key다. 데이터를 수집 중 test_key1이 할당량을 모두 소진하면 임의로 다른 활성화된 키를 Main Key로 설정하여 에러없이 진행한다.
```shell
shell:> list-keys
test_key2 - active
test_key3 - active
* test_key4 - active
test_key1 - inactive
```

- create : 데이터를 생성한다.
  - --code : 생성 대상 시설 코드. 다수로 입력가능
  - --extractArea: 생성 범위 대상
    - KOREA : 대한민국 전체
    - SEOUL : 서울
  - --outputPath (optional) : CSV 생성 경로
```shell
shell:> create --code MT1,FD6 --extractArea KOREA
```
