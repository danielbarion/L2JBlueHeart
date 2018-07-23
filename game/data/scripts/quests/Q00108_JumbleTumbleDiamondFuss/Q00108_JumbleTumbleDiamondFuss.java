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
package quests.Q00108_JumbleTumbleDiamondFuss;

import java.util.HashMap;
import java.util.Map;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.enums.Race;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.ItemHolder;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.serverpackets.SocialAction;
import l2r.gameserver.util.Util;

import quests.Q00281_HeadForTheHills.Q00281_HeadForTheHills;

/**
 * Jumble, Tumble, Diamond Fuss (108)
 * @author Janiko
 */
public final class Q00108_JumbleTumbleDiamondFuss extends Quest
{
	// NPCs
	private static final int COLLECTOR_GOUPH = 30523;
	private static final int TRADER_REEP = 30516;
	private static final int CARRIER_TOROCCO = 30555;
	private static final int MINER_MARON = 30529;
	private static final int BLACKSMITH_BRUNON = 30526;
	private static final int WAREHOUSE_KEEPER_MURDOC = 30521;
	private static final int WAREHOUSE_KEEPER_AIRY = 30522;
	// Monsters
	private static final int GOBLIN_BRIGAND_LEADER = 20323;
	private static final int GOBLIN_BRIGAND_LIEUTENANT = 20324;
	private static final int BLADE_BAT = 20480;
	// Items
	private static final int GOUPHS_CONTRACT = 1559;
	private static final int REEPS_CONTRACT = 1560;
	private static final int ELVEN_WINE = 1561;
	private static final int BRUNONS_DICE = 1562;
	private static final int BRUNONS_CONTRACT = 1563;
	private static final int AQUAMARINE = 1564;
	private static final int CHRYSOBERYL = 1565;
	private static final int GEM_BOX = 1566;
	private static final int COAL_PIECE = 1567;
	private static final int BRUNONS_LETTER = 1568;
	private static final int BERRY_TART = 1569;
	private static final int BAT_DIAGRAM = 1570;
	private static final int STAR_DIAMOND = 1571;
	// Rewards
	private static final ItemHolder[] REWARDS =
	{
		new ItemHolder(1060, 100), // Lesser Healing Potion
		new ItemHolder(4412, 10), // Echo Crystal - Theme of Battle
		new ItemHolder(4413, 10), // Echo Crystal - Theme of Love
		new ItemHolder(4414, 10), // Echo Crystal - Theme of Solitude
		new ItemHolder(4415, 10), // Echo Crystal - Theme of Feast
		new ItemHolder(4416, 10), // Echo Crystal - Theme of Celebration
	};
	private static final int SILVERSMITH_HAMMER = 1511;
	// Misc
	private static final int MIN_LVL = 10;
	private static final int MAX_GEM_COUNT = 10;
	private static final Map<Integer, Double> GOBLIN_DROP_CHANCES = new HashMap<>();
	
	static
	{
		GOBLIN_DROP_CHANCES.put(GOBLIN_BRIGAND_LEADER, 0.8);
		GOBLIN_DROP_CHANCES.put(GOBLIN_BRIGAND_LIEUTENANT, 0.6);
	}
	
