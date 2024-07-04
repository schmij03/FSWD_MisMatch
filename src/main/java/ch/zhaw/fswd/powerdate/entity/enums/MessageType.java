package ch.zhaw.fswd.powerdate.entity.enums;

public enum MessageType {
    TEXT, IMAGE;

    @Override
    public String toString() {
        return name();
    }
}
