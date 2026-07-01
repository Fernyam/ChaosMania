package net.fernyam.chaosmania.data.settings;

public abstract class BaseSettings {
    public enum ListType {
        BLACK_LIST,
        WHITE_LIST
    }

    protected String namePlayer;
    protected String uuidPlayer;

    public String getNamePlayer() { return namePlayer; }
    public void setNamePlayer(String namePlayer) { this.namePlayer = namePlayer; }

    public String getUuidPlayer() { return uuidPlayer; }
    public void setUuidPlayer(String uuidPlayer) { this.uuidPlayer = uuidPlayer; }

}