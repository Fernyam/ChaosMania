package net.fernyam.chaosmania.event;

import net.fernyam.chaosmania.ChaosManiaMod;
import net.fernyam.chaosmania.data.settings.SettingsManager;
import net.fernyam.chaosmania.data.settings.custom.ModSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;
import java.util.List;

public class ModLoadEvent {

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            String uuid = player.getStringUUID();

            // Проверяем, есть ли у игрока настройки модов
            var modSettings = SettingsManager.getModSettings(uuid);

            // Если у игрока нет настроек модов - создаем
            if (modSettings == null) {
                SettingsManager.addNewPlayer(player.getName().getString(), uuid);
                modSettings = SettingsManager.getModSettings(uuid);
            }

            // Проверяем все запрещенные моды
            List<String> blockedMods = checkBlockedMods(uuid, modSettings);

            if (!blockedMods.isEmpty()) {
                // Кикаем игрока
                String message = "Установлены запрещённые моды: " + String.join(", ", blockedMods);
                ChaosManiaMod.LOGGER.warn("Игрок {} ({}) зашёл с запрещёнными модами: {}",
                        player.getName().getString(), uuid, String.join(", ", blockedMods));
                player.connection.disconnect(Component.literal(message));
            }
        }
    }

    private static List<String> checkBlockedMods(String uuid, ModSettings modSettings) {
        List<String> blockedMods = new ArrayList<>();

        // Если контроль модов выключен - пропускаем
        if (!modSettings.isModLoadControlEnabled()) {
            return blockedMods;
        }

        // Получаем все загруженные моды
        var loadedMods = ModList.get().getMods();

        for (var modInfo : loadedMods) {
            String modId = modInfo.getModId();

            // Если мод есть в списке и его нельзя загружать
            if (modSettings.isModExists(modId) && !modSettings.canLoadMod(modId)) {
                blockedMods.add(modId + " (" + modInfo.getDisplayName() + ")");
            }
        }

        return blockedMods;
    }

    // Дополнительный метод для проверки конкретного мода
    private static boolean isModBlockedForPlayer(String uuid, String modId) {
        var modSettings = SettingsManager.getModSettings(uuid);
        if (modSettings == null) return false;
        if (!modSettings.isModLoadControlEnabled()) return false;

        return modSettings.isModExists(modId) && !modSettings.canLoadMod(modId);
    }
}
