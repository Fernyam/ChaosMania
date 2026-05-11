//package net.fernyam.chaosmania.event;
//import net.fernyam.chaosmania.ChaosManiaMod;
//import net.fernyam.chaosmania.ConfigMod;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.item.crafting.RecipeHolder;
//import net.minecraft.world.item.crafting.RecipeManager;
//import net.neoforged.neoforge.event.AddReloadListenerEvent;
//import net.neoforged.neoforge.event.entity.player.PlayerEvent;
//import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//public class ItemCraftingEvent
//{
//    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
//        // Получаем RecipeManager
//        RecipeManager recipeManager = event.getServer().getRecipeManager();
//
//        // Собираем все рецепты
//        List<RecipeHolder<?>> allRecipes = new ArrayList<>(recipeManager.getRecipes());
//
//        // Удаляем ненужные рецепты
//        List<RecipeHolder<?>> filteredRecipes = allRecipes.stream()
//                .filter(recipe -> !isRecipeForbidden(recipe))
//                .toList();
//
//        // Применяем отфильтрованные рецепты
//        recipeManager.replaceRecipes(filteredRecipes);
//
//        ChaosManiaMod.LOGGER.info("Удалено {} запрещённых рецептов",
//                allRecipes.size() - filteredRecipes.size());
//    }
//
//    private static boolean isRecipeForbidden(RecipeHolder<?> recipe) {
//        ResourceLocation recipeId = recipe.id();
//
//        // Проверяем по списку из конфига
//        List<? extends String> forbidden = ConfigMod.FORBIDDEN_RECIPES.get();
//        return forbidden.contains(recipeId.toString());
//    }
//}
