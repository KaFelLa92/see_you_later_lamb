package web.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import web.exception.CustomException;
import web.exception.ErrorCode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 파일 업로드 유틸리티 클래스
 * 양 일러스트, 프로필 이미지 등의 파일 업로드/삭제를 처리
 *
 * application.properties 설정:
 * file.upload.base-path=/app/uploads
 * file.upload.max-size=10485760  # 10MB
 */
@Slf4j
@Component
public class FileUploadUtil {

    // ========== 설정값 (application.properties에서 주입) ==========

    /**
     * 파일 업로드 기본 경로
     * 예: /app/uploads 또는 C:/uploads
     */
    @Value("${file.upload.base-path}")
    private String basePath;

    /**
     * 최대 파일 크기 (바이트)
     * 기본값: 10MB = 10,485,760 bytes
     */
    @Value("${file.upload.max-size:10485760}")
    private long maxFileSize;

    /**
     * 허용하는 이미지 확장자 목록
     */
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"
    );

    /**
     * 허용하는 MIME 타입 목록
     */
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    // ========== 파일 업로드 메서드 ==========

    /**
     * 파일 업로드
     *
     * 처리 흐름:
     * 1. 파일 유효성 검증 (크기, 확장자, MIME 타입)
     * 2. 고유한 파일명 생성 (UUID 사용)
     * 3. 디렉토리 생성 (없으면)
     * 4. 파일 저장
     * 5. 저장된 파일의 경로 반환
     *
     * @param file 업로드할 파일
     * @param directory 저장할 하위 디렉토리 (예: "lamb", "profile")
     * @return String 저장된 파일의 상대 경로 (예: "lamb/abc123.jpg")
     * @throws CustomException 파일 검증 실패 또는 저장 실패 시
     */
    public String uploadFile(MultipartFile file, String directory) {
        // 1. 파일 존재 여부 확인
        if (file == null || file.isEmpty()) {
            log.warn("파일 업로드 실패: 파일이 비어있음");
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "파일이 비어있습니다.");
        }

        // 2. 파일 크기 검증
        if (file.getSize() > maxFileSize) {
            log.warn("파일 업로드 실패: 파일 크기 초과 - {}bytes (최대 {}bytes)",
                    file.getSize(), maxFileSize);
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        // 3. 파일 확장자 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            log.warn("파일 업로드 실패: 파일명이 없음");
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "파일명이 없습니다.");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            log.warn("파일 업로드 실패: 허용되지 않는 확장자 - {}", extension);
            throw new CustomException(
                    ErrorCode.INVALID_FILE_TYPE,
                    "허용되는 확장자: " + String.join(", ", ALLOWED_EXTENSIONS)
            );
        }

        // 4. MIME 타입 검증
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            log.warn("파일 업로드 실패: 허용되지 않는 MIME 타입 - {}", contentType);
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }

        // 5. 고유한 파일명 생성
        // UUID를 사용하여 파일명 중복 방지
        String uniqueFilename = UUID.randomUUID().toString().replace("-", "") + "." + extension;

        // 6. 저장 경로 설정
        // basePath/directory/uniqueFilename
        // 예: /app/uploads/lamb/abc123.jpg
        Path directoryPath = Paths.get(basePath, directory);
        Path filePath = directoryPath.resolve(uniqueFilename);

        try {
            // 7. 디렉토리 생성 (없으면)
            // 중간 디렉토리까지 모두 생성
            Files.createDirectories(directoryPath);

            // 8. 파일 저장
            // REPLACE_EXISTING: 같은 이름의 파일이 있으면 덮어쓰기
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 9. 상대 경로 반환 (directory/filename)
            // DB에는 상대 경로만 저장 (기본 경로는 변경될 수 있으므로)
            String relativePath = directory + "/" + uniqueFilename;
            log.info("파일 업로드 성공: {}", relativePath);
            return relativePath;

        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생", e);
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED, e);
        }
    }

    // ========== 파일 삭제 메서드 ==========

    /**
     * 파일 삭제
     *
     * @param relativePath 삭제할 파일의 상대 경로 (예: "lamb/abc123.jpg")
     * @throws CustomException 파일 삭제 실패 시
     */
    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            log.warn("파일 삭제 실패: 파일 경로가 비어있음");
            return;  // 경로가 없으면 삭제할 필요 없음
        }

        // 절대 경로 생성
        Path filePath = Paths.get(basePath, relativePath);

        try {
            // 파일 존재 여부 확인 후 삭제
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("파일 삭제 성공: {}", relativePath);
            } else {
                log.warn("파일 삭제 실패: 파일이 존재하지 않음 - {}", relativePath);
            }
        } catch (IOException e) {
            log.error("파일 삭제 중 오류 발생: {}", relativePath, e);
            // 삭제 실패는 치명적이지 않으므로 예외를 던지지 않음
            // 단, 로그는 남김
        }
    }

    // ========== 파일 정보 조회 메서드 ==========

    /**
     * 파일 존재 여부 확인
     *
     * @param relativePath 확인할 파일의 상대 경로
     * @return boolean 파일이 존재하면 true
     */
    public boolean fileExists(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return false;
        }

        Path filePath = Paths.get(basePath, relativePath);
        return Files.exists(filePath);
    }

    /**
     * 파일의 전체 URL 생성
     *
     * @param relativePath 파일의 상대 경로
     * @param baseUrl 서버의 기본 URL (예: "http://localhost:8080")
     * @return String 파일의 전체 URL (예: "http://localhost:8080/uploads/lamb/abc123.jpg")
     */
    public String getFileUrl(String relativePath, String baseUrl) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }

        // baseUrl에 /가 없으면 추가
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        return baseUrl + "uploads/" + relativePath;
    }

    // ========== 헬퍼 메서드 ==========

    /**
     * 파일명에서 확장자 추출
     *
     * @param filename 파일명
     * @return String 확장자 (점 제외)
     */
    private String getFileExtension(String filename) {
        // 마지막 점(.)의 위치 찾기
        int lastIndexOfDot = filename.lastIndexOf('.');

        // 점이 없거나 파일명의 시작이면 빈 문자열 반환
        if (lastIndexOfDot == -1 || lastIndexOfDot == 0) {
            return "";
        }

        // 점 이후의 문자열 반환
        return filename.substring(lastIndexOfDot + 1);
    }

    /**
     * 파일 크기를 사람이 읽기 쉬운 형태로 변환
     *
     * @param bytes 파일 크기 (바이트)
     * @return String 변환된 파일 크기 (예: "1.5 MB")
     */
    public String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * 디렉토리 생성
     *
     * @param directory 생성할 하위 디렉토리명
     * @throws CustomException 디렉토리 생성 실패 시
     */
    public void createDirectory(String directory) {
        Path directoryPath = Paths.get(basePath, directory);

        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
                log.info("디렉토리 생성 성공: {}", directory);
            }
        } catch (IOException e) {
            log.error("디렉토리 생성 실패: {}", directory, e);
            throw new CustomException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "디렉토리 생성에 실패했습니다."
            );
        }
    }
}