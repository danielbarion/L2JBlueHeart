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
package quests.Q00271_ProofOfValor;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.enums.Race;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * Proof of Valor (271)
 * @author xban1x
 */
public final class Q00271_ProofOfValor extends Quest
{
	// NPC
	private static final int RUKAIN = 30577;
	// Items
	private static final int KASHA_WOLF_FANG = 1473;
	// Monsters
	private static final int KASHA_WOLF = 20475;
	// Rewards
	private static final int HEALING_POTION = 1061;
	private static final int NECKLACE_OF_COURAGE = 1506;
	private static final int NECKLACE_OF_VALOR = 1507;
	// Misc
	private static final int MIN_LVL = 4;
	
	public Q00271_ProofOfValor()
	{
		super(271, Q00271_ProofOfValor.class.getSimpleName(), "Proof of Valor");
		addStartNpc(RUKAIN);
		addTalkId(RUKAIN);
		addKillId(KASHA_WOLF);
		registerQuestItems(KASHA_WOLF_FANG);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if ((st != null) && event.equalsIgnoreCase("30577-04.htm"))
		{
			st.startQuest();
			return hasAtLeastOneQuestItem(player, NECKLACE_OF_VALOR, NECKLACE_OF_COURAGE) ? "30577-08.html" : event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = killer.getQuestState(getName());
		if ((st != null) && st.isCond(1))
		{
			final long count = st.getQuestItemsCount(KASHA_WOLF_FANG);
			int amount = ((getRandom(100) < 25) && (count < 49)) ? 2 : 1;
			st.giveItems(KASHA_WOLF_FANG, amount);
			if ((count + amount) >= 50)
			{
				st.setCond(2, true);
			}
			else
			{
				st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = null;
		if (st != null)
		{
			switch (st.getState())
			{
				case State.CREATED:
				{
					htmltext = (player.getRace() == Race.ORC) ? (player.getLevel() >= MIN_LVL) ? (hasAtLeastOneQuestItem(player, NECKLACE_OF_VALOR, NECKLACE_OF_COURAGE)) ? "30577-07.htm" : "30577-03.htm" : "30577-02.htm" : "30577-01.htm";
					break;
				}
				case State.STARTED:
				{
					switch (st.getCond())
					{
						case 1:
						{
							htmltext = "30577-05.html";
							break;
						}
						case 2:
						{
							if (st.getQuestItemsCount(KASHA_WOLF_FANG) >= 50)
							{
								if (getRandom(100) <= 13)
								{
									st.rewardItems(NECKLACE_OF_VALOR, 1);
									st.rewardItems(HEALING_POTION, 10);
								}
								else
								{
									st.rewardItems(NECKLACE_OF_COURAGE, 1);
								}
								st.takeItems(KASHA_WOLF_FANG, -1);
								st.exitQuest(true, true);
								htmltext = "30577-06.html";
							}
							break;
						}
					}
					break;
				}
			}
		}
		return htmltext;
	}
}
