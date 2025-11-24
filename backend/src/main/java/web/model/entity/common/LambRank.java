package web.model.entity.common;

///  양 등급과 등장확률을 정의하는 enum 파일

public enum LambRank {
    COMMON      (1, 66),    // 일반 기본 66% (디폴트값)
    RARE        (2, 25),    // 희귀 기본 25%
    SPECIAL     (3, 8),     // 특급 기본 8%
    LEGENDARY   (4, 1);     // 전설 기본 1%

    public final int rank;
    public final int prob;

    LambRank(int rank, int prob) {
        this.rank = rank;
        this.prob = prob;
    }
}
