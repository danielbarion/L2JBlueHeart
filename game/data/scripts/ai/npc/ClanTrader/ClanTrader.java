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
package ai.npc.ClanTrader;

import l2r.Config;
import l2r.gameserver.model.ClanPrivilege;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;

import ai.npc.AbstractNpcAI;

/**
 * Clan Trader AI.
 * @author St3eT
 */
public class ClanTrader extends AbstractNpcAI
{
	// Npc
	private static final int[] CLAN_TRADER =
	{
		32024, // Mulia
		32025, // Ilia
	};
	// Items
	private static final int BLOOD_ALLIANCE = 9911; // Blood Alliance
	private static final int BLOOD_ALLIANCE_COUNT = 1; // Blood Alliance Count
	private static final int BLOOD_OATH = 9910; // Blood Oath
	private static final int BLOOD_OATH_COUNT = 10; // Blood Oath Count
	private static final int KNIGHTS_EPAULETTE = 9912; // Knight's Epaulette
	private static final int KNIGHTS_EPAULETTE_COUNT = 100; // Knight's Epaulette Count
	
	public ClanTrader()
	{
		super(ClanTrader.class.getSimpleName(), "ai/npc");
		addStartNpc(CLAN_TRADER);
		addTalkId(CLAN_TRADER);
		addFirstTalkId(CLAN_TRADER);
	}
	
	private String giveReputation(L2Npc npc, L2PcInstance player, int count, int itemId, int itemCount)
	{
		if (getQuestItemsCount(player, itemId) >= itemCount)
		{
			takeItems(player, itemId, itemCount);
			player.getClan().addReputationScore(count, true);
			
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_ADDED_S1S_POINTS_TO_REPUTATION_SCORE);
			sm.addInt(count);
			player.sendPacket(sm);
			return npc.getId() + "-04.html";
		}
		return npc.getId() + "-03.html";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "32024.html":
			case "32024-02.html":
			case "32025.html":
			case "32025-02.html":
			{
				htmltext = event;
				break;
			}
			case "repinfo":
			{
				htmltext = (player.getClan().getLevel() > 4) ? npc.getId() + "-02.html" : npc.getId() + "-05.html";
				break;
			}
			case "exchange-ba":
			{
				htmltext = giveReputation(npc, player, Config.BLOODALLIANCE_POINTS, BLOOD_ALLIANCE, BLOOD_ALLIANCE_COUNT);
				break;
			}
			case "exchange-bo":
			{
				htmltext = giveReputation(npc, player, Config.BLOODOATH_POINTS, BLOOD_OATH, BLOOD_OATH_COUNT);
				break;
			}
			case "exchange-ke":
			{
				htmltext = giveReputation(npc, player, Config.KNIGHTSEPAULETTE_POINTS, KNIGHTS_EPAULETTE, KNIGHTS_EPAULETTE_COUNT);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (player.isClanLeader() || player.hasClanPrivilege(ClanPrivilege.CL_TROOPS_FAME))
		{
			return npc.getId() + ".html";
		}
		return npc.getId() + "-01.html";
	}
}
