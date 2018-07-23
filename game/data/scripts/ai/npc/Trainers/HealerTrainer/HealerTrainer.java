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
package ai.npc.Trainers.HealerTrainer;

import java.util.Collection;

import l2r.Config;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.data.xml.impl.SkillTreesData;
import l2r.gameserver.model.L2SkillLearn;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.AcquireSkillType;
import l2r.gameserver.model.holders.ItemHolder;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.AcquireSkillList;

import ai.npc.AbstractNpcAI;

/**
 * Trainer healers AI.
 * @author Zoey76
 */
public final class HealerTrainer extends AbstractNpcAI
{
	// NPC
	// @formatter:off
	private static final int[] HEALER_TRAINERS =
	{
		30022, 30030, 30032, 30036, 30067, 30068, 30116, 30117, 30118, 30119,
		30144, 30145, 30188, 30194, 30293, 30330, 30375, 30377, 30464, 30473,
		30476, 30680, 30701, 30720, 30721, 30858, 30859, 30860, 30861, 30864,
		30906, 30908, 30912, 31280, 31281, 31287, 31329, 31330, 31335, 31969,
		31970, 31976, 32155, 32162
	};
	// @formatter:on
	// Misc
	private static final int MIN_LEVEL = 76;
	private static final int MIN_CLASS_LEVEL = 3;
	
	public HealerTrainer()
	{
		super(HealerTrainer.class.getSimpleName(), "ai/npc/Trainers");
		addStartNpc(HEALER_TRAINERS);
		addTalkId(HEALER_TRAINERS);
		addFirstTalkId(HEALER_TRAINERS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "30864.html":
			case "30864-1.html":
			{
				htmltext = event;
				break;
			}
			case "SkillTransfer":
			{
				htmltext = "main.html";
				break;
			}
			case "SkillTransferLearn":
			{
				if (!npc.getTemplate().canTeach(player.getClassId()))
				{
					htmltext = npc.getId() + "-noteach.html";
					break;
				}
				
				if ((player.getLevel() < MIN_LEVEL) || (player.getClassId().level() < MIN_CLASS_LEVEL))
				{
					htmltext = "learn-lowlevel.html";
					break;
				}
				
				final AcquireSkillList asl = new AcquireSkillList(AcquireSkillType.TRANSFER);
				int count = 0;
				for (L2SkillLearn skillLearn : SkillTreesData.getInstance().getAvailableTransferSkills(player))
				{
					if (SkillData.getInstance().getInfo(skillLearn.getSkillId(), skillLearn.getSkillLevel()) != null)
					{
						count++;
						asl.addSkill(skillLearn.getSkillId(), skillLearn.getSkillLevel(), skillLearn.getSkillLevel(), skillLearn.getLevelUpSp(), 0);
					}
				}
				
				if (count > 0)
				{
					player.sendPacket(asl);
				}
				else
				{
					player.sendPacket(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);
				}
				break;
			}
			case "SkillTransferCleanse":
			{
				if (!npc.getTemplate().canTeach(player.getClassId()))
				{
					htmltext = "cleanse-no.html";
					break;
				}
				
				if ((player.getLevel() < MIN_LEVEL) || (player.getClassId().level() < MIN_CLASS_LEVEL))
				{
					htmltext = "cleanse-no.html";
					break;
				}
				
				if (player.getAdena() < Config.FEE_DELETE_TRANSFER_SKILLS)
				{
					player.sendPacket(SystemMessageId.CANNOT_RESET_SKILL_LINK_BECAUSE_NOT_ENOUGH_ADENA);
					break;
				}
				
				if (hasTransferSkillItems(player))
				{
					// Come back when you have used all transfer skill items for this class.
					htmltext = "cleanse-no_skills.html";
				}
				else
				{
					boolean hasSkills = false;
					final Collection<L2SkillLearn> skills = SkillTreesData.getInstance().getTransferSkillTree(player.getClassId()).values();
					for (L2SkillLearn skillLearn : skills)
					{
						final L2Skill skill = player.getKnownSkill(skillLearn.getSkillId());
						if (skill != null)
						{
							player.removeSkill(skill);
							for (ItemHolder item : skillLearn.getRequiredItems())
							{
								player.addItem("Cleanse", item.getId(), item.getCount(), npc, true);
							}
							hasSkills = true;
						}
					}
					
					// Adena gets reduced once.
					if (hasSkills)
					{
						player.reduceAdena("Cleanse", Config.FEE_DELETE_TRANSFER_SKILLS, npc, true);
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	/**
	 * Verify if the player has the required item.
	 * @param player the player to verify
	 * @return {@code true} if the player has the item for the current class, {@code false} otherwise
	 */
	private static boolean hasTransferSkillItems(L2PcInstance player)
	{
		int itemId;
		switch (player.getClassId())
		{
			case cardinal:
			{
				itemId = 15307;
				break;
			}
			case evaSaint:
			{
				itemId = 15308;
				break;
			}
			case shillienSaint:
			{
				itemId = 15309;
				break;
			}
			default:
			{
				itemId = -1;
			}
		}
		return (player.getInventory().getInventoryItemCount(itemId, -1) > 0);
	}
}
