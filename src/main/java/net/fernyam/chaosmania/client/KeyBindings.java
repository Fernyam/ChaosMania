package net.fernyam.chaosmania.client;


import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

public class KeyBindings {

    // 1. Создаем объект KeyMapping (пока что null, заполним в событии регистрации)
    public static KeyMapping EXAMPLE_KEY;

    // 2. Это событие вызывается NeoForge специально для регистрации клавиш (MOD bus)

    public static void registerBindings(RegisterKeyMappingsEvent event) {
        // Создаем клавишу. Параметры: имя (локализуемый ключ), контекст, клавиша по умолчанию (R), категория
        EXAMPLE_KEY = new KeyMapping(
                "key.chaosmania.open_master_screen", // Локализуемый ключ
                KeyConflictContext.IN_GAME,         // Конфликтует только когда мы в игре
                InputConstants.Type.KEYSYM,         // Тип: клавиатура
                InputConstants.KEY_R,               // Код клавиши (R)
                "key.category.chaosmania"            // Категория в меню управления
        );

        // Регистрируем её
        event.register(EXAMPLE_KEY);
    }
}