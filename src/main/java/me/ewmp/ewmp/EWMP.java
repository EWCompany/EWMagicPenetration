package me.ewmp.ewmp;

import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public final class EWMP extends JavaPlugin implements Listener, CommandExecutor {
    private Set<String> whitelistedPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig(); // Сохраняем конфигурацию по умолчанию, если её нет
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("penetration").setExecutor(this); // Регистрация команды
        getLogger().info(ChatColor.GREEN + "#####################################");
        getLogger().info(ChatColor.GREEN + "#                                   #");
        getLogger().info(ChatColor.GREEN + "# EWMagicPenetration plugin V1-Beta #");
        getLogger().info(ChatColor.GREEN + "#                                   #");
        getLogger().info(ChatColor.GREEN + "#              Enabling             #");
        getLogger().info(ChatColor.GREEN + "#####################################");

            // Загрузка белого списка из конфигурации
            if (getConfig().contains("whitelistedPlayers")) {
                whitelistedPlayers = new HashSet<>(getConfig().getStringList("whitelistedPlayers"));
            }
        }

        @Override
        public boolean onCommand (CommandSender sender, Command cmd, String label, String[]args){
            if (cmd.getName().equalsIgnoreCase("penetration")) {
                if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                    if (!sender.hasPermission("penetration.reload")) {
                        sender.sendMessage(ChatColor.RED + getConfig().getString("no_permision_message"));
                        return true;
                    }
                    // Перезагрузка плагина
                    reloadPlugin();
                    sender.sendMessage(ChatColor.GREEN + getConfig().getString("reload_message"));
                    return true;
                }

                if (args.length >= 2 && args[0].equalsIgnoreCase("add")) {
                    if (!sender.hasPermission("penetration.add")) {
                        sender.sendMessage(ChatColor.RED + getConfig().getString("no_permision_message"));
                        return true;
                    }
                    String playerNameToAdd = args[1];
                    Player playerToAdd = Bukkit.getPlayer(playerNameToAdd);
                    // Добавляем игрока в белый список независимо от его статуса
                    if (playerToAdd != null && playerToAdd.isOnline()) {
                        whitelistedPlayers.add(playerToAdd.getName());
                        sender.sendMessage(ChatColor.GREEN + "Player " + playerToAdd.getName() + getConfig().getString("added_player_message"));
                    } else {
                        whitelistedPlayers.add(playerNameToAdd);
                        sender.sendMessage(ChatColor.GREEN + "Player " + playerNameToAdd + getConfig().getString("added_player_message"));
                    }
                    // Сохранение списка в конфигурации
                    getConfig().set("whitelistedPlayers", new ArrayList<>(whitelistedPlayers));
                    saveConfig();
                    return true;
                } else if (args.length >= 2 && args[0].equalsIgnoreCase("delete")) {
                    if (!sender.hasPermission("penetration.delete")) {
                        sender.sendMessage(ChatColor.RED + getConfig().getString("no_permision_message"));
                        return true;
                    }
                    String playerNameToDelete = args[1];
                    if (whitelistedPlayers.contains(playerNameToDelete)) {
                        whitelistedPlayers.remove(playerNameToDelete);
                        sender.sendMessage(ChatColor.GREEN + "Player " + playerNameToDelete + getConfig().getString("delete_message"));
                        // Сохранение обновленного списка в конфигурации
                        getConfig().set("whitelistedPlayers", new ArrayList<>(whitelistedPlayers));
                        saveConfig();
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player" + playerNameToDelete + " не найден в белом списке.");
                    }
                    return true;
                } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                    if (whitelistedPlayers.isEmpty()) {
                        sender.sendMessage(ChatColor.YELLOW + getConfig().getString("empty_list_message"));
                    } else {
                        sender.sendMessage(ChatColor.GREEN + getConfig().getString("players_in_list") +  String.join(", ",  whitelistedPlayers));
                    }
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED +  "/penetration add,delete,list <player>");
                    return true;
                }
            }
            return false; // Возвращаем false, если команда не распознана
        }

        @EventHandler
        public void onPlayerLogin (PlayerLoginEvent event){
            String playerName = event.getPlayer().getName();
            if (!whitelistedPlayers.contains(playerName)) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + getConfig().getString("denied_message"));
            }
        }

        private void reloadPlugin () {
            onDisable(); // Отключаем плагин
            onEnable();  // Включаем плагин заново
        }

        @Override
        public void onDisable () {
            getLogger().info(ChatColor.GREEN + "#####################################");
            getLogger().info(ChatColor.GREEN + "#                                   #");
            getLogger().info(ChatColor.GREEN + "# EWMagicPenetration plugin V1-Beta #");
            getLogger().info(ChatColor.GREEN + "#                                   #");
            getLogger().info(ChatColor.GREEN + "#           Disabling               #");
            getLogger().info(ChatColor.GREEN + "#####################################");
        }
}