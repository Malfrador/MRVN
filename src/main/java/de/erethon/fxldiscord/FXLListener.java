package de.erethon.fxldiscord;

import de.erethon.factionsxl.event.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FXLListener implements Listener {

    FXLDC plugin = FXLDC.getPlugin();
    JDA jda = plugin.getJda();
    String id = plugin.getConfig().getString("announcementChannelID");


    @EventHandler
    public void truceEvent(WarTruceEndEvent event) {
        MessageChannel channel = jda.getTextChannelById(id);
        String msg = "**[Krieg]** Der Waffenstillstand zwischen " + event.getAttacker().getName() + " und " + event.getDefender().getName() + " ist nun zu Ende.";
        boolean hasRole = false;
        String id = "";
        for (Role r : jda.getRoles()) {
            if (r.getName().equals(event.getDefender().getName())) {
                hasRole = true;
                id = r.getId();
                break;
            }
        }
        if (hasRole) {
            msg = msg + " <@&" + id + ">";
        }
        channel.sendMessage(msg).queue();
    }

    @EventHandler
    public void warDeclarationEvent(WarDeclarationEvent event) {
        MessageChannel channel = jda.getTextChannelById(id);
        String msg = "**[Krieg]** " + event.getAttacker().getName() + " hat " + event.getDefender().getName() + " einen " + event.getCasusBelli().getType() + "-Krieg erklärt!";
        boolean hasRole = false;
        String id = "";
        for (Role r : jda.getRoles()) {
            if (r.getName().equals(event.getDefender().getName())) {
                hasRole = true;
                id = r.getId();
                break;
            }
        }
        if (hasRole) {
            msg = msg + " <@&" + id + ">";
        }
        channel.sendMessage(msg).queue();
    }

    @EventHandler
    public void warEndEvent(WarEndEvent event) {
        MessageChannel channel = jda.getTextChannelById(id);
        String msg = "**[Krieg]** Der Krieg zwischen " + event.getAttacker().getName() + " und " + event.getDefender().getName() + " ist nun vorbei.";
        channel.sendMessage(msg).queue();
    }

    @EventHandler
    public void regionAttackEvent(WarRegionAttackEvent event) { // WarRegionAttackEvent
        MessageChannel channel = jda.getTextChannelById(id);
        String msg = "**[Regionen]** Die Region _" + event.getRegion().getName() + "_ (" + event.getRegion().getOwner().getName() + ") wird bald von " + event.getAttacker().getName() + " angegriffen!";
        boolean hasRole = false;
        String id = "";
        for (Role r : jda.getRoles()) {
            if (r.getName().equals(event.getRegion().getOwner().getName())) {
                hasRole = true;
                id = r.getId();
                break;
            }
        }
        if (hasRole) {
            msg = msg + " <@&" + id + ">";
        }
        channel.sendMessage(msg).queue();
    }

    @EventHandler
    public void regionAttackEndEvent(WarRegionOccupiedEvent event) {
        MessageChannel channel = jda.getTextChannelById(id);
        String msg = "**[Regionen]** Die Region _" + event.getRegion().getName() + "_ wurde von " + event.getOccupant().getName() + " besetzt.";
        channel.sendMessage(msg).queue();
    }



}
