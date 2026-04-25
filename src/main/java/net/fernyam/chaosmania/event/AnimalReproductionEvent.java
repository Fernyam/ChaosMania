package net.fernyam.chaosmania.event;

import net.fernyam.chaosmania.ConfigMod;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;


public class AnimalReproductionEvent
{

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if(!ConfigMod.DISABLE_ANIMAL_BREEDING.get()) return;

        if (event.getTarget() instanceof Animal animal) {
            // Получаем текущий предмет
            ItemStack stack = event.getItemStack();

            // Проверяем все возможные условия кормления
            if (!stack.isEmpty() && animal.isFood(stack)) {
                // Отменяем только если это действительно еда
                event.setCanceled(true);
                event.setCancellationResult(net.minecraft.world.InteractionResult.FAIL);
            }
        }
    }
}
