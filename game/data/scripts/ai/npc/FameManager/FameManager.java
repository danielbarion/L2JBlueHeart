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
package ai.npc.FameManager;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.SystemMessageId;

import ai.npc.AbstractNpcAI;

/**
 * Fame Manager AI.
 * @author St3eT
 */
public class FameManager extends AbstractNpcAI
{
	// Npc
	private static final int[] FAME_MANAGER =
	{
		36479, // Rapidus
		36480, // Scipio
	};
	// Misc
	private static final int MIN_LVL = 40;
	private static final int DECREASE_COST = 5000;
	private static final int REPUTATION_COST = 1000;
	private static final int MIN_CLAN_LVL = 5;
	private static final int CLASS_LVL = 2;
	
	public FameManager()
	{
		super(FameManager.class.getSimpleName(), "ai/npc");
		addStartNpc(FAME_MANAGER);
		addTalkId(FAME_MANAGER);
		addFirstTalkId(FAME_MANAGER);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "36479.html":
			case "36479-02.html":
			case "36479-07.html":
			case "36480.html":
			case "36480-02.html":
			case "36480-07.html":
			{
				htmltext = event;
				break;
			}
			case "decreasePk":
			{
				if (player.getPkKills() > 0)
				{
					if ((player.getFame() >= DECREASE_COST) && (player.getLevel() >= MIN_LVL) && (player.getClassId().level() >= CLASS_LVL))
					{
						player.setFame(player.getFame() - DECREASE_COST);
						player.setPkKills(player.getPkKills() - 1);
						player.sendUserInfo(true);
						htmltext = npc.getId() + "-06.html";
					}
					else
					{
						htmltext = npc.getId() + "-01.html";
					}
				}
				else
				{
					htmltext = npc.getId() + "-05.html";
				}
				break;
			}
			case "clanRep":
			{
				if ((player.getClan() != null) && (player.getClan().getLevel() >= MIN_CLAN_LVL))
				{
					if ((player.getFame() >= REPUTATION_COST) && (player.getLevel() >= MIN_LVL) && (player.getClassId().level() >= CLASS_LVL))
					{
						player.setFame(player.getFame() - REPUTATION_COST);
						player.getClan().addReputationScore(50, true);
						player.sendUserInfo(true);
						player.sendPacket(SystemMessageId.ACQUIRED_50_CLAN_FAME_POINTS);
						htmltext = npc.getId() + "-04.html";
					}
					else
					{
						htmltext = npc.getId() + "-01.html";
					}
				}
				else
				{
					htmltext = npc.getId() + "-03.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return ((player.getFame() > 0) && (player.getLevel() >= MIN_LVL) && (player.getClassId().level() >= CLASS_LVL)) ? npc.getId() + ".html" : npc.getId() + "-01.html";
	}
}
