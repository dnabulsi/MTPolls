package teammt.mtpolls.containers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import masecla.mlib.classes.builders.InventoryBuilder;
import masecla.mlib.classes.builders.ItemBuilder;
import masecla.mlib.containers.generic.ImmutableContainer;
import masecla.mlib.main.MLib;
import teammt.mtpolls.polls.Poll;
import teammt.mtpolls.polls.PollManager;
import teammt.mtpolls.polls.PollOption;

public class ViewableClosedPollContainer extends ImmutableContainer {

    private PollManager pollManager;
    private Map<UUID, Poll> currentlyViewing = new HashMap<>();

    public ViewableClosedPollContainer(MLib lib, PollManager pollManager) {
        super(lib);
        this.pollManager = pollManager;
    }

    @Override
    public void onTopClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getSlot() == 40) {
            event.getWhoClicked().closeInventory();
            return;
        }
    }

    @Override
    public Inventory getInventory(Player player) {
        List<String> redSkulls = new ArrayList<>(pollManager.getSkulls("red-skulls"));
        List<String> greenSkulls = new ArrayList<>(pollManager.getSkulls("green-skulls"));

        Map<Integer, int[]> locationsMap = new HashMap<>();
        locationsMap.put(2, new int[] { 21, 23 });
        locationsMap.put(3, new int[] { 29, 31, 33 });
        locationsMap.put(4, new int[] { 28, 30, 32, 34 });
        locationsMap.put(5, new int[] { 20, 22, 24, 30, 32 });
        locationsMap.put(6, new int[] { 19, 21, 23, 25, 30, 32 });

        Poll poll = currentlyViewing.get(player.getUniqueId());

        InventoryBuilder myInventory = new InventoryBuilder().title("'" + poll.getName() + "' Poll")
                .size(getSize(player))
                .border(getConfirmationDialogueBorder())
                .setItem(0, getTimeItem(poll))
                .setItem(13, getConfirmationDialogueQuestion(poll.getQuestion()))
                .setItem(40, getConfirmationDialogueClose());
        int i = 0;
        int max = pollManager.getMaxVotes(poll);
        for (PollOption option : pollManager.getPollOptions(poll)) {

            ItemBuilder optionItem = new ItemBuilder()
                    .tagString("MTPolls_option", "option" + i)
                    .replaceable("%option%", option.getOption())
                    .replaceable("%letter%", String.valueOf((char) (i + 65)))
                    .replaceable("%votes%", option.getVotes());

            if (option.getVotes() == max)
                optionItem.skull(greenSkulls.get(i));
            else
                optionItem.skull(redSkulls.get(i));

            if (option.getPlayersVoted().size() == 0)
                optionItem.mnl("viewable-poll-container-option");
            else if (option.getPlayersVoted().contains(player.getUniqueId()))
                optionItem.mnl("viewable-poll-container-option-voted");
            else
                optionItem.mnl("viewable-poll-container-option");

            myInventory.setItem(locationsMap.get(poll.getOptions().size())[i],
                    optionItem.build(lib));
            i++;
        }
        return myInventory.build(lib, player);

    }

    private ItemStack getConfirmationDialogueQuestion(String question) {
        return new ItemBuilder().skull(lib.getConfigurationAPI().getConfig("config").get("question-skull").toString())
                .mnl("viewable-poll-container-question").replaceable("%question%", question).build(lib);
    }

    private ItemStack getConfirmationDialogueBorder() {
        Material pane = null;
        if (lib.getCompatibilityApi().getServerVersion().getMajor() <= 12)
            pane = Material.matchMaterial("STAINED_GLASS_PANE");
        else
            pane = Material.matchMaterial("BLACK_STAINED_GLASS_PANE");
        ItemBuilder paneItem = new ItemBuilder(pane);
        if (lib.getCompatibilityApi().getServerVersion().getMajor() <= 12)
            paneItem = paneItem.data((byte) 15);
        return paneItem.build(lib);
    }

    private ItemStack getConfirmationDialogueClose() {
        return new ItemBuilder(Material.BARRIER)
                .mnl("viewable-poll-container-close")
                .build(lib);
    }

    private ItemStack getTimeItem(Poll poll) {
        DateFormat datetime = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss zzz");
        datetime.setTimeZone(TimeZone.getTimeZone(lib.getConfigurationAPI().getConfig().getString("timezone")));
        return new ItemBuilder(Material.CLOCK)
                .mnl("viewable-closed-poll-container-time")
                .replaceable("%votes%", poll.getVotes())
                .replaceable("%date%", datetime.format(poll.getCreatedAt() * 1000))
                .replaceable("%player-name%", Bukkit.getOfflinePlayer(poll.getCreatedBy()).getName())
                .build(lib);
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
    public boolean requiresUpdating() {
        return true;
    }

    public void setViewing(UUID uuid, Poll poll) {
        currentlyViewing.put(uuid, poll);
    }

}