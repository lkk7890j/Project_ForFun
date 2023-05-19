package idv.tfp10105.project_forfun.common;

public enum Gender {
    NONE(-1), BOTH(0), MALE(1), FEMALE(2);

    private final int code;

    Gender(int code) {
        this.code = code;
    }

    public int toInt() {
        return code;
    }

    public static Gender toEnum (int code) {
        for (Gender gender : values()) {
            if (gender.code == code) {
                return gender;
            }
        }

        return NONE;
    }
}
