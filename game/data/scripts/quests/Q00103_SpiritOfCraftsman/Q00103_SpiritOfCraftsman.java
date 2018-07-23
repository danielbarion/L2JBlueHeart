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
package quests.Q00103_SpiritOfCraftsman;

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
 * Spirit of Craftsman (103)
 * @author janiko
 */
public final class Q00103_SpiritOfCraftsman extends Quest
{
	// NPC
	private static final int BLACKSMITH_KAROYD = 30307;
	private static final int CECON = 30132;
	private static final int HARNE = 30144;
	// Items
	private static final int KAROYDS_LETTER = 968;
	private static final int CECKTINONS_VOUCHER_1 = 969;
	private static final int CECKTINONS_VOUCHER_2 = 970;
	private static final int SOUL_CATCHER = 971;
	private static final int PRESERVE_OIL = 972;
	private static final int ZOMBIE_HEAD = 973;
	private static final int STEELBENDERS_HEAD = 974;
	private static final int BONE_FRAGMENT = 1107;
	// Monsters
	private static final int MARSH_ZOMBIE = 20015;
	private static final int DOOM_SOLDIER = 20455;
	private static final int SKELETON_HUNTER = 20517;
	private static final int SKELETON_HUNTER_ARCHER = 20518;
	// Rewards
	private static final int BLOODSABER = 975;
	private static final ItemHolder[] REWARDS =
	{
		new ItemHolder(1060, 100), // Lesser Healing Potion
		new ItemHolder(4412, 10), // Echo Crystal - Theme of Battle
		new ItemHolder(4413, 10), // Echo Crystal - Theme of Love
		new ItemHolder(4414, 10), // Echo Crystal - Theme of Solitude
		new ItemHolder(4415, 10), // Echo Crystal - Theme of Feast
		new ItemHolder(4416, 10), // Echo Crystal - Theme of Celebration
	};
	// Misc
	private static final int MIN_LVL = 10;
	
