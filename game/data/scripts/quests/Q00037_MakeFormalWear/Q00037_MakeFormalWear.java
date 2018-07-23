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
package quests.Q00037_MakeFormalWear;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * Make Formal Wear (37)
 * @author malyelfik
 */
public class Q00037_MakeFormalWear extends Quest
{
	// NPCs
	private static final int ALEXIS = 30842;
	private static final int LEIKAR = 31520;
	private static final int JEREMY = 31521;
	private static final int MIST = 31627;
	// Items
	private static final int FORMAL_WEAR = 6408;
	private static final int MYSTERIOUS_CLOTH = 7076;
	private static final int JEWEL_BOX = 7077;
	private static final int SEWING_KIT = 7078;
	private static final int DRESS_SHOES_BOX = 7113;
	private static final int BOX_OF_COOKIES = 7159;
	private static final int ICE_WINE = 7160;
	private static final int SIGNET_RING = 7164;
	// Misc
	private static final int MIN_LEVEL = 60;
	
	public Q00037_MakeFormalWear()
	{
		super(37, Q00037_MakeFormalWear.class.getSimpleName(), "Make Formal Wear");
		addStartNpc(ALEXIS);
		addTalkId(ALEXIS, JEREMY, LEIKAR, MIST);
		registerQuestItems(SIGNET_RING, ICE_WINE, BOX_OF_COOKIES);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "30842-03.htm":
				st.startQuest();
				break;
			case "31520-02.html":
				st.giveItems(SIGNET_RING, 1);
				st.setCond(2, true);
				break;
			case "31521-02.html":
				st.giveItems(ICE_WINE, 1);
				st.setCond(3, true);
				break;
			case "31627-02.html":
				if (!st.hasQuestItems(ICE_WINE))
				{
					return getNoQuestMsg(player);
				}
				st.takeItems(ICE_WINE, 1);
				st.setCond(4, true);
				break;
			case "31521-05.html":
				st.giveItems(BOX_OF_COOKIES, 1);
				st.setCond(5, true);
				break;
			case "31520-05.html":
				if (!st.hasQuestItems(BOX_OF_COOKIES))
				{
					return getNoQuestMsg(player);
				}
				st.takeItems(BOX_OF_COOKIES, 1);
				st.setCond(6, true);
				break;
			case "31520-08.html":
				if (!st.hasQuestItems(SEWING_KIT, JEWEL_BOX, MYSTERIOUS_CLOTH))
				{
					return "31520-09.html";
				}
				st.takeItems(SEWING_KIT, 1);
				st.takeItems(JEWEL_BOX, 1);
				st.takeItems(MYSTERIOUS_CLOTH, 1);
				st.setCond(7, true);
				break;
			case "31520-12.html":
				if (!st.hasQuestItems(DRESS_SHOES_BOX))
				{
					return "31520-13.html";
				}
				st.takeItems(DRESS_SHOES_BOX, 1);
				st.giveItems(FORMAL_WEAR, 1);
				st.exitQuest(false, true);
				break;
			default:
				htmltext = null;
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		if (st == null)
		{
			return htmltext;
		}
		switch (npc.getId())
		{
			case ALEXIS:
				switch (st.getState())
				{
					case State.CREATED:
						htmltext = (player.getLevel() >= MIN_LEVEL) ? "30842-01.htm" : "30842-02.html";
						break;
					case State.STARTED:
						if (st.isCond(1))
						{
							htmltext = "30842-04.html";
						}
						break;
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
				}
				break;
			case LEIKAR:
				if (st.isStarted())
				{
					switch (st.getCond())
					{
						case 1:
							htmltext = "31520-01.html";
							break;
						case 2:
							htmltext = "31520-03.html";
							break;
						case 5:
							htmltext = "31520-04.html";
							break;
						case 6:
							htmltext = (st.hasQuestItems(SEWING_KIT, JEWEL_BOX, MYSTERIOUS_CLOTH)) ? "31520-06.html" : "31520-07.html";
							break;
						case 7:
							htmltext = (st.hasQuestItems(DRESS_SHOES_BOX)) ? "31520-10.html" : "31520-11.html";
							break;
							
					}
				}
				break;
			case JEREMY:
				if (st.isStarted())
				{
					switch (st.getCond())
					{
						case 2:
							htmltext = "31521-01.html";
							break;
						case 3:
							htmltext = "31521-03.html";
							break;
						case 4:
							htmltext = "31521-04.html";
							break;
						case 5:
							htmltext = "31521-06.html";
							break;
					}
				}
				break;
			case MIST:
				if (st.isStarted())
				{
					switch (st.getCond())
					{
						case 3:
							htmltext = "31627-01.html";
							break;
						case 4:
							htmltext = "31627-03.html";
							break;
					}
				}
				break;
		}
		return htmltext;
	}
}
