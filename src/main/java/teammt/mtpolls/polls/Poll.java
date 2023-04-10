package teammt.mtpolls.polls;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import masecla.mlib.main.MLib;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Poll {
    private UUID pollId;
    private UUID createdBy;
    private ItemStack skull;
    private String name;
    private String question;
    private int duration;
    private List<UUID> options = new ArrayList<>();
    private long createdAt;
    private boolean open;
    private int votes;

    public Poll(UUID createdBy, MLib lib) {
        this.pollId = UUID.randomUUID();
        this.createdBy = createdBy;
        this.open = true;
        this.skull = getSkull(createdBy, lib);
    }

    @SuppressWarnings("deprecation")
    private ItemStack getSkull(UUID uuid, MLib lib) {
        Player player = Bukkit.getPlayer(uuid);
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        if (lib.getCompatibilityApi().getServerVersion().getMajor() >= 13)
            skullMeta.setOwningPlayer(player);
        else
            skullMeta.setOwner(player.getName());
        playerHead.setItemMeta(skullMeta);
        return playerHead;

    }

}
