package net.citizensnpcs;

import java.util.List;

import net.citizensnpcs.api.event.NPCCreateEvent.NPCCreateReason;
import net.citizensnpcs.resources.npclib.HumanNPC;
import net.citizensnpcs.resources.npclib.NPCManager;
import net.citizensnpcs.utils.LocationUtils;
import net.citizensnpcs.utils.MessageUtils;
import net.citizensnpcs.utils.Messaging;
import net.citizensnpcs.utils.PathUtils;
import net.citizensnpcs.utils.StringUtils;
import net.citizensnpcs.waypoints.WaypointPath;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;

public class TickTask implements Runnable {
    @Override
    public void run() {
        Player[] online = Bukkit.getServer().getOnlinePlayers();
        if (toRemove.size() > 0) {
            cachedActions.values().removeAll(toRemove);
            toRemove.clear();
        }
        for (HumanNPC npc : NPCManager.getList().values()) {
            updateWaypoints(npc);
            npc.doTick();
            if (!npc.getNPCData().isLookClose() && !npc.getNPCData().isTalkClose())
                continue;
            boolean canLookClose = npc.getHandle().pathFinished() && !npc.getHandle().hasTarget()
                    && npc.getNPCData().isLookClose();
            if (!npc.getNPCData().isTalkClose() && !canLookClose)
                continue;
            Location npcLoc = npc.getLocation();
            for (Player player : online) {
                // If the player is within 'seeing' range
                String name = player.getName().toLowerCase();
                if (LocationUtils.withinRange(npcLoc, player.getLocation(), Settings.getDouble("NPCRange"))) {
                    if (canLookClose)
                        NPCManager.faceEntity(npc, player);

                    if (npc.getNPCData().isTalkClose() && !cachedActions.containsEntry(npc, name)) {
                        MessageUtils.sendText(npc, player);
                        cachedActions.put(npc, name);
                    }
                } else {
                    cachedActions.remove(npc, name);
                }
            }
        }
    }

    private void updateWaypoints(HumanNPC npc) {
        WaypointPath waypoints = npc.getWaypoints();
        switch (waypoints.size()) {
            case 0:
                break;
            case 1:
                // TODO: merge the default and this case.
                if (waypoints.currentIndex() >= 1) {
                    if (!waypoints.isStarted()) {
                        waypoints.schedule(npc, 0);
                    }
                    if (waypoints.isStarted() && !npc.isPaused() && npc.getHandle().pathFinished()) {
                        waypoints.setIndex(0);
                    }
                } else {
                    if (!npc.getWaypoints().isStarted()) {
                        PathUtils.createPath(npc, npc.getNPCData().getLocation(), -1, -1,
                                Settings.getDouble("PathfindingRange"));
                        waypoints.setStarted(true);
                    }
                    if (waypoints.isStarted() && !npc.isPaused() && npc.getHandle().pathFinished()) {
                        waypoints.setIndex(1);
                    }
                }
                if (waypoints.isStarted() && !npc.isPaused() && npc.getHandle().pathFinished()) {
                    waypoints.setStarted(false);
                    waypoints.onReach(npc);
                }
                break;
            default:
                if (!waypoints.isStarted()) {
                    waypoints.scheduleNext(npc);
                }
                if (waypoints.isStarted() && !npc.isPaused() && npc.getHandle().pathFinished()) {
                    waypoints.setIndex(waypoints.currentIndex() + 1);
                    waypoints.setStarted(false);
                    waypoints.onReach(npc);
                }
        }
    }

    public static class RespawnTask implements Runnable {
        private final String owner;
        private final int UID;

        public RespawnTask(HumanNPC npc) {
            this.UID = npc.getUID();
            this.owner = npc.getOwner();
        }

        public void register(int delay) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Citizens.plugin, this, delay);
        }

        @Override
        public void run() {
            NPCManager.register(UID, owner, NPCCreateReason.RESPAWN);
            Messaging.sendUncertain(owner, StringUtils.wrap(NPCManager.get(UID).getName()) + " has respawned.");
        }
    }

    private static final SetMultimap<HumanNPC, String> cachedActions = HashMultimap.create();
    private static final List<String> toRemove = Lists.newArrayList();

    public static void clearActions(Player player) {
        toRemove.add(player.getName().toLowerCase());
    }

    public static void scheduleRespawn(HumanNPC npc, int delay) {
        new RespawnTask(npc).register(delay);
    }
}