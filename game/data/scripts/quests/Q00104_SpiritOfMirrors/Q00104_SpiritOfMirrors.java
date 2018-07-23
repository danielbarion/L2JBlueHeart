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
package quests.Q00104_SpiritOfMirrors;

import java.util.HashMap;
import java.util.Map;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.enums.Race;
import l2r.gameserver.enums.audio.Voice;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.ItemHolder;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.network.NpcStringId;

/**
 * Spirit of Mirrors (104)
 * @author xban1x
 */
public final class Q00104_SpiritOfMirrors extends Quest
{
	// NPCs
	private static final int GALLINT = 30017;
	private static final int ARNOLD = 30041;
	private static final int JOHNSTONE = 30043;
	private static final int KENYOS = 30045;
	// Items
	private static final int GALLINTS_OAK_WAND = 748;
	private static final int SPIRITBOUND_WAND1 = 1135;
	private static final int SPIRITBOUND_WAND2 = 1136;
	private static final int SPIRITBOUND_WAND3 = 1137;
	// Monsters
	private static final Map<Integer, Integer> MONSTERS = new HashMap<>();
	
	static
	{
		MONSTERS.put(27003, SPIRITBOUND_WAND1); // Spirit Of Mirrors
		MONSTERS.put(27004, SPIRITBOUND_WAND2); // Spirit Of Mirrors
		MONSTERS.put(27005, SPIRITBOUND_WAND3); // Spirit Of Mirrors
	}
	
	// Rewards
	private static final int SOULSHOT_NO_GRADE = 1835;
	private static final int SPIRITSHOT_NO_GRADE = 2509;
	private static final int SPIRITSHOT = 5790;
	private static final ItemHolder[] REWARDS =
	{
		new ItemHolder(1060, 100), // Lesser Healing Potion
		new ItemHolder(4412, 10), // Echo Crystal - Theme of Battle
		new ItemHolder(4413, 10), // Echo Crystal - Theme of Love
		new ItemHolder(4414, 10), // Echo Crystal - Theme of Solitude
		new ItemHolder(4415, 10), // Echo Crystal - Theme of Feast
		new ItemHolder(4416, 10), // Echo Crystal - Theme of Celebration
		new ItemHolder(747, 1), // Wand of Adept
	};
	// Misc
	private static final int MIN_LVL = 10;
	
	public Q00104_SpiritOfMirrors()
	{
		super(104, Q00104_SpiritOfMirrors.class.getSimpleName(), "Spirit of Mirrors");
		addStartNpc(GALLINT);
		addTalkId(ARNOLD, GALLINT, JOHNSTONE, KENYOS);
		addKillId(MONSTERS.keySet());
		registerQuestItems(GALLINTS_OAK_WAND, SPIRITBOUND_WAND1, SPIRITBOUND_WAND2, SPIRITBOUND_WAND3);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if ((st != null) && event.equalsIgnoreCase("30017-04.htm"))
		{
			st.startQuest();
			st.giveItems(GALLINTS_OAK_WAND, 3);
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = killer.getQuestState(getName());
		if ((st != null) && (st.isCond(1) || st.isCond(2)) && (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == GALLINTS_OAK_WAND) && !st.hasQuestItems(MONSTERS.get(npc.getId())))
		{
			st.takeItems(GALLINTS_OAK_WAND, 1);
			st.giveItems(MONSTERS.get(npc.getId()), 1);
			if (st.hasQuestItems(SPIRITBOUND_WAND1, SPIRITBOUND_WAND2, SPIRITBOUND_WAND3))
			{
				st.setCond(3, true);
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
		String htmltext = getNoQuestMsg(player);
		if (st != null)
		{
			switch (npc.getId())
			{
				case GALLINT:
				{
					switch (st.getState())
					{
						case State.CREATED:
						{
							htmltext = (player.getRace() == Race.HUMAN) ? (player.getLevel() >= MIN_LVL) ? "30017-03.htm" : "30017-02.htm" : "30017-01.htm";
							break;
						}
						case State.STARTED:
						{
							if (st.isCond(3) && st.hasQuestItems(SPIRITBOUND_WAND1, SPIRITBOUND_WAND2, SPIRITBOUND_WAND3))
							{
								if ((player.getLevel() < 25) && player.isMageClass())
								{
									st.rewardItems(SPIRITSHOT, 3000);
									playSound(player, Voice.TUTORIAL_VOICE_027_1000);
								}
								for (ItemHolder reward : REWARDS)
								{
									st.giveItems(reward);
								}
								if (player.isMageClass())
								{
									st.giveItems(SPIRITSHOT_NO_GRADE, 500);
								}
								else
								{
									st.giveItems(SOULSHOT_NO_GRADE, 1000);
								}
								st.addExpAndSp(39750, 3407);
								st.giveAdena(16866, true);
								st.exitQuest(false, true);
								// TODO: Newbie Guide
								showOnScreenMsg(player, NpcStringId.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_N_GO_FIND_THE_NEWBIE_GUIDE, 2, 5000);
								htmltext = "30017-06.html";
							}
							else
							{
								htmltext = "30017-05.html";
							}
							break;
						}
						case State.COMPLETED:
						{
							htmltext = getAlreadyCompletedMsg(player);
							break;
						}
					}
					break;
				}
				case ARNOLD:
				case JOHNSTONE:
				case KENYOS:
				{
					if (st.isCond(1))
					{
						if (!st.isSet(npc.getName()))
						{
							st.set(npc.getName(), "1");
						}
						if (st.isSet("Arnold") && st.isSet("Johnstone") && st.isSet("Kenyos"))
						{
							st.setCond(2, true);
						}
					}
					htmltext = npc.getId() + "-01.html";
					break;
				}
			}
		}
		return htmltext;
	}
}
