package teammt.mtpolls.main;

import java.time.Instant;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import masecla.mlib.main.MLib;
import teammt.mtpolls.commands.PollsCommand;
import teammt.mtpolls.containers.ActivePollsContainer;
import teammt.mtpolls.containers.ClosedPollsContainer;
import teammt.mtpolls.containers.PollCreationContainer;
import teammt.mtpolls.containers.ViewableActivePollContainer;
import teammt.mtpolls.containers.ViewableClosedPollContainer;
import teammt.mtpolls.polls.Poll;
import teammt.mtpolls.polls.PollManager;
import teammt.mtpolls.polls.PollOption;

public class MTPolls extends JavaPlugin {
    private MLib lib;
    private PollManager pollManager;
    private PollCreationContainer pollCreationContainer;
    private ActivePollsContainer activePollsContainer;
    private ViewableActivePollContainer viewableActivePollContainer;
    private ClosedPollsContainer closedPollsContainer;
    private ViewableClosedPollContainer viewableClosedPollContainer;

    @Override
    public void onEnable() {
        this.lib = new MLib(this);
        lib.getConfigurationAPI().requireAll();

        // Managers
        pollManager = new PollManager(lib);
        pollManager.register();
        viewableActivePollContainer = new ViewableActivePollContainer(lib, pollManager);
        viewableActivePollContainer.register();
        viewableClosedPollContainer = new ViewableClosedPollContainer(lib, pollManager);
        viewableClosedPollContainer.register();
        pollCreationContainer = new PollCreationContainer(lib, pollManager, viewableActivePollContainer);
        pollCreationContainer.register();
        activePollsContainer = new ActivePollsContainer(lib, pollManager, viewableActivePollContainer);
        activePollsContainer.register();
        closedPollsContainer = new ClosedPollsContainer(lib, pollManager, viewableClosedPollContainer);
        closedPollsContainer.register();

        // Commands
        new PollsCommand(lib, pollCreationContainer).register();

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                List<Poll> pollsList = pollManager.getPolls(true);
                for (Poll poll : pollsList) {
                    if (poll.isOpen()) {
                        int votes = 0;
                        for (PollOption option : pollManager.getPollOptions(poll))
                            votes += option.getVotes();
                        poll.setVotes(votes);
                        if (poll.getCreatedAt() + poll.getDuration() < Instant.now().getEpochSecond()) {
                            poll.setOpen(false);

                        }
                        pollManager.savePoll(poll);
                    }
                }
            }
        }, 0L, 20L);

    }

}