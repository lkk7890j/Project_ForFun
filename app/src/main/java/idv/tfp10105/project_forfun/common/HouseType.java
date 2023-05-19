package idv.tfp10105.project_forfun.common;

public enum HouseType {
    NONE(-1), WITH_BATH(0), NO_BATH(1);

    private final int code;

    HouseType(int code) {
        this.code = code;
    }

    public int toInt() {
        return code;
    }

    public static HouseType toEnum (int code) {
        for (HouseType type : values()) {
            if (type.code == code) {
                return type;
            }
        }

        return NONE;
    }
}