	public Q00103_SpiritOfCraftsman()
	{
		super(103, Q00103_SpiritOfCraftsman.class.getSimpleName(), "Spirit of Craftsman");
		addStartNpc(BLACKSMITH_KAROYD);
		addTalkId(BLACKSMITH_KAROYD, CECON, HARNE);
		addKillId(MARSH_ZOMBIE, DOOM_SOLDIER, SKELETON_HUNTER, SKELETON_HUNTER_ARCHER);
		registerQuestItems(KAROYDS_LETTER, CECKTINONS_VOUCHER_1, CECKTINONS_VOUCHER_2, SOUL_CATCHER, PRESERVE_OIL, ZOMBIE_HEAD, STEELBENDERS_HEAD, BONE_FRAGMENT);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs == null)
		{
			return htmltext;
		}
		switch (event)
		{
			case "30307-04.htm":
			{
				htmltext = event;
				break;
			}
			case "30307-05.htm":
			{
				if (qs.isCreated())
				{
					qs.startQuest();
					giveItems(player, KAROYDS_LETTER, 1);
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
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		switch (npc.getId())
		{
			case BLACKSMITH_KAROYD:
			{
				switch (qs.getState())
				{
					case State.CREATED:
					{
						if (talker.getRace() != Race.DARK_ELF)
						{
							htmltext = "30307-01.htm";
						}
						else if (talker.getLevel() < MIN_LVL)
						{
							htmltext = "30307-02.htm";
						}
						else
						{
							htmltext = "30307-03.htm";
						}
						break;
					}
					case State.STARTED:
					{
						switch (qs.getCond())
						{
							case 1:
							case 2:
							case 3:
							case 4:
							case 5:
							case 6:
							case 7:
							{
								if (hasAtLeastOneQuestItem(talker, KAROYDS_LETTER, CECKTINONS_VOUCHER_1, CECKTINONS_VOUCHER_2))
								{
									htmltext = "30307-06.html";
								}
								break;
							}
							case 8:
							{
								if (hasQuestItems(talker, STEELBENDERS_HEAD))
								{
									Q00281_HeadForTheHills.giveNewbieReward(talker);
									talker.sendPacket(new SocialAction(talker.getObjectId(), 3));
									showOnScreenMsg(talker, NpcStringId.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_N_GO_FIND_THE_NEWBIE_GUIDE, 2, 5000);
									
									addExpAndSp(talker, 46663, 3999);
									giveAdena(talker, 19799, true);
									
									giveItems(talker, BLOODSABER, 1);
									for (ItemHolder reward : REWARDS)
									{
										giveItems(talker, reward);
									}
									
									qs.exitQuest(false, true);
									htmltext = "30307-07.html";
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
			case CECON:
			{
				if (qs.isStarted())
				{
					switch (qs.getCond())
					{
						case 1:
						{
							if (hasQuestItems(talker, KAROYDS_LETTER))
							{
								qs.setCond(2, true);
								takeItems(talker, KAROYDS_LETTER, 1);
								giveItems(talker, CECKTINONS_VOUCHER_1, 1);
								htmltext = "30132-01.html";
							}
							break;
						}
						case 2:
						case 3:
						case 4:
						{
							if (hasAtLeastOneQuestItem(talker, CECKTINONS_VOUCHER_1, CECKTINONS_VOUCHER_2))
							{
								htmltext = "30132-02.html";
							}
							break;
						}
						case 5:
						{
							if (hasQuestItems(talker, SOUL_CATCHER))
							{
								qs.setCond(6, true);
								takeItems(talker, SOUL_CATCHER, 1);
								giveItems(talker, PRESERVE_OIL, 1);
								htmltext = "30132-03.html";
							}
							break;
						}
						case 6:
						{
							if (hasQuestItems(talker, PRESERVE_OIL))
							{
								htmltext = "30132-04.html";
							}
							break;
						}
						case 7:
						{
							if (hasQuestItems(talker, ZOMBIE_HEAD))
							{
								qs.setCond(8, true);
								takeItems(talker, ZOMBIE_HEAD, 1);
								giveItems(talker, STEELBENDERS_HEAD, 1);
								htmltext = "30132-05.html";
							}
							break;
						}
						case 8:
						{
							if (hasQuestItems(talker, STEELBENDERS_HEAD))
							{
								htmltext = "30132-06.html";
							}
							break;
						}
					}
				}
				break;
			}
			case HARNE:
			{
				if (qs.isStarted())
				{
					switch (qs.getCond())
					{
						case 2:
						{
							if (hasQuestItems(talker, CECKTINONS_VOUCHER_1))
							{
								qs.setCond(3, true);
								takeItems(talker, CECKTINONS_VOUCHER_1, 1);
								giveItems(talker, CECKTINONS_VOUCHER_2, 1);
								htmltext = "30144-01.html";
							}
							break;
						}
						case 3:
						case 4:
						{
							if (hasQuestItems(talker, CECKTINONS_VOUCHER_2) && (getQuestItemsCount(talker, BONE_FRAGMENT) >= 10))
							{
								qs.setCond(5, true);
								takeItems(talker, CECKTINONS_VOUCHER_2, 1);
								takeItems(talker, BONE_FRAGMENT, 10);
								giveItems(talker, SOUL_CATCHER, 1);
								htmltext = "30144-03.html";
							}
							else
							{
								htmltext = "30144-02.html";
							}
							break;
						}
						case 5:
						{
							if (hasQuestItems(talker, SOUL_CATCHER))
							{
								htmltext = "30144-04.html";
							}
							break;
						}
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
		switch (npc.getId())
		{
			case MARSH_ZOMBIE:
			{
				final QuestState qs = getQuestState(killer, false);
				if ((qs != null) && qs.isCond(6) && Util.checkIfInRange(1500, npc, killer, true))
				{
					if (hasQuestItems(killer, PRESERVE_OIL))
					{
						if (giveItemRandomly(killer, npc, ZOMBIE_HEAD, 1, 1, 0.5, true))
						{
							takeItems(killer, PRESERVE_OIL, -1);
							qs.setCond(7);
						}
					}
				}
				break;
			}
			case DOOM_SOLDIER:
			case SKELETON_HUNTER:
			case SKELETON_HUNTER_ARCHER:
			{
				final QuestState qs = getRandomPartyMemberState(killer, 3, 3, npc);
				if ((qs != null) && giveItemRandomly(qs.getPlayer(), npc, BONE_FRAGMENT, 1, 10, 1.0, true))
				{
					qs.setCond(4);
				}
				break;
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public boolean checkPartyMember(QuestState qs, L2Npc npc)
	{
		return hasQuestItems(qs.getPlayer(), CECKTINONS_VOUCHER_2) && (getQuestItemsCount(qs.getPlayer(), BONE_FRAGMENT) < 10);
	}
}
