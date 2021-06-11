package xyz.jpenilla.wanderingtrades.util;

import com.google.common.collect.ImmutableList;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.jpenilla.jmplib.HeadBuilder;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.jmplib.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Config;
import xyz.jpenilla.wanderingtrades.config.PlayerHeadConfig;

import static io.papermc.lib.PaperLib.isPaper;

public class StoredPlayers {

    private final WanderingTrades wanderingTrades;
    private final ConcurrentHashMap<UUID, MerchantRecipe> uuidMerchantRecipeMap = new ConcurrentHashMap<>();
    private final ProfileCompleter profileCompleter;

    public StoredPlayers(WanderingTrades wanderingTrades) {
        this.wanderingTrades = wanderingTrades;
        this.profileCompleter = isPaper() ? new ProfileCompleter() : null;
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
                .filter(offlinePlayer -> offlinePlayer.getName() != null && !offlinePlayer.getName().isEmpty() && !TextUtil.containsCaseInsensitive(offlinePlayer.getName(), wanderingTrades.config().playerHeadConfig().usernameBlacklist()))
                .filter(offlinePlayer -> {
                    final LocalDateTime logout = Instant.ofEpochMilli(offlinePlayer.getLastPlayed()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                    final LocalDateTime cutoff = LocalDateTime.now().minusDays(wanderingTrades.config().playerHeadConfig().days());
                    return logout.isAfter(cutoff) || wanderingTrades.config().playerHeadConfig().days() == -1;
                })
                .forEach(this::addOfflineHead);
    }

    private MerchantRecipe getHeadRecipe(UUID uuid, String name) {
        final Config cfg = wanderingTrades.config();
        final PlayerHeadConfig playerHeadConfig = cfg.playerHeadConfig();
        final ItemBuilder headBuilder = new HeadBuilder(uuid)
                .setLore(playerHeadConfig.lore())
                .setAmount(playerHeadConfig.headsPerTrade());
        if (playerHeadConfig.name() != null) {
            headBuilder.setName(playerHeadConfig.name().replace("{PLAYER}", name));
        }
        final ItemStack head = headBuilder.build();

        if (this.profileCompleter != null) {
            final SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                this.profileCompleter.submitSkullMeta(meta);
            }
        }

        final MerchantRecipe recipe = new MerchantRecipe(
                head,
                0,
                playerHeadConfig.maxUses(),
                playerHeadConfig.experienceReward()
        );
        recipe.addIngredient(playerHeadConfig.ingredientOne());
        if (playerHeadConfig.ingredientTwo() != null) {
            recipe.addIngredient(playerHeadConfig.ingredientTwo());
        }
        return recipe;
    }

    public List<MerchantRecipe> randomlySelectPlayerHeads() {
        final Collection<UUID> selectedPlayers = new ArrayList<>();
        final int amount = wanderingTrades.config().playerHeadConfig().getRandAmount();
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
            if (wanderingTrades.config().playerHeadConfig().permissionWhitelist()) {
                Bukkit.getScheduler().runTaskAsynchronously(wanderingTrades, () -> {
                    if (wanderingTrades.vaultHook().permissions().playerHas(null, offlinePlayer, Constants.Permissions.WANDERINGTRADES_HEADAVAILABLE)) {
                        Bukkit.getScheduler().runTask(wanderingTrades, () -> uuidMerchantRecipeMap.put(offlinePlayer.getUniqueId(), getHeadRecipe(offlinePlayer.getUniqueId(), offlinePlayer.getName())));
                    }
                });
                return;
            }
        }
        uuidMerchantRecipeMap.put(offlinePlayer.getUniqueId(), getHeadRecipe(offlinePlayer.getUniqueId(), offlinePlayer.getName()));
    }

    private void addHead(Player player) {
        if (!uuidMerchantRecipeMap.containsKey(player.getUniqueId()) && !TextUtil.containsCaseInsensitive(player.getName(), wanderingTrades.config().playerHeadConfig().usernameBlacklist())) {
            uuidMerchantRecipeMap.put(player.getUniqueId(), getHeadRecipe(player.getUniqueId(), player.getName()));
        }
    }

    public void addHeadIfPermissible(Player player) {
        if (wanderingTrades.isVaultPermissions()) {
            if (wanderingTrades.config().playerHeadConfig().permissionWhitelist()) {
                if (player.hasPermission(Constants.Permissions.WANDERINGTRADES_HEADAVAILABLE)) {
                    this.addHead(player);
                }
                return;
            }
        }
        this.addHead(player);
    }

    public void onLogout(Player player) {
        if (wanderingTrades.isVaultPermissions() && wanderingTrades.config().playerHeadConfig().permissionWhitelist()) {
            if (!player.hasPermission(Constants.Permissions.WANDERINGTRADES_HEADAVAILABLE)) {
                uuidMerchantRecipeMap.remove(player.getUniqueId());
            }
        }
    }

}
