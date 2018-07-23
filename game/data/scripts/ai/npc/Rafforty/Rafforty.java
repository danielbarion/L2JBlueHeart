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
package ai.npc.Rafforty;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import ai.npc.AbstractNpcAI;

/**
 * Rafforty AI.
 * @author malyelfik, Gladicek
 */
public class Rafforty extends AbstractNpcAI
{
	// NPC
	private static final int RAFFORTY = 32020;
	// Items
	private static final int NECKLACE = 16025;
	private static final int BLESSED_NECKLACE = 16026;
	private static final int BOTTLE = 16027;
	
	public Rafforty()
	{
		super(Rafforty.class.getSimpleName(), "ai/npc");
		addStartNpc(RAFFORTY);
		addFirstTalkId(RAFFORTY);
		addTalkId(RAFFORTY);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		switch (event)
		{
			case "32020-01.html":
				if (!hasQuestItems(player, NECKLACE))
				{
					htmltext = "32020-02.html";
				}
				break;
			case "32020-04.html":
				if (!hasQuestItems(player, BOTTLE))
				{
					htmltext = "32020-05.html";
				}
				break;
			case "32020-07.html":
				if (!hasQuestItems(player, BOTTLE, NECKLACE))
				{
					return "32020-08.html";
				}
				takeItems(player, NECKLACE, 1);
				takeItems(player, BOTTLE, 1);
				giveItems(player, BLESSED_NECKLACE, 1);
				break;
		}
		return htmltext;
	}
}