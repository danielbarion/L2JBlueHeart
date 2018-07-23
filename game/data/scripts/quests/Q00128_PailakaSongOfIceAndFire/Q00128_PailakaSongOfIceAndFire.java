/*
 * Copyright (C) 2004-2015 L2J DataPack
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
package quests.Q00128_PailakaSongOfIceAndFire;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Instance;
import l2r.gameserver.model.holders.SkillHolder;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * Pailaka - Song of Ice and Fire (128)
 * @author Gnacik, St3eT
 */
public final class Q00128_PailakaSongOfIceAndFire extends Quest
{
	// NPCs
	private static final int ADLER1 = 32497;
	private static final int ADLER2 = 32510;
	private static final int SINAI = 32500;
	private static final int INSPECTOR = 32507;
	private static final int HILLAS = 18610;
	private static final int PAPION = 18609;
	private static final int KINSUS = 18608;
	private static final int GARGOS = 18607;
	private static final int ADIANTUM = 18620;
	// Items
	private static final int SWORD = 13034;
	private static final int ENH_SWORD1 = 13035;
	private static final int ENH_SWORD2 = 13036;
	private static final int BOOK1 = 13130;
	private static final int BOOK2 = 13131;
	private static final int BOOK3 = 13132;
	private static final int BOOK4 = 13133;
	private static final int BOOK5 = 13134;
	private static final int BOOK6 = 13135;
	private static final int BOOK7 = 13136;
	private static final int WATER_ESSENCE = 13038;
	private static final int FIRE_ESSENCE = 13039;
	private static final int SHIELD_POTION = 13032;
	private static final int HEAL_POTION = 13033;
	private static final int FIRE_ENHANCER = 13040;
	private static final int WATER_ENHANCER = 13041;
	private static final int[] REWARDS =
	{
		13294, // Pailaka Ring
		13293, // Pailaka Earring
		736, // Scroll of Escape
	};
	// Skills
	private static SkillHolder VITALITY_REPLENISHING = new SkillHolder(5774, 1);
	// Misc
	private static final int MIN_LEVEL = 36;
	private static final int MAX_LEVEL = 42;
	private static final int EXIT_TIME = 5;
	
