package teammt.mtpolls.containers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import masecla.mlib.classes.Replaceable;
import masecla.mlib.containers.instances.SquaredPagedContainer;
import masecla.mlib.main.MLib;
import masecla.mlib.nbt.TagBuilder;
import teammt.mtpolls.polls.Poll;
import teammt.mtpolls.polls.PollManager;
import teammt.mtpolls.polls.PollOption;

public class ClosedPollsContainer extends SquaredPagedContainer {

    private PollManager pollManager;
    private ViewableClosedPollContainer viewableClosedPollContainer;

    public ClosedPollsContainer(MLib lib, PollManager pollManager,
            ViewableClosedPollContainer viewableClosedPollContainer) {
        super(lib);
        this.pollManager = pollManager;
        this.viewableClosedPollContainer = viewableClosedPollContainer;
    }

    @Override
    public void usableClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
            return;

        Player player = (Player) event.getWhoClicked();
        String tag = lib.getNmsAPI().read(event.getCurrentItem()).getString("poll").getValue();
        if (tag == null)
            return;
        Poll poll = pollManager.getPoll(tag);
        this.viewableClosedPollContainer.setViewing(player.getUniqueId(), poll);
        lib.getContainerAPI().openFor(player, ViewableClosedPollContainer.class);
    }

    @Override
    public List<ItemStack> getOrderableItems(Player player) {
        List<ItemStack> result = new ArrayList<ItemStack>();
        for (Poll poll : pollManager.getPolls(false))
            result.add(buildItemForPoll(player, poll));
        return result;
    }

    private ItemStack buildItemForPoll(Player player, Poll poll) {
        DateFormat datetime = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss zzz");
        datetime.setTimeZone(TimeZone.getTimeZone(lib.getConfigurationAPI().getConfig().getString("timezone")));
        ItemStack result = poll.getSkull();
        ItemMeta itemMeta = result.getItemMeta();
        Replaceable time = new Replaceable("%time%",
                lib.getTimesAPI().generateTime(
                        poll.getCreatedAt() + poll.getDuration() - Instant.now().getEpochSecond()));
        Replaceable date = new Replaceable("%date%", datetime.format(poll.getCreatedAt() * 1000));
        Replaceable playerName = new Replaceable("%player-name%",
                Bukkit.getOfflinePlayer(poll.getCreatedBy()).getName());
        Replaceable votes = new Replaceable("%votes%", poll.getVotes());
        itemMeta.setDisplayName(lib.getMessagesAPI().getPluginMessage("poll-item-name",
                new Replaceable("%poll-name%", poll.getName())));
        itemMeta.setLore(lib.getMessagesAPI().getPluginListMessage("poll-item-closed-no-vote-lore", time, date,
                playerName, votes));
        for (PollOption cr : pollManager.getPollOptions(poll)) {
            if (cr.getPlayersVoted().contains(player.getUniqueId()))
                itemMeta.setLore(lib.getMessagesAPI().getPluginListMessage("poll-item-closed-vote-lore",
                        new Replaceable("%option%", cr.getOption()), time, date, playerName, votes));
        }
        result.setItemMeta(itemMeta);
        TagBuilder tag = lib.getNmsAPI().write();
        tag.tagString("poll", poll.getPollId().toString());
        result = tag.applyOn(result);

        return result;
    }

    @Override
    public int getSize(Player player) {
        return 45;
    }

    @Override
    public int getUpdatingInterval() {
        return 10;
    }

    @Override
    public String getTitle(Player player) {
        return lib.getMessagesAPI().getPluginMessage("closed-polls-container-title", player);
    }

}
