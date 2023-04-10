package teammt.mtpolls.polls;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import masecla.mlib.classes.Registerable;
import masecla.mlib.classes.Replaceable;
import masecla.mlib.main.MLib;

public class PollManager extends Registerable {

    public PollManager(MLib lib) {
        super(lib);
    }

    public void savePoll(Poll poll) {
        lib.getConfigurationAPI().getConfig("data").set("polls." + poll.getPollId(), poll);
    }

    public void savePollOption(PollOption pollOption) {
        lib.getConfigurationAPI().getConfig("data").set("poll-options." + pollOption.getPollOptionId(), pollOption);
    }

    public void vote(Player player, Poll poll, int index) {
        if (!poll.isOpen()) {
            lib.getMessagesAPI().sendMessage("poll-closed", player);
            return;
        }
        for (PollOption cr : getPollOptions(poll)) {
            if (cr.getPlayersVoted().contains(player.getUniqueId())) {
                if (getPollOptions(poll).get(index).equals(cr)) {
                    lib.getMessagesAPI().sendMessage("already-voted", player);
                    return;
                }
                cr.decrementVotes();
                cr.getPlayersVoted().remove(player.getUniqueId());
                savePollOption(cr);
            }
        }
        PollOption newOption = getPollOption(poll.getOptions().get(index).toString());
        newOption.getPlayersVoted().add(player.getUniqueId());
        newOption.incrementVotes();
        savePollOption(newOption);
        lib.getMessagesAPI().sendMessage("poll-successfully-voted", player,
                new Replaceable("%option%", newOption.getOption()),
                new Replaceable("%name%", poll.getName()));
    }

    public List<Poll> getPolls(Boolean open) {
        List<Poll> polls = new ArrayList<>();
        if (lib.getConfigurationAPI().getConfig("data").getConfigurationSection("polls") == null) {
            return polls;
        }
        for (Object cr : lib.getConfigurationAPI().getConfig("data").getConfigurationSection("polls").getKeys(false)) {
            Poll poll = (Poll) lib.getConfigurationAPI().getConfig("data").get("polls." + cr);
            if (open) {
                if (poll.isOpen())
                    polls.add(poll);
            } else {
                if (!poll.isOpen())
                    polls.add(poll);
            }
        }
        return polls;
    }

    public List<PollOption> getPollOptions(Poll poll) {
        List<PollOption> pollOptions = new ArrayList<>();
        for (UUID cr : poll.getOptions())
            pollOptions.add(getPollOption(cr.toString()));
        return pollOptions;
    }

    public Poll getPoll(String id) {
        return (Poll) lib.getConfigurationAPI().getConfig("data").get("polls." + id);
    }

    public PollOption getPollOption(String id) {
        return (PollOption) lib.getConfigurationAPI().getConfig("data").get("poll-options." + id);
    }

    @SuppressWarnings("unchecked")
    public List<String> getSkulls(String id) {
        List<String> skulls = new ArrayList<>();
        for (String cr : (ArrayList<String>) lib.getConfigurationAPI().getConfig("config").get(id)) {
            skulls.add(cr);
        }
        return skulls;
    }

    public int getMaxVotes(Poll poll) {
        int max = 0;
        for (PollOption cr : getPollOptions(poll)) {
            if (cr.getVotes() > max)
                max = cr.getVotes();
        }
        return max;
    }

}
