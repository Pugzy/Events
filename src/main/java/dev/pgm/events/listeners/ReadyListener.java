package dev.pgm.events.listeners;

import java.time.Duration;

import dev.pgm.events.events.TeamReadyEvent;
import dev.pgm.events.events.TeamUnreadyEvent;
import dev.pgm.events.ready.ReadyParties;
import dev.pgm.events.ready.ReadySystem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.match.event.MatchLoadEvent;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.events.CountdownCancelEvent;
import tc.oc.pgm.events.CountdownStartEvent;
import tc.oc.pgm.start.StartCountdown;
import tc.oc.pgm.start.StartMatchModule;

public class ReadyListener implements Listener {

  private final ReadySystem system;
  private final ReadyParties parties;

  public ReadyListener(ReadySystem system, ReadyParties parties) {
    this.system = system;
    this.parties = parties;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onReady(TeamReadyEvent event) {

    Party party = event.getTeam();
    Match match = party.getMatch();

    Bukkit.broadcastMessage(
            party.getColor() + party.getNameLegacy() + ChatColor.RESET + " is now ready.");

    parties.ready(party);

    if (parties.allReady(match))
      match
              .needModule(StartMatchModule.class)
              .forceStartCountdown(Duration.ofSeconds(20), Duration.ZERO);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onUnready(TeamUnreadyEvent event) {
    Party party = event.getTeam();
    Match match = party.getMatch();

    Bukkit.broadcastMessage(
            party.getColor()
                    + party.getNameLegacy()
                    + ChatColor.RESET
                    + " is now unready.");

    if (parties.allReady(match)) {
      parties.unReady(party);
      if (system.unreadyShouldCancel()) {
        // check if unready should cancel
        match.getCountdown().cancelAll(StartCountdown.class);
      }
    } else {
      parties.unReady(party);
    }
  }

  @EventHandler
  public void onQueueStart(CountdownStartEvent event) {
    if (event.getCountdown() instanceof StartCountdown)
      system.onStart(
          ((StartCountdown) event.getCountdown()).getRemaining(),
          parties.allReady(event.getMatch()));
  }

  @EventHandler
  public void onCancel(CountdownCancelEvent event) {
    if (!(event.getCountdown() instanceof StartCountdown)) return;

    Duration remaining = system.onCancel(parties.allReady(event.getMatch()));
    if (remaining != null)
      event
          .getMatch()
          .needModule(StartMatchModule.class)
          .forceStartCountdown(remaining, Duration.ZERO);
  }

  @EventHandler
  public void onStart(MatchLoadEvent event) {
    system.reset();
  }
}
