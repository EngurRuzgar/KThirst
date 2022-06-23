package io.github.kpgtb.kkthirst.nms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class InventoryHelper_1_13 implements IInventoryHelper{
    @Override
    public void updateInventoryTitle(Player player, String title) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, InstantiationException {
        // 1.13
        //PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(ep.activeContainer.windowId, "minecraft:chest",new ChatMessage("Ciekaweee"), player.getOpenInventory().getTopInventory().getSize());
        //ep.playerConnection.sendPacket(packet);
        //ep.updateInventory(ep.activeContainer);

        Object entityPlayer = getCraftBukkitClass("entity.CraftPlayer")
                .getMethod("getHandle")
                .invoke(player);

        Object activeContainer = entityPlayer.getClass()
                .getField("activeContainer")
                .get(entityPlayer);
        Object windowId = activeContainer.getClass()
                .getField("windowId")
                .get(activeContainer);
        Object chatMessage = getNMSClass("ChatMessage")
                .getDeclaredConstructor(String.class, Object[].class)
                .newInstance(title, new Object[0]);

        Object packet = getNMSClass("PacketPlayOutOpenWindow")
                .getDeclaredConstructor(int.class, String.class, getNMSClass("IChatBaseComponent"), int.class)
                .newInstance(windowId, "minecraft:chest", chatMessage, player.getOpenInventory().getTopInventory().getSize());
        sendPacket(entityPlayer, packet);

        entityPlayer.getClass()
                .getMethod("updateInventory", getNMSClass("Container"))
                .invoke(entityPlayer, activeContainer);

    }

    private void sendPacket(Object entityPlayer, Object packet) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        Object playerConnection = entityPlayer.getClass()
                .getField("playerConnection")
                .get(entityPlayer);
        playerConnection.getClass()
                .getMethod("sendPacket", getNMSClass("Packet"))
                .invoke(playerConnection, packet);
    }


    private String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    private Class<?> getCraftBukkitClass(String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit."+getVersion()+"."+name);
    }

    private Class<?> getNMSClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server."+getVersion()+"."+name);
    }
}