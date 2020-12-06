package xyz.jpenilla.wanderingtrades.util;

import com.google.common.collect.ImmutableList;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class StoredPlayers {

    private final WanderingTrades wanderingTrades;
    private final ConcurrentHashMap<UUID, MerchantRecipe> uuidMerchantRecipeMap = new ConcurrentHashMap<>();
    private final ProfileCompleter profileCompleter;

    public StoredPlayers(WanderingTrades wanderingTrades) {
        this.wanderingTrades = wanderingTrades;
        this.profileCompleter = wanderingTrades.isPaperServer() ? new ProfileCompleter() : null;
        if (this.profileCompleter != null) {
            this.profileCompleter.runTaskTimerAsynchronously(wanderingTrades, 0L, 40L);
        }
    }

    @SuppressWarnings("deprecation")
    public void load() {
        this.uuidMerchantRecipeMap.clear();
        if (this.profileCompleter != null) {
            this.profileCompleter.clearQueue();
        }
        ImmutableList.copyOf(Bukkit.getOfflinePlayers()).stream()
                .filter(offlinePlayer -> offlinePlayer.getName() != null && !offlinePlayer.getName().isEmpty() && !TextUtil.containsCaseInsensitive(offlinePlayer.getName(), wanderingTrades.getCfg().getPlayerHeadConfig().getUsernameBlacklist()))
                .filter(offlinePlayer -> {
                    final LocalDateTime logout = Instant.ofEpochMilli(offlinePlayer.getLastPlayed()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                    final LocalDateTime cutoff = LocalDateTime.now().minusDays(wanderingTrades.getCfg().getPlayerHeadConfig().getDays());
                    return logout.isAfter(cutoff) || wanderingTrades.getCfg().getPlayerHeadConfig().getDays() == -1;
                })
                .forEach(this::addOfflineHead);
    }

    private MerchantRecipe getHeadRecipe(UUID uuid, String name) {
        final ItemStack head = new HeadBuilder(uuid)
                .setName(wanderingTrades.getCfg().getPlayerHeadConfig().getName().replace("{PLAYER}", name))
                .setLore(wanderingTrades.getCfg().getPlayerHeadConfig().getLore())
                .setAmount(wanderingTrades.getCfg().getPlayerHeadConfig().getHeadsPerTrade())
                .build();

        if (this.profileCompleter != null) {
            Optional.ofNullable(((SkullMeta) head.getItemMeta()).getPlayerProfile()).ifPresent(this.profileCompleter::submitProfile);
        }

        final MerchantRecipe recipe = new MerchantRecipe(
                head,
                0,
                wanderingTrades.getCfg().getPlayerHeadConfig().getMaxUses(),
                wanderingTrades.getCfg().getPlayerHeadConfig().isExperienceReward()
        );
        recipe.addIngredient(wanderingTrades.getCfg().getPlayerHeadConfig().getIngredient1());
        if (wanderingTrades.getCfg().getPlayerHeadConfig().getIngredient2() != null) {
            recipe.addIngredient(wanderingTrades.getCfg().getPlayerHeadConfig().getIngredient2());
        }
        return recipe;
    }

    public List<MerchantRecipe> randomlySelectPlayerHeads() {
        final Collection<UUID> selectedPlayers = new ArrayList<>();
        final int amount = wanderingTrades.getCfg().getPlayerHeadConfig().getRandAmount();
        final ArrayList<UUID> UUIDs = new ArrayList<>(uuidMerchantRecipeMap.keySet());
        if (UUIDs.size() < 2) {
            selectedPlayers.addAll(UUIDs);
        } else {
            for (int i = 0; selectedPlayers.size() < amount; i++) {
                final UUID uuid = UUIDs.get(ThreadLocalRandom.current().nextInt(0, UUIDs.size() - 1));
                if (selectedPlayers.contains(uuid)) {
                    continue;
                }
                selectedPlayers.add(uuid);
                if (i > 10 * amount) {
                    break;
                }
            }
        }
        return selectedPlayers.stream().map(uuidMerchantRecipeMap::get).collect(Collectors.toList());
    }

    private void addOfflineHead(OfflinePlayer offlinePlayer) {
        if (wanderingTrades.isVaultPermissions()) {
            if (wanderingTrades.getCfg().getPlayerHeadConfig().isPermissionWhitelist()) {
                Bukkit.getScheduler().runTaskAsynchronously(wanderingTrades, () -> {
                    if (wanderingTrades.getVault().getPerms().playerHas(null, offlinePlayer, "wanderingtrades.headavailable")) {
                        Bukkit.getScheduler().runTask(wanderingTrades, () -> uuidMerchantRecipeMap.put(offlinePlayer.getUniqueId(), getHeadRecipe(offlinePlayer.getUniqueId(), offlinePlayer.getName())));
                    }
                });
                return;
            }
        }
        uuidMerchantRecipeMap.put(offlinePlayer.getUniqueId(), getHeadRecipe(offlinePlayer.getUniqueId(), offlinePlayer.getName()));
    }

    private void addHead(Player player) {
        if (!uuidMerchantRecipeMap.containsKey(player.getUniqueId()) && !TextUtil.containsCaseInsensitive(player.getName(), wanderingTrades.getCfg().getPlayerHeadConfig().getUsernameBlacklist())) {
            uuidMerchantRecipeMap.put(player.getUniqueId(), getHeadRecipe(player.getUniqueId(), player.getName()));
        }
    }

    public void addHeadIfPermissible(Player player) {
        if (wanderingTrades.isVaultPermissions()) {
            if (wanderingTrades.getCfg().getPlayerHeadConfig().isPermissionWhitelist()) {
                if (player.hasPermission("wanderingtrades.headavailable")) {
                    this.addHead(player);
                }
                return;
            }
        }
        this.addHead(player);
    }

    public void onLogout(Player player) {
        if (wanderingTrades.isVaultPermissions() && wanderingTrades.getCfg().getPlayerHeadConfig().isPermissionWhitelist()) {
            if (!player.hasPermission("wanderingtrades.headavailable")) {
                uuidMerchantRecipeMap.remove(player.getUniqueId());
            }
        }
    }

}
