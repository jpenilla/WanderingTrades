package xyz.jpenilla.wanderingtrades.util;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class StoredPlayers {
    private final WanderingTrades wanderingTrades;
    private final Map<UUID, MerchantRecipe> players = new HashMap<>();

    public StoredPlayers(WanderingTrades wanderingTrades) {
        this.wanderingTrades = wanderingTrades;
    }

    @SuppressWarnings("deprecation")
    public void load() {
        players.clear();
        ImmutableList.copyOf(Bukkit.getOfflinePlayers()).stream()
                .filter(offlinePlayer -> offlinePlayer.getName() != null && !offlinePlayer.getName().equals("") && !TextUtil.containsCaseInsensitive(offlinePlayer.getName(), wanderingTrades.getCfg().getPlayerHeadConfig().getUsernameBlacklist()))
                .filter(offlinePlayer -> {
                    final LocalDateTime logout = Instant.ofEpochMilli(offlinePlayer.getLastPlayed()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                    final LocalDateTime cutoff = LocalDateTime.now().minusDays(wanderingTrades.getCfg().getPlayerHeadConfig().getDays());
                    return logout.isAfter(cutoff) || wanderingTrades.getCfg().getPlayerHeadConfig().getDays() == -1;
                })
                .forEach(offlinePlayer -> {
                    if (!wanderingTrades.isVaultPermissions()) {
                        players.put(offlinePlayer.getUniqueId(), getHeadRecipe(offlinePlayer.getUniqueId(), offlinePlayer.getName()));
                    } else {
                        if (wanderingTrades.getCfg().getPlayerHeadConfig().isPermissionWhitelist()) {
                            Bukkit.getScheduler().runTaskAsynchronously(wanderingTrades, () -> {
                                if (wanderingTrades.getVault().getPerms().playerHas(null, offlinePlayer, "wanderingtrades.headavailable")) {
                                    Bukkit.getScheduler().runTask(wanderingTrades, () -> players.put(offlinePlayer.getUniqueId(), getHeadRecipe(offlinePlayer.getUniqueId(), offlinePlayer.getName())));
                                }
                            });
                        } else {
                            players.put(offlinePlayer.getUniqueId(), getHeadRecipe(offlinePlayer.getUniqueId(), offlinePlayer.getName()));
                        }
                    }
                });
    }

    private MerchantRecipe getHeadRecipe(UUID uuid, String name) {
        ItemStack head = new HeadBuilder(uuid)
                .setName(wanderingTrades.getCfg().getPlayerHeadConfig().getName().replace("{PLAYER}", name))
                .setLore(wanderingTrades.getCfg().getPlayerHeadConfig().getLore())
                .setAmount(wanderingTrades.getCfg().getPlayerHeadConfig().getHeadsPerTrade()).build();

        if (wanderingTrades.isPaperServer()) {
            final SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
            if (skullMeta != null && skullMeta.getPlayerProfile() != null) {
                Bukkit.getScheduler().runTaskAsynchronously(wanderingTrades, () -> {
                    try {
                        skullMeta.getPlayerProfile().complete();
                    } catch (Exception e) {
                        wanderingTrades.getLog().debug(String.format("Failed to cache player head skin for player: [username=%s,uuid=%s]", name, uuid));
                    }
                });
            }
        }

        MerchantRecipe recipe = new MerchantRecipe(head, 0, wanderingTrades.getCfg().getPlayerHeadConfig().getMaxUses(), wanderingTrades.getCfg().getPlayerHeadConfig().isExperienceReward());
        recipe.addIngredient(wanderingTrades.getCfg().getPlayerHeadConfig().getIngredient1());
        if (wanderingTrades.getCfg().getPlayerHeadConfig().getIngredient2() != null) {
            recipe.addIngredient(wanderingTrades.getCfg().getPlayerHeadConfig().getIngredient2());
        }
        return recipe;
    }

    public List<MerchantRecipe> getPlayerHeadsFromServer() {
        final Collection<UUID> selectedPlayers = new LinkedHashSet<>();
        int count = 0;
        final int amount = wanderingTrades.getCfg().getPlayerHeadConfig().getRandAmount();
        UUID[] UUIDs = players.keySet().toArray(new UUID[0]);
        while (selectedPlayers.size() < amount) {
            UUID u = UUIDs[ThreadLocalRandom.current().nextInt(0, players.size())];
            if (!selectedPlayers.contains(u)) {
                selectedPlayers.add(u);
            }
            count++;
            if (count > 10 * amount) {
                break;
            }
        }
        return selectedPlayers.stream().map(players::get).collect(Collectors.toList());
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
