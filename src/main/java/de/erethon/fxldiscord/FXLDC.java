package de.erethon.fxldiscord;

import de.erethon.factionsxl.event.FPlayerFactionLeaveEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

public final class FXLDC extends JavaPlugin implements Listener {

    static FXLDC plugin;
    JDA jda;
    DiscordLink link;
    LuckPerms luckPerms;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        String token = getConfig().getString("token");
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setActivity(Activity.playing("DRE II"));
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.setEventManager(new AnnotatedEventManager());
        builder.addEventListeners(new JDAListener());
        builder.addEventListeners(new DiscordLink());

        try {
            jda = builder.build();
        } catch (LoginException loginException) {
            loginException.printStackTrace();
        }
        getServer().getPluginManager().registerEvents(new FXLListener(), this);
        this.getCommand("discord").setExecutor(new DiscordCommand());
        link = new DiscordLink();
        luckPerms = LuckPermsProvider.get();


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

    public DiscordLink getLink() {
        return link;
    }

    public LuckPerms getLP() {
        return luckPerms;
    }
}
