package dev.graygames.nospawn;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import java.util.Set;

/**
 * Cancels natural hostile-mob spawns inside GriefPrevention admin claims.
 * Spawners, eggs, breeding, commands and plugins are untouched.
 */
public final class NoSpawnInAdminClaims extends JavaPlugin implements Listener {

    /** Spawn reasons treated as "natural": the ones that happen without a player's deliberate action. */
    private static final Set<SpawnReason> NATURAL = EnumSet.of(
            SpawnReason.NATURAL, SpawnReason.JOCKEY, SpawnReason.MOUNT, SpawnReason.PATROL,
            SpawnReason.RAID, SpawnReason.REINFORCEMENTS, SpawnReason.VILLAGE_INVASION,
            SpawnReason.LIGHTNING, SpawnReason.SILVERFISH_BLOCK, SpawnReason.DROWNED,
            SpawnReason.ENDER_PEARL, SpawnReason.TRIAL_SPAWNER);

    private boolean blockPassive;
    private boolean blockPhantoms;
    private boolean blockSlimes;
    private boolean onlyAdminClaims;
    private boolean debug;
    private long cancelled;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        load();
        if (getServer().getPluginManager().getPlugin("GriefPrevention") == null) {
            getLogger().severe("GriefPrevention is not loaded; disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Blocking natural spawns in " + (onlyAdminClaims ? "admin claims" : "all claims")
                + " (passive=" + blockPassive + ", phantoms=" + blockPhantoms + ", slimes=" + blockSlimes + ")");
    }

    @Override
    public void onDisable() {
        if (cancelled > 0) {
            getLogger().info("Cancelled " + cancelled + " spawns this session.");
        }
    }

    private void load() {
        reloadConfig();
        blockPassive = getConfig().getBoolean("block-passive", false);
        blockPhantoms = getConfig().getBoolean("block-phantoms", true);
        blockSlimes = getConfig().getBoolean("block-slimes", true);
        onlyAdminClaims = getConfig().getBoolean("only-admin-claims", true);
        debug = getConfig().getBoolean("debug", false);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent event) {
        if (!NATURAL.contains(event.getSpawnReason())) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (!isTargeted(entity)) {
            return;
        }
        Location loc = event.getLocation();
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, true, null);
        if (claim == null) {
            return;
        }
        if (onlyAdminClaims && !claim.isAdminClaim()) {
            return;
        }
        event.setCancelled(true);
        cancelled++;
        if (debug) {
            getLogger().info("Cancelled " + entity.getType() + " (" + event.getSpawnReason() + ") at "
                    + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + " in claim " + claim.getID());
        }
    }

    private boolean isTargeted(Entity entity) {
        if (entity instanceof Phantom) {
            return blockPhantoms;
        }
        if (entity instanceof Slime) {
            return blockSlimes;
        }
        if (entity instanceof Monster) {
            return true;
        }
        return blockPassive;
    }
}
