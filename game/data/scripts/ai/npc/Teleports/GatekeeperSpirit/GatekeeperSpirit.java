/*
 * Copyright (C) 2004-2013 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.npc.Teleports.GatekeeperSpirit;

import l2r.gameserver.SevenSigns;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import ai.npc.AbstractNpcAI;

/**
 * Gatekeeper Spirit AI.
 * @author Zoey76
 */
public class GatekeeperSpirit extends AbstractNpcAI
{
	// NPCs
	private static final int GATEKEEPER_SPIRIT_ENTER = 31111;
	private static final int GATEKEEPER_SPIRIT_EXIT = 31112;
	private static final int LILITH = 25283;
	private static final int ANAKIM = 25286;
	// Exit gatekeeper spawn locations
	private static final Location SPAWN_LILITH_GATEKEEPER = new Location(184410, -10111, -5488);
	private static final Location SPAWN_ANAKIM_GATEKEEPER = new Location(184410, -13102, -5488);
	// Teleport
	private static final Location TELEPORT_DUSK = new Location(184464, -13104, -5504);
	private static final Location TELEPORT_DAWN = new Location(184448, -10112, -5504);
	private static final Location EXIT = new Location(182960, -11904, -4897);
	
	public GatekeeperSpirit()
	{
		super(GatekeeperSpirit.class.getSimpleName(), "ai/npc/Teleports");
		addStartNpc(GATEKEEPER_SPIRIT_ENTER, GATEKEEPER_SPIRIT_EXIT);
		addFirstTalkId(GATEKEEPER_SPIRIT_ENTER, GATEKEEPER_SPIRIT_EXIT);
		addTalkId(GATEKEEPER_SPIRIT_ENTER, GATEKEEPER_SPIRIT_EXIT);
		addKillId(LILITH, ANAKIM);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "ANAKIM":
			{
				addSpawn(GATEKEEPER_SPIRIT_EXIT, SPAWN_ANAKIM_GATEKEEPER, false, 900000);
				break;
			}
			case "LILITH":
			{
				addSpawn(GATEKEEPER_SPIRIT_EXIT, SPAWN_LILITH_GATEKEEPER, false, 900000);
				break;
			}
			case "TeleportIn":
			{
				final int playerCabal = SevenSigns.getInstance().getPlayerCabal(player.getObjectId());
				final int sealOfAvariceOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE);
				final int compWinner = SevenSigns.getInstance().getCabalHighestScore();
				if (!SevenSigns.getInstance().isSealValidationPeriod())
				{
					htmltext = "31111-no.html";
				}
				else if ((compWinner == SevenSigns.CABAL_DUSK) && (playerCabal == SevenSigns.CABAL_DUSK) && (sealOfAvariceOwner == SevenSigns.CABAL_DUSK))
				{
					player.teleToLocation(TELEPORT_DUSK, false);
				}
				else if ((compWinner == SevenSigns.CABAL_DAWN) && (playerCabal == SevenSigns.CABAL_DAWN) && (sealOfAvariceOwner == SevenSigns.CABAL_DAWN))
				{
					player.teleToLocation(TELEPORT_DAWN, false);
				}
				else
				{
					htmltext = "31111-no.html";
				}
				break;
			}
			case "TeleportOut":
			{
				player.teleToLocation(EXIT, true);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		switch (npc.getId())
		{
			case ANAKIM:
			{
				startQuestTimer("ANAKIM", 10000, npc, killer);
				break;
			}
			case LILITH:
			{
				startQuestTimer("LILITH", 10000, npc, killer);
				break;
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}