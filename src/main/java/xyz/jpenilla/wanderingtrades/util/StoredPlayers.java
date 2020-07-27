package xyz.jpenilla.wanderingtrades.util;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.jpenilla.jmplib.HeadBuilder;
import xyz.jpenilla.jmplib.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class StoredPlayers {
    private final WanderingTrades wanderingTrades;
    @Getter private final HashMap<UUID, MerchantRecipe> players = new HashMap<>();

    public StoredPlayers(WanderingTrades wanderingTrades) {
        this.wanderingTrades = wanderingTrades;
    }

    public void load() {
        players.clear();
        OfflinePlayer[] op = Bukkit.getOfflinePlayers().clone();

        for (OfflinePlayer offlinePlayer : op) {
            long lastLogout = offlinePlayer.getLastPlayed();
            LocalDateTime logout = Instant.ofEpochMilli(lastLogout)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            LocalDateTime cutoff = LocalDateTime.now().minusDays(wanderingTrades.getCfg().getPlayerHeadConfig().getDays());
            if (logout.isAfter(cutoff) || wanderingTrades.getCfg().getPlayerHeadConfig().getDays() == -1) {
                if (!TextUtil.containsCaseInsensitive(offlinePlayer.getName(), wanderingTrades.getCfg().getPlayerHeadConfig().getUsernameBlacklist())) {
                    if (!wanderingTrades.isVaultPermissions()) {
                        players.put(offlinePlayer.getUniqueId(), getHeadRecipe(offlinePlayer.getUniqueId(), offlinePlayer.getName()));
                    } else {
                        if (wanderingTrades.getCfg().getPlayerHeadConfig().isPermissionWhitelist()) {
                            Bukkit.getScheduler().runTaskAsynchronously(wanderingTrades, () -> {
                                if (wanderingTrades.getVault().getPerms().playerHas(null, offlinePlayer, "wanderingtrades.headavailable")) {
                                    players.put(offlinePlayer.getUniqueId(), getHeadRecipe(offlinePlayer.getUniqueId(), offlinePlayer.getName()));
                                }
                            });
                        } else {
                            players.put(offlinePlayer.getUniqueId(), getHeadRecipe(offlinePlayer.getUniqueId(), offlinePlayer.getName()));
                        }
                    }
                }
            }
        }
    }

    private MerchantRecipe getHeadRecipe(UUID uuid, String name) {
        ItemStack head = new HeadBuilder(uuid)
                .setName(wanderingTrades.getCfg().getPlayerHeadConfig().getName().replace("{PLAYER}", name))
                .setLore(wanderingTrades.getCfg().getPlayerHeadConfig().getLore())
                .setAmount(wanderingTrades.getCfg().getPlayerHeadConfig().getHeadsPerTrade()).build();

        if (wanderingTrades.isPaperServer()) {
            final SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
            if (skullMeta != null && skullMeta.getPlayerProfile() != null) {
                Bukkit.getScheduler().runTaskAsynchronously(wanderingTrades, () -> skullMeta.getPlayerProfile().complete());
            }
        }

        MerchantRecipe recipe = new MerchantRecipe(head, 0, wanderingTrades.getCfg().getPlayerHeadConfig().getMaxUses(), wanderingTrades.getCfg().getPlayerHeadConfig().isExperienceReward());
        recipe.addIngredient(wanderingTrades.getCfg().getPlayerHeadConfig().getIngredient1());
        if (wanderingTrades.getCfg().getPlayerHeadConfig().getIngredient2() != null) {
            recipe.addIngredient(wanderingTrades.getCfg().getPlayerHeadConfig().getIngredient2());
        }
        return recipe;
    }

    public ArrayList<MerchantRecipe> getPlayerHeadsFromServer() {
        ArrayList<UUID> selectedPlayers = new ArrayList<>();
        int count = 0;
        UUID[] UUIDs = players.keySet().toArray(new UUID[0]);
        while (selectedPlayers.size() < wanderingTrades.getCfg().getPlayerHeadConfig().getPlayerHeadsFromServerAmount()) {
            UUID u = UUIDs[ThreadLocalRandom.current().nextInt(0, players.size())];
            if (!selectedPlayers.contains(u)) {
                selectedPlayers.add(u);
            }
            count++;
            if (count > 10 * wanderingTrades.getCfg().getPlayerHeadConfig().getPlayerHeadsFromServerAmount()) {
                break;
            }
        }

        ArrayList<MerchantRecipe> newTrades = new ArrayList<>();
        for (UUID player : selectedPlayers) {
            newTrades.add(players.get(player));
        }
        return newTrades;
    }

    private void addHead(Player player) {
        if (!players.containsKey(player.getUniqueId()) && !TextUtil.containsCaseInsensitive(player.getName(), wanderingTrades.getCfg().getPlayerHeadConfig().getUsernameBlacklist())) {
            players.put(player.getUniqueId(), getHeadRecipe(player.getUniqueId(), player.getName()));
        }
    }

    public void tryAddHead(Player player) {
        if (wanderingTrades.isVaultPermissions()) {
            if (wanderingTrades.getCfg().getPlayerHeadConfig().isPermissionWhitelist()) {
                if (player.hasPermission("wanderingtrades.headavailable")) {
                    addHead(player);
                }
                return;
            }
        }
        addHead(player);
    }

    public void onLogout(Player player) {
        if (wanderingTrades.isVaultPermissions() && wanderingTrades.getCfg().getPlayerHeadConfig().isPermissionWhitelist()) {
            if (!player.hasPermission("wanderingtrades.headavailable")) {
                players.remove(player.getUniqueId());
            }
        }
    }
}
