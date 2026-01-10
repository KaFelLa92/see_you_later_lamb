package web.model.entity.common;

/// setting의 set_traffic enum 값을 관리하는 파일

public enum TrafficType {
    SUBWAY_AND_BUS,  // 1. 지하철+버스
    SUBWAY,          // 2. 지하철
    BUS,             // 3. 버스
    CAR,             // 4. 자동차
    BICYCLE,         // 5. 자전거
    WALK             // 6. 도보
}

