package de.erethon.fxldiscord;

import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.player.FPlayer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.SimpleFormatter;

public class DiscordLink extends ListenerAdapter {

    FXLDC plugin = FXLDC.getPlugin();
    JDA jda = plugin.getJda();
    FactionsXL fxl = (FactionsXL) plugin.getServer().getPluginManager().getPlugin("FactionsXL");
    private final Map<String, Player> convos = new HashMap<>();

    public void startLinking(Player player, String username) {
        User user = null;
        for (User u : jda.getUsers()) {
            if (u.getAsTag().equals(username)) {
                user = u;
            }
        }
        if (user == null) {
            player.sendMessage(ChatColor.RED + "Dieser Discord-Account ist nicht auf dem DRE-Discord oder die Discord-API ist offline.");
            return;
        }
        CompletableFuture<PrivateChannel> channel = user.openPrivateChannel().submit();
        BukkitRunnable checker = new BukkitRunnable() {
            @Override
            public void run() {
                if (channel.isDone()) {
                    try {
                        sendMessage(channel.get(), player);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    cancel();
                }
            }
        };
        checker.runTaskTimer(plugin, 5,5);
    }

    public  void sendMessage(PrivateChannel channel, Player player) {
        Message message = new MessageBuilder("**DRE2 -> Discord Link**" +
                "\n\n_" + player.getName() + "_ möchte diesen Discord-Account mit seinem Minecraft-Account verbinden." +
                "\n\n**Wenn du diesen Vorgang gestartet hast, klicke auf das :white_check_mark: unten.**\nWenn nicht, ignoriere diese Nachricht.").build();
        channel.sendMessage(message).queue(m -> {
            m.addReaction("U+2705").queue();
        });
        convos.put((String) channel.getId(), player);

    }

    @SubscribeEvent
    public void onReact(PrivateMessageReactionAddEvent event) {
        BukkitRunnable sync = new BukkitRunnable() {
            @Override
            public void run() {
                if (event.getUser().isBot()) {
                    return;
                }
                PrivateChannel channel = event.getChannel();
                if (plugin.getLink().getConvos().get(channel.getId()) == null) {
                    return;
                }
                if (event.getReaction().getReactionEmote().getAsCodepoints().equals("U+2705")) {
                    accept(plugin.getLink().getConvos().get(channel.getId()), channel.getUser().getId());
                    channel.sendMessage("Die Verlinkung war erfolgreich!").queue();
                }
                plugin.getLink().getConvos().remove(channel.getId());
            }
        };
        sync.run();
    }

    public Map<String, Player> getConvos() {
        return convos;
    }

    public  void accept(Player player, String id) {
        LuckPerms lp = plugin.getLP();
        net.luckperms.api.model.user.User user = lp.getPlayerAdapter(Player.class).getUser(player);
        MetaNode node = MetaNode.builder()
                .key("discord-id")
                .value(id)
                .build();
        user.data().add(node);
        lp.getUserManager().saveUser(user);
        plugin.getLogger().info("MC-Account " + player.getName() + " und Discord-Account " + plugin.getJda().getUserById(id).getAsTag() + " wurden gelinkt.");
        player.sendMessage(ChatColor.GREEN + "Dein Account wurde erfolgreich mit dem Discord-Account " + plugin.getJda().getUserById(id).getAsTag() + " verbunden.");
    }

    public void updateFactions(Player player) {
        plugin.getLogger().info("Updating factions for " + player.getName());
        FPlayer fPlayer = fxl.getFPlayerCache().getByPlayer(player);
        if (fPlayer == null) {
            plugin.getLogger().info("No player");
            return;
        }
        Faction faction = fPlayer.getFaction();
        if (faction == null) {
            plugin.getLogger().info("No Faction");
            return;
        }
        List<Role> roles = jda.getRolesByName(faction.getName(), false);
        String userid = plugin.getLP().getPlayerAdapter(Player.class).getUser(player).getCachedData().getMetaData().getMeta().get("discord-id").get(0);
        if (userid == null) {
            plugin.getLogger().info("No userid");
            return;
        }
        User user = jda.getUserById(userid);
        if (user == null) {
            plugin.getLogger().info("No user");
            return;
        }
        Guild server =  jda.getGuildById("447363254092693516");
        Member member = server.getMember(user);
        for (Role role : member.getRoles()) {
            if (fxl.getFactionCache().getByName(role.getName()) != null) {
                server.removeRoleFromMember(member, role);
            }
        }
        server.addRoleToMember(member, roles.get(0));
        plugin.getLogger().info(member.getEffectiveName() + " wurde der Fraktionsgruppe " + roles.get(0).getName() + " zugewiesen.");

    }


}
