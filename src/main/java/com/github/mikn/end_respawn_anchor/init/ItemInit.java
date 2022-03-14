package com.github.mikn.end_respawn_anchor.init;

import com.github.mikn.end_respawn_anchor.EndRespawnAnchor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EndRespawnAnchor.MODID);
    public static final RegistryObject<Item> END_RESPAWN_ANCHOR = ITEMS.register("end_respawn_anchor", () -> new BlockItem(BlockInit.END_RESPAWN_ANCHOR.get(), new Item.Properties()));
}
