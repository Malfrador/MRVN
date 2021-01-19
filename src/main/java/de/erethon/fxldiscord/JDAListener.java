package de.erethon.fxldiscord;

import de.erethon.factionsxl.FactionsXL;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class JDAListener extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannel().getId().equals("480397566580621332")) {
            if (event.getMessage().getContentDisplay().contains("!fxlstatus")) {
                event.getChannel().sendMessage("Server: " + FactionsXL.getInstance().getServer().getVersion());
            }
        }
    }
}
