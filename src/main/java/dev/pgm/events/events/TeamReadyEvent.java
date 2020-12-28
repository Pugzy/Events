package dev.pgm.events.events;

import tc.oc.pgm.api.party.Party;

public class TeamReadyEvent extends TournamentTeamEvent {
  public TeamReadyEvent(Party team) {
    super(team);
  }
}
