package de.erethon.fxldiscord;

import de.erethon.factionsxl.event.FPlayerFactionLeaveEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

public final class FXLDC extends JavaPlugin implements Listener {

    static FXLDC plugin;
    JDA jda;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        String token = getConfig().getString("token");
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setActivity(Activity.playing("DRE II"));
        builder.setEventManager(new AnnotatedEventManager());
        builder.addEventListeners(new JDAListener());

        try {
            jda = builder.build();
        } catch (LoginException loginException) {
            loginException.printStackTrace();
        }
        getServer().getPluginManager().registerEvents(new FXLListener(), this);


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static FXLDC getPlugin() {
        return plugin;
    }

    public JDA getJda() {
        return jda;
    }
}
