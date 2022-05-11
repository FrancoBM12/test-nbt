package com.francobm.testplugin.listeners;

import com.francobm.testplugin.nbt.NBTTag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class Listener implements org.bukkit.event.Listener {

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        ItemStack itemStack = event.getItemInHand(); // obtiene el item que se esta colocando
        NBTTag nbtTag = NBTTag.get(itemStack); // obtiene el nbt del item
        if (nbtTag == null) return; // si no tiene nbt, no hace nada
        if(!nbtTag.hasKey("test")) return; // si no tiene la clave test, no hace nada
        event.setCancelled(true); // cancela el evento
    }
}