	public Q00108_JumbleTumbleDiamondFuss()
	{
		super(108, Q00108_JumbleTumbleDiamondFuss.class.getSimpleName(), "Jumble, Tumble, Diamond Fuss");
		addStartNpc(COLLECTOR_GOUPH);
		addTalkId(COLLECTOR_GOUPH, TRADER_REEP, CARRIER_TOROCCO, MINER_MARON, BLACKSMITH_BRUNON, WAREHOUSE_KEEPER_MURDOC, WAREHOUSE_KEEPER_AIRY);
		addKillId(GOBLIN_BRIGAND_LEADER, GOBLIN_BRIGAND_LIEUTENANT, BLADE_BAT);
		registerQuestItems(GOUPHS_CONTRACT, REEPS_CONTRACT, ELVEN_WINE, BRUNONS_DICE, BRUNONS_CONTRACT, AQUAMARINE, CHRYSOBERYL, GEM_BOX, COAL_PIECE, BRUNONS_LETTER, BERRY_TART, BAT_DIAGRAM, STAR_DIAMOND);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		String htmltext = null;
		if (st == null)
		{
			return htmltext;
		}
		switch (event)
		{
			case "30523-04.htm":
			{
				if (st.isCreated())
				{
					st.startQuest();
					st.giveItems(GOUPHS_CONTRACT, 1);
					htmltext = event;
				}
				break;
			}
			case "30555-02.html":
			{
				if (st.isCond(2) && st.hasQuestItems(REEPS_CONTRACT))
				{
					st.takeItems(REEPS_CONTRACT, -1);
					st.giveItems(ELVEN_WINE, 1);
					st.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "30526-02.html":
			{
				if (st.isCond(4) && st.hasQuestItems(BRUNONS_DICE))
				{
					st.takeItems(BRUNONS_DICE, -1);
					st.giveItems(BRUNONS_CONTRACT, 1);
					st.setCond(5, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		final QuestState st = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		if (st == null)
		{
			return htmltext;
		}
		switch (npc.getId())
		{
			case COLLECTOR_GOUPH:
			{
				switch (st.getState())
				{
					case State.CREATED:
					{
						if (talker.getRace() != Race.DWARF)
						{
							htmltext = "30523-01.htm";
						}
						else if (talker.getLevel() < MIN_LVL)
						{
							htmltext = "30523-02.htm";
						}
						else
						{
							htmltext = "30523-03.htm";
						}
						break;
					}
					case State.STARTED:
					{
						switch (st.getCond())
						{
							case 1:
							{
								if (st.hasQuestItems(GOUPHS_CONTRACT))
								{
									htmltext = "30523-05.html";
								}
								break;
							}
							case 2:
							case 3:
							case 4:
							case 5:
							case 6:
							{
								if (hasAtLeastOneQuestItem(talker, REEPS_CONTRACT, ELVEN_WINE, BRUNONS_DICE, BRUNONS_CONTRACT))
								{
									htmltext = "30523-06.html";
								}
								break;
							}
							case 7:
							{
								if (st.hasQuestItems(GEM_BOX))
								{
									st.takeItems(GEM_BOX, -1);
									st.giveItems(COAL_PIECE, 1);
									st.setCond(8, true);
									htmltext = "30523-07.html";
								}
								break;
							}
							case 8:
							case 9:
							case 10:
							case 11:
							{
								if (hasAtLeastOneQuestItem(talker, COAL_PIECE, BRUNONS_LETTER, BERRY_TART, BAT_DIAGRAM))
								{
									htmltext = "30523-08.html";
								}
								break;
							}
							case 12:
							{
								if (st.hasQuestItems(STAR_DIAMOND))
								{
									Q00281_HeadForTheHills.giveNewbieReward(talker);
									talker.sendPacket(new SocialAction(talker.getObjectId(), 3));
									showOnScreenMsg(talker, NpcStringId.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_N_GO_FIND_THE_NEWBIE_GUIDE, 2, 5000);
									
									st.addExpAndSp(34565, 2962);
									st.giveAdena(14666, true);
									
									st.giveItems(SILVERSMITH_HAMMER, 1);
									for (ItemHolder reward : REWARDS)
									{
										st.giveItems(reward);
									}
									
									st.exitQuest(false, true);
									htmltext = "30523-09.html";
								}
								break;
							}
						}
						break;
					}
					case State.COMPLETED:
					{
						htmltext = getAlreadyCompletedMsg(talker);
						break;
					}
				}
				break;
			}
			case TRADER_REEP:
			{
				switch (st.getCond())
				{
					case 1:
					{
						if (st.hasQuestItems(GOUPHS_CONTRACT))
						{
							st.takeItems(GOUPHS_CONTRACT, -1);
							st.giveItems(REEPS_CONTRACT, 1);
							st.setCond(2, true);
							htmltext = "30516-01.html";
						}
						break;
					}
					case 2:
					{
						if (st.hasQuestItems(REEPS_CONTRACT))
						{
							htmltext = "30516-02.html";
						}
						break;
					}
					default:
					{
						if (st.getCond() > 2)
						{
							htmltext = "30516-02.html";
						}
						break;
					}
				}
				break;
			}
			case CARRIER_TOROCCO:
			{
				switch (st.getCond())
				{
					case 2:
					{
						if (st.hasQuestItems(REEPS_CONTRACT))
						{
							htmltext = "30555-01.html";
						}
						break;
					}
					case 3:
					{
						if (st.hasQuestItems(ELVEN_WINE))
						{
							htmltext = "30555-03.html";
						}
						break;
					}
					case 7:
					{
						if (st.hasQuestItems(GEM_BOX))
						{
							htmltext = "30555-04.html";
						}
						break;
					}
					default:
					{
						if (st.isStarted())
						{
							htmltext = "30555-05.html";
						}
						break;
					}
				}
				break;
			}
			case MINER_MARON:
			{
				switch (st.getCond())
				{
					case 3:
					{
						if (st.hasQuestItems(ELVEN_WINE))
						{
							st.takeItems(ELVEN_WINE, -1);
							st.giveItems(BRUNONS_DICE, 1);
							st.setCond(4, true);
							htmltext = "30529-01.html";
						}
						break;
					}
					case 4:
					{
						if (st.hasQuestItems(BRUNONS_DICE))
						{
							htmltext = "30529-02.html";
						}
						break;
					}
					default:
					{
						if (st.getCond() > 4)
						{
							htmltext = "30529-03.html";
						}
						break;
					}
				}
				break;
			}
			case BLACKSMITH_BRUNON:
			{
				switch (st.getCond())
				{
					case 4:
					{
						if (st.hasQuestItems(BRUNONS_DICE))
						{
							htmltext = "30526-01.html";
						}
						break;
					}
					case 5:
					{
						if (st.hasQuestItems(BRUNONS_CONTRACT))
						{
							htmltext = "30526-03.html";
						}
						break;
					}
					case 6:
					{
						if (st.hasQuestItems(BRUNONS_CONTRACT) && (st.getQuestItemsCount(AQUAMARINE) >= MAX_GEM_COUNT) && (st.getQuestItemsCount(CHRYSOBERYL) >= MAX_GEM_COUNT))
						{
							takeItems(talker, -1, BRUNONS_CONTRACT, AQUAMARINE, CHRYSOBERYL);
							st.giveItems(GEM_BOX, 1);
							st.setCond(7, true);
							htmltext = "30526-04.html";
						}
						break;
					}
					case 7:
					{
						if (st.hasQuestItems(GEM_BOX))
						{
							htmltext = "30526-05.html";
						}
						break;
					}
					case 8:
					{
						if (st.hasQuestItems(COAL_PIECE))
						{
							st.takeItems(COAL_PIECE, -1);
							st.giveItems(BRUNONS_LETTER, 1);
							st.setCond(9, true);
							htmltext = "30526-06.html";
						}
						break;
					}
					case 9:
					{
						if (st.hasQuestItems(BRUNONS_LETTER))
						{
							htmltext = "30526-07.html";
						}
						break;
					}
					case 10:
					case 11:
					case 12:
					{
						if (hasAtLeastOneQuestItem(talker, BERRY_TART, BAT_DIAGRAM, STAR_DIAMOND))
						{
							htmltext = "30526-08.html";
						}
						break;
					}
				}
				break;
			}
			case WAREHOUSE_KEEPER_MURDOC:
			{
				switch (st.getCond())
				{
					case 9:
					{
						if (st.hasQuestItems(BRUNONS_LETTER))
						{
							st.takeItems(BRUNONS_LETTER, -1);
							st.giveItems(BERRY_TART, 1);
							st.setCond(10, true);
							htmltext = "30521-01.html";
						}
						break;
					}
					case 10:
					{
						if (st.hasQuestItems(BERRY_TART))
						{
							htmltext = "30521-02.html";
						}
						break;
					}
					case 11:
					case 12:
					{
						htmltext = "30521-03.html";
						break;
					}
				}
				break;
			}
			case WAREHOUSE_KEEPER_AIRY:
			{
				switch (st.getCond())
				{
					case 10:
					{
						if (st.hasQuestItems(BERRY_TART))
						{
							st.takeItems(BERRY_TART, -1);
							st.giveItems(BAT_DIAGRAM, 1);
							st.setCond(11, true);
							htmltext = "30522-01.html";
						}
						break;
					}
					case 11:
					{
						if (st.hasQuestItems(BAT_DIAGRAM))
						{
							htmltext = "30522-02.html";
						}
						break;
					}
					case 12:
					{
						if (st.hasQuestItems(STAR_DIAMOND))
						{
							htmltext = "30522-03.html";
						}
						break;
					}
					default:
					{
						if (st.isStarted())
						{
							htmltext = "30522-04.html";
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = killer.getQuestState(getName());
		if ((st != null) && Util.checkIfInRange(1500, npc, killer, true))
		{
			switch (npc.getId())
			{
				case GOBLIN_BRIGAND_LEADER:
				case GOBLIN_BRIGAND_LIEUTENANT:
				{
					if (st.isCond(5) && st.hasQuestItems(BRUNONS_CONTRACT))
					{
						final double dropChance = GOBLIN_DROP_CHANCES.get(npc.getId());
						boolean playSound = false;
						if (st.giveItemRandomly(npc, AQUAMARINE, 1, MAX_GEM_COUNT, dropChance, false))
						{
							if (st.getQuestItemsCount(CHRYSOBERYL) >= MAX_GEM_COUNT)
							{
								st.setCond(6, true);
								break;
							}
							
							playSound = true;
						}
						if (st.giveItemRandomly(npc, CHRYSOBERYL, 1, MAX_GEM_COUNT, dropChance, false))
						{
							if (st.getQuestItemsCount(AQUAMARINE) >= MAX_GEM_COUNT)
							{
								st.setCond(6, true);
								break;
							}
							
							playSound = true;
						}
						
						if (playSound)
						{
							st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case BLADE_BAT:
				{
					if (st.isCond(11) && st.hasQuestItems(BAT_DIAGRAM))
					{
						if (st.giveItemRandomly(npc, STAR_DIAMOND, 1, 1, 0.2, true))
						{
							st.takeItems(BAT_DIAGRAM, -1);
							st.setCond(12);
						}
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
