package web.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

/**
 * 카카오맵 API 서비스
 * - 주소를 좌표로 변환
 * - 두 지점 간 거리 및 이동 시간 계산
 * - 대중교통 경로 추천
 */
@Service
@RequiredArgsConstructor
public class KakaoMapService {

    // application.properties에서 주입
    // kakao.api.key=your-kakao-rest-api-key
    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    // WebClient: 비동기 HTTP 클라이언트 (RestTemplate의 최신 버전)
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://dapi.kakao.com")
            .build();

    // ============================================
    // [1] 주소를 좌표로 변환 (Geocoding)
    // ============================================

    /**
     * 주소를 좌표(위도, 경도)로 변환
     * @param address 도로명 주소 또는 지번 주소
     * @return Map {lat: 위도, lng: 경도} (실패시 null)
     */
    public Map<String, Double> getCoordinatesFromAddress(String address) {
        try {
            // 카카오 주소 검색 API 호출
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/address.json")
                            .queryParam("query", address)
                            .build())
                    .header("Authorization", "KakaoAK " + kakaoApiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();  // 동기 방식으로 결과 대기

            // JSON 파싱
            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            JsonArray documents = jsonObject.getAsJsonArray("documents");

            if (documents.size() > 0) {
                JsonObject location = documents.get(0).getAsJsonObject();

                // 좌표 추출
                double lat = location.get("y").getAsDouble();  // 위도
                double lng = location.get("x").getAsDouble();  // 경도

                Map<String, Double> coordinates = new HashMap<>();
                coordinates.put("lat", lat);
                coordinates.put("lng", lng);

                return coordinates;
            }

            return null;  // 주소를 찾지 못함

        } catch (Exception e) {
            System.err.println("주소 검색 실패: " + e.getMessage());
            return null;
        }
    }

    // ============================================
    // [2] 두 지점 간 거리 계산 (직선 거리)
    // ============================================

    /**
     * 두 좌표 간 직선 거리 계산 (Haversine 공식)
     * @param lat1 출발지 위도
     * @param lng1 출발지 경도
     * @param lat2 도착지 위도
     * @param lng2 도착지 경도
     * @return 거리 (킬로미터)
     */
    public double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        // 지구 반지름 (km)
        final double EARTH_RADIUS = 6371.0;

        // 라디안 변환
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        // Haversine 공식
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 거리 계산 (km)
        return EARTH_RADIUS * c;
    }

    // ============================================
    // [3] 대중교통 경로 조회
    // ============================================

    /**
     * 대중교통 경로 조회 (카카오 길찾기 API)
     * @param startLat 출발지 위도
     * @param startLng 출발지 경도
     * @param endLat 도착지 위도
     * @param endLng 도착지 경도
     * @return 경로 정보 Map
     */
    public Map<String, Object> getPublicTransitRoute(
            double startLat, double startLng,
            double endLat, double endLng) {

        try {
            // 카카오 길찾기 API 호출
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/geo/transcoord.json")
                            .queryParam("x", startLng)
                            .queryParam("y", startLat)
                            .queryParam("input_coord", "WGS84")
                            .queryParam("output_coord", "WGS84")
                            .build())
                    .header("Authorization", "KakaoAK " + kakaoApiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 실제 대중교통 경로는 카카오 모빌리티 API 사용 (별도 신청 필요)
            // 여기서는 간단한 정보만 반환
            Map<String, Object> routeInfo = new HashMap<>();

            // 직선 거리 계산
            double distance = calculateDistance(startLat, startLng, endLat, endLng);

            // 예상 시간 계산 (대중교통 평균 속도: 시속 30km)
            double estimatedTime = (distance / 30.0) * 60;  // 분 단위

            routeInfo.put("distance", Math.round(distance * 100.0) / 100.0);  // km, 소수점 2자리
            routeInfo.put("estimatedTime", Math.round(estimatedTime));  // 분
            routeInfo.put("method", "대중교통");

            return routeInfo;

        } catch (Exception e) {
            System.err.println("경로 조회 실패: " + e.getMessage());
            return null;
        }
    }

    // ============================================
    // [4] 교통수단별 경로 정보 조회
    // ============================================

    /**
     * 교통수단에 따른 경로 정보 계산
     * @param startLat 출발지 위도
     * @param startLng 출발지 경도
     * @param endLat 도착지 위도
     * @param endLng 도착지 경도
     * @param trafficType 교통수단 타입
     * @return 경로 정보 Map
     */
    public Map<String, Object> getRouteByTrafficType(
            double startLat, double startLng,
            double endLat, double endLng,
            String trafficType) {

        Map<String, Object> routeInfo = new HashMap<>();

        // 직선 거리 계산
        double distance = calculateDistance(startLat, startLng, endLat, endLng);
        double estimatedTime;
        String method;

        // 교통수단별 평균 속도 및 계산
        switch (trafficType) {
            case "SUBWAY_AND_BUS":
            case "SUBWAY":
            case "BUS":
                // 대중교통: 시속 30km
                estimatedTime = (distance / 30.0) * 60;
                method = "대중교통";
                break;

            case "CAR":
                // 자동차: 시속 50km (도심 기준)
                estimatedTime = (distance / 50.0) * 60;
                method = "자동차";
                break;

            case "BICYCLE":
                // 자전거: 시속 15km
                estimatedTime = (distance / 15.0) * 60;
                method = "자전거";
                break;

            case "WALK":
                // 도보: 시속 4km
                estimatedTime = (distance / 4.0) * 60;
                method = "도보";
                break;

            default:
                estimatedTime = (distance / 30.0) * 60;
                method = "대중교통";
        }

        routeInfo.put("distance", Math.round(distance * 100.0) / 100.0);  // km
        routeInfo.put("estimatedTime", Math.round(estimatedTime));  // 분
        routeInfo.put("method", method);
        routeInfo.put("trafficType", trafficType);

        return routeInfo;
    }

    // ============================================
    // [5] 최적 교통수단 추천
    // ============================================

    /**
     * 거리에 따른 최적 교통수단 추천
     * @param distance 거리 (km)
     * @param preferredTraffic 사용자 선호 교통수단
     * @return 추천 교통수단
     */
    public String recommendTrafficType(double distance, String preferredTraffic) {
        // 1km 미만: 도보 추천
        if (distance < 1.0) {
            return "WALK";
        }
        // 1~3km: 자전거 또는 버스
        else if (distance < 3.0) {
            return preferredTraffic.equals("BICYCLE") ? "BICYCLE" : "BUS";
        }
        // 3~10km: 대중교통
        else if (distance < 10.0) {
            return "SUBWAY_AND_BUS";
        }
        // 10km 이상: 대중교통 또는 자동차
        else {
            return preferredTraffic.equals("CAR") ? "CAR" : "SUBWAY_AND_BUS";
        }
    }
}