package xyz.jpenilla.wanderingtrades.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jspecify.annotations.NullMarked;

@NullMarked
@SuppressWarnings("unused")
public class HeadBuilder extends ItemBuilder<SkullMeta> {
    private HeadBuilder(final ItemStack stack, final SkullMeta meta) {
        super(stack, meta);
    }

    public HeadBuilder(final String base64) {
        super(ItemBuilder.<SkullMeta>create(Material.PLAYER_HEAD)
            .editMeta(meta -> meta.setPlayerProfile(skinProfile(base64))).build());
    }

    public static PlayerProfile skinProfile(final String base64) {
        final UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());
        final PlayerProfile profile = Bukkit.createProfile(hashAsId, "");
        profile.setProperty(new ProfileProperty("textures", base64));
        return profile;
    }

    public HeadBuilder(final OfflinePlayer offlinePlayer) {
        super(ItemBuilder.<SkullMeta>create(Material.PLAYER_HEAD)
            .editMeta(meta -> meta.setPlayerProfile(offlinePlayer.getPlayerProfile())).build());
    }

    public HeadBuilder(final UUID uuid) {
        super(ItemBuilder.<SkullMeta>create(Material.PLAYER_HEAD)
            .editMeta(meta -> meta.setPlayerProfile(Bukkit.createProfile(uuid))).build());
    }

    public HeadBuilder playerProfile(final PlayerProfile profile) {
        return (HeadBuilder) this.editMeta(meta -> meta.setPlayerProfile(profile));
    }

    public HeadBuilder skinBase64(final String base64) {
        return (HeadBuilder) this.editMeta(meta -> meta.setPlayerProfile(skinProfile(base64)));
    }

    @Override
    protected HeadBuilder create(final ItemStack stack, final SkullMeta meta) {
        return new HeadBuilder(stack, meta);
    }
}