	public Q00128_PailakaSongOfIceAndFire()
	{
		super(128, Q00128_PailakaSongOfIceAndFire.class.getSimpleName(), "Pailaka - Song of Ice and Fire");
		addStartNpc(ADLER1);
		addTalkId(ADLER1, ADLER2, SINAI, INSPECTOR);
		addKillId(HILLAS, PAPION, KINSUS, GARGOS, ADIANTUM);
		registerQuestItems(SWORD, ENH_SWORD1, ENH_SWORD2, BOOK1, BOOK2, BOOK3, BOOK4, BOOK5, BOOK6, BOOK7, WATER_ESSENCE, FIRE_ESSENCE, SHIELD_POTION, HEAL_POTION, FIRE_ENHANCER, WATER_ENHANCER);
	}
	
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return getNoQuestMsg(player);
		}
		
		switch (event)
		{
			case "32500-02.htm":
			case "32500-03.htm":
			case "32500-04.htm":
			case "32500-05.htm":
			case "32497-02.htm":
			case "32507-07.htm":
			case "32497-04.htm":
			{
				htmltext = event;
				break;
			}
			case "32497-03.htm":
			{
				if (!st.isStarted())
				{
					st.startQuest();
					htmltext = event;
				}
				break;
			}
			case "32500-06.htm":
			{
				if (st.isCond(1))
				{
					st.setCond(2, true);
					giveItems(player, SWORD, 1);
					giveItems(player, BOOK1, 1);
					htmltext = event;
				}
				break;
			}
			case "32507-04.htm":
			{
				if (st.isCond(3))
				{
					st.setCond(4, true);
					takeItems(player, SWORD, -1);
					takeItems(player, WATER_ESSENCE, -1);
					takeItems(player, BOOK2, -1);
					giveItems(player, BOOK3, 1);
					giveItems(player, ENH_SWORD1, 1);
					htmltext = event;
				}
				break;
			}
			case "32507-08.htm":
			{
				if (st.isCond(6))
				{
					st.setCond(7, true);
					takeItems(player, ENH_SWORD1, -1);
					takeItems(player, BOOK5, -1);
					takeItems(player, FIRE_ESSENCE, -1);
					giveItems(player, ENH_SWORD2, 1);
					giveItems(player, BOOK6, 1);
					htmltext = event;
				}
				break;
			}
			case "32510-02.htm":
			{
				st.exitQuest(false, true);
				
				Instance inst = InstanceManager.getInstance().getInstance(npc.getInstanceId());
				inst.setDuration(EXIT_TIME * 60000);
				inst.setEmptyDestroyTime(0);
				
				if (inst.containsPlayer(player.getObjectId()))
				{
					npc.setTarget(player);
					npc.doCast(VITALITY_REPLENISHING.getSkill());
					addExpAndSp(player, 810000, 50000);
					for (int id : REWARDS)
					{
						giveItems(player, id, 1);
					}
				}
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public final String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		if (st == null)
		{
			return htmltext;
		}
		
		switch (npc.getId())
		{
			case ADLER1:
			{
				switch (st.getState())
				{
					case State.CREATED:
					{
						if (player.getLevel() < MIN_LEVEL)
						{
							htmltext = "32497-05.htm";
						}
						else if (player.getLevel() > MAX_LEVEL)
						{
							htmltext = "32497-06.htm";
						}
						else
						{
							htmltext = "32497-01.htm";
						}
						break;
					}
					case State.STARTED:
					{
						if (st.getCond() > 1)
						{
							htmltext = "32497-00.htm";
						}
						else
						{
							htmltext = "32497-03.htm";
						}
						break;
					}
					case State.COMPLETED:
					{
						htmltext = "32497-07.htm";
						break;
					}
					default:
					{
						htmltext = "32497-01.htm";
						break;
					}
				}
				break;
			}
			case SINAI:
			{
				if (st.getCond() > 1)
				{
					htmltext = "32500-00.htm";
				}
				else
				{
					htmltext = "32500-01.htm";
				}
				break;
			}
			case INSPECTOR:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = "32507-01.htm";
						break;
					}
					case 2:
					{
						htmltext = "32507-02.htm";
						break;
					}
					case 3:
					{
						htmltext = "32507-03.htm";
						break;
					}
					case 4:
					case 5:
					{
						htmltext = "32507-05.htm";
						break;
					}
					case 6:
					{
						htmltext = "32507-06.htm";
						break;
					}
					default:
					{
						htmltext = "32507-09.htm";
						break;
					}
				}
				break;
			}
			case ADLER2:
			{
				if (st.isCompleted())
				{
					htmltext = "32510-00.htm";
				}
				else if (st.isCond(9))
				{
					htmltext = "32510-01.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public final String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		final QuestState st = player.getQuestState(getName());
		if ((st != null) && st.isStarted())
		{
			switch (npc.getId())
			{
				case HILLAS:
				{
					if (st.isCond(2))
					{
						st.setCond(3);
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						takeItems(player, BOOK1, -1);
						giveItems(player, BOOK2, 1);
						giveItems(player, WATER_ESSENCE, 1);
					}
					addSpawn(PAPION, -53903, 181484, -4555, 30456, false, 0, false, npc.getInstanceId());
					break;
				}
				case PAPION:
				{
					if (st.isCond(4))
					{
						st.setCond(5);
						takeItems(player, BOOK3, -1);
						giveItems(player, BOOK4, 1);
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					addSpawn(KINSUS, -61415, 181418, -4818, 63852, false, 0, false, npc.getInstanceId());
					break;
				}
				case KINSUS:
				{
					if (st.isCond(5))
					{
						st.setCond(6);
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						takeItems(player, BOOK4, -1);
						giveItems(player, BOOK5, 1);
						giveItems(player, FIRE_ESSENCE, 1);
					}
					addSpawn(GARGOS, -61354, 183624, -4821, 63613, false, 0, false, npc.getInstanceId());
					break;
				}
				case GARGOS:
				{
					if (st.isCond(7))
					{
						st.setCond(8);
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						takeItems(player, BOOK6, -1);
						giveItems(player, BOOK7, 1);
					}
					addSpawn(ADIANTUM, -53297, 185027, -4617, 1512, false, 0, false, npc.getInstanceId());
					break;
				}
				case ADIANTUM:
				{
					if (st.isCond(8))
					{
						st.setCond(9);
						playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						takeItems(player, BOOK7, -1);
						addSpawn(ADLER2, -53297, 185027, -4617, 33486, false, 0, false, npc.getInstanceId());
					}
					break;
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
}