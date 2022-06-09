package io.github.kpgtb.kkthirst.manager;

import io.github.kpgtb.kkthirst.KKthirst;
import io.github.kpgtb.kkthirst.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class UserManager {

    private final KKthirst plugin;
    private final HashMap<UUID, User> users;

    public UserManager(KKthirst plugin) {
        this.plugin = plugin;
        users = new HashMap<>();

        final double thirstPerMinute = plugin.getConfig().getDouble("minusThirstPerMinute");
        final double hpPerSecond = plugin.getConfig().getDouble("hpPerSecond");

        BukkitTask thirst = new BukkitRunnable() {
            @Override
            public void run() {
                for(User user : users.values()) {
                    if(!Bukkit.getOfflinePlayer(user.getUuid()).isOnline()) {
                        user.save();
                        users.remove(user.getUuid());
                        continue;
                    }

                    double userThirst = user.getThirst();
                    if(userThirst - thirstPerMinute <=0) {
                        damagePlayer(user, hpPerSecond);
                        continue;
                    }
                    user.setThirst(userThirst - thirstPerMinute);
                }
            }
        }.runTaskTimer(plugin, 15 * 20, 15 * 20);

        BukkitTask autoSaver = new BukkitRunnable() {
            @Override
            public void run() {
                for(User user : users.values()) {
                    user.save();
                }
            }
        }.runTaskTimer(plugin, 5 * 60 * 20, 5 * 60 * 20);
    }

    private void damagePlayer(User user, double hpPerSecond) {
        BukkitTask damageTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(user.getThirst() > 0) {
                    cancel();
                }

                if(!Bukkit.getOfflinePlayer(user.getUuid()).isOnline()) {
                    cancel();
                }

                Player player = Bukkit.getPlayer(user.getUuid());
                double playerHP = player.getHealth();

                if(playerHP - hpPerSecond <= 0) {
                    player.setHealth(0.0);
                    user.setThirst(plugin.getConfig().getDouble("maxThirst"));
                    user.save();
                    cancel();
                }

                player.setHealth(playerHP - hpPerSecond);
            }
        }.runTaskTimer(plugin, 20,20);
    }

    public void addUser(UUID uuid, User user) {
        users.put(uuid, user);
    }

    public void removeUser(UUID uuid) {
        users.remove(uuid);
    }

    public User getUser(UUID uuid) {
        if(!users.containsKey(uuid)) {
            return null;
        }
        return users.get(uuid);
    }

    public Collection<User> getUsers() {
        return users.values();
    }

}