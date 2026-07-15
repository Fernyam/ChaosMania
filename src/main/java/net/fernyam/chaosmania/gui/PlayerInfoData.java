package net.fernyam.chaosmania.gui;

import java.util.Objects;

import static net.fernyam.chaosmania.data.settings.SettingsManager.ALL_PLAYER_UUID;

public class PlayerInfoData {
    private final String uuid;
    private final String name;
    private final boolean isAllPlayers;

    public static final PlayerInfoData ALL_PLAYERS = new PlayerInfoData(ALL_PLAYER_UUID, "§6§l[ВСЕМ]", true);

    public PlayerInfoData(String uuid, String name) {
        this(uuid, name, false);
    }

    public PlayerInfoData(String uuid, String name, boolean isAllPlayers) {
        this.uuid = uuid;
        this.name = name;
        this.isAllPlayers = isAllPlayers;
    }

    public String getUuid() { return uuid; }
    public String getName() { return name; }
    public boolean isAllPlayers() { return isAllPlayers; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PlayerInfoData that = (PlayerInfoData) obj;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return name + " (" + uuid + ")";
    }
}