package dev.pgm.events.events;

import tc.oc.pgm.api.party.Party;

public class TeamUnreadyEvent extends TournamentTeamEvent {
    public TeamUnreadyEvent(Party team) {
        super(team);
    }
}
