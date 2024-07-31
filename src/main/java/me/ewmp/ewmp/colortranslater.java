package me.ewmp.ewmp;

import org.bukkit.ChatColor;

public class colortranslater {
    public static String getMsg(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
