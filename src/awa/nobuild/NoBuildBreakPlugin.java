package awa.nobuild;

import arc.Events;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType.BuildSelectEvent;
import mindustry.gen.Player;
import mindustry.mod.Plugin;
import mindustry.net.Administration.ActionType;
import java.util.HashMap;
import java.util.Map;

public class NoBuildBreakPlugin extends Plugin{
    private static final long NOTICE_COOLDOWN_MS = 1500L;
    private static final String DENY_MESSAGE = "[scarlet]本服务器已禁用建造和拆除。";

    private final Map<String, Long> lastNoticeTimes = new HashMap<>();

    @Override
    public void init(){
        Vars.netServer.admins.addActionFilter(action -> {
            if(action.type == ActionType.placeBlock || action.type == ActionType.breakBlock){
                notifyPlayer(action.player);
                if(action.player != null && action.player.unit() != null){
                    action.player.unit().clearBuilding();
                }
                return false;
            }
            return true;
        });

        Events.on(BuildSelectEvent.class, event -> {
            if(event.builder != null && event.builder.isPlayer()){
                event.builder.clearBuilding();
                notifyPlayer(event.builder.getPlayer());
            }
        });
    }

    private void notifyPlayer(Player player){
        if(player == null){
            return;
        }

        long now = Time.millis();
        long previous = lastNoticeTimes.getOrDefault(player.uuid(), 0L);
        if(now - previous < NOTICE_COOLDOWN_MS){
            return;
        }

        lastNoticeTimes.put(player.uuid(), now);
        player.sendMessage(DENY_MESSAGE);
    }
}
