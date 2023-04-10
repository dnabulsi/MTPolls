package teammt.mtpolls.commands;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import masecla.mlib.annotations.RegisterableInfo;
import masecla.mlib.annotations.SubcommandInfo;
import masecla.mlib.classes.Registerable;
import masecla.mlib.main.MLib;
import teammt.mtpolls.containers.ActivePollsContainer;
import teammt.mtpolls.containers.ClosedPollsContainer;
import teammt.mtpolls.containers.PollCreationContainer;

@RegisterableInfo(command = "mtpolls")
public class PollsCommand extends Registerable {

	private PollCreationContainer pollCreationContainer;

	public PollsCommand(MLib lib, PollCreationContainer pollCreationContainer) {
		super(lib);
		this.pollCreationContainer = pollCreationContainer;
	}

	/**
	 * Help menu.
	 */
	@SubcommandInfo(permission = "teammt.mtpolls.commands.help")
	public void handleHelpNoArgs(CommandSender sender) {
		lib.getMessagesAPI().sendMessage("help-menu", sender);
	}

	@SubcommandInfo(subcommand = "help", permission = "teammt.mtpolls.commands.help")
	public void handleHelpArgs(CommandSender sender) {
		lib.getMessagesAPI().sendMessage("help-menu", sender);
	}

	/**
	 * Reload configurations currently doing the config reload
	 */
	@SubcommandInfo(subcommand = "reload", permission = "teammt.mtpolls.commands.reload")
	public void onReload(Player sender) {
		try {
			lib.getConfigurationAPI().reloadAll();
		} catch (IOException e) {
		}
		lib.getMessagesAPI().reloadSharedConfig();
		lib.getMessagesAPI().sendMessage("plugin-reloaded", sender);
	}

	/**
	 * Open poll creator
	 */
	@SubcommandInfo(subcommand = "create", permission = "teammt.mtpolls.commands.create")
	public void handleTop(Player sender) {
		this.pollCreationContainer.setUUID(sender);
		lib.getMessagesAPI().sendMessage("poll-gui", sender);
		lib.getContainerAPI().openFor(sender, PollCreationContainer.class);
	}

	/**
	 * Reopen poll creator
	 */
	@SubcommandInfo(subcommand = "reopen", permission = "teammt.mtpolls.commands.reopen")
	public void handleTest(Player sender) {
		if (!pollCreationContainer.getCurrentlyCreating().containsKey(sender.getUniqueId())) {
			lib.getMessagesAPI().sendMessage("no-editing-session-found", sender);
			return;
		}
		lib.getContainerAPI().openFor(sender, PollCreationContainer.class);
	}

	/**
	 * View all active polls
	 */
	@SubcommandInfo(subcommand = "list", permission = "teammt.mtpolls.commands.list")
	public void handlePolls(Player sender) {
		lib.getContainerAPI().openFor(sender, ActivePollsContainer.class);
	}

	/**
	 * View all inactive polls
	 */
	@SubcommandInfo(subcommand = "results", permission = "teammt.mtpolls.commands.results")
	public void handleResults(Player sender) {
		lib.getContainerAPI().openFor(sender, ClosedPollsContainer.class);
	}

}