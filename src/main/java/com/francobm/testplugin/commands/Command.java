package com.francobm.testplugin.commands;

import com.francobm.testplugin.nbt.NBTTag;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(args.length == 0) {
            return true;
        }
        // /test set <key> <value>
        Player player = (Player) sender;
        if(player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            player.sendMessage("You must be holding an item!");
            return true;
        }
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if(args[0].equalsIgnoreCase("set")) {
            if(args.length == 3) {
                NBTTag tag = NBTTag.get(itemInHand);
                if(tag == null) {
                    tag = new NBTTag();
                }
                tag.setString(args[1], args[2]);
                player.getInventory().setItemInMainHand(tag.apply(itemInHand));
            }
        }
        // /test get <key>
        if(args[0].equalsIgnoreCase("get")) {
            if(args.length == 2) {
                NBTTag tag = NBTTag.get(itemInHand);
                if(tag == null) {
                    player.sendMessage("Item has no NBT data!");
                    return true;
                }
                player.sendMessage(tag.getString(args[1]));
            }
        }
        return true;
    }
}
