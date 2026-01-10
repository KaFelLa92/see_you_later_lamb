package web.model.entity.common;

public enum CycleType {
    DAILY(1, "일 반복"),
    WEEKLY(2, "주 반복"),
    MONTHLY(3, "월 반복"),
    QUARTERLY(4, "분기 반복"),
    SEMI_ANNUAL(5, "반기 반복"),
    YEARLY(6, "연 반복");

    private final int id;
    private final String description;

    CycleType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
