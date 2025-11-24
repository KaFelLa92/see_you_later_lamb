package web.model.entity.common;

public enum LangType {

    KOREAN("ko", "한국어"),
    ENGLISH("en", "영어"),
    CHINESE_SIMPLIFIED("zh-CN", "중국어(간체)"),
    CHINESE_TRADITIONAL("zh-TW", "중국어(번체)"),
    JAPANESE("ja", "일본어"),
    VIETNAMESE("vi", "베트남어"),
    SPANISH("es", "스페인어");

    private final String code;
    private final String displayName;

    LangType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }
}