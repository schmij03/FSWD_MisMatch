package ch.zhaw.fswd.powerdate.entity.enums;

public enum ReadStatus {
    SENT, READ;

    @Override
    public String toString() {
        return name();
    }
}
