/*
 * Copyright (C) 2004-2015 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.npc.NpcBuffers;

import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2TamedBeastInstance;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.util.Util;

/**
 * @author UnAfraid
 */
public class NpcBufferAI implements Runnable
{
	private final L2Npc _npc;
	private final NpcBufferSkillData _skillData;
	
	protected NpcBufferAI(L2Npc npc, NpcBufferSkillData skill)
	{
		_npc = npc;
		_skillData = skill;
	}
	
	@Override
	public void run()
	{
		if ((_npc == null) || !_npc.isVisible() || _npc.isDecayed() || _npc.isDead() || (_skillData == null) || (_skillData.getSkill() == null))
		{
			return;
		}
		
		if ((_npc.getSummoner() == null) || !_npc.getSummoner().isPlayer())
		{
			return;
		}
		
		final L2Skill skill = _skillData.getSkill();
		final L2PcInstance player = _npc.getSummoner().getActingPlayer();
		
		switch (_skillData.getAffectScope())
		{
			case PARTY:
			{
				if (player.isInParty())
				{
					for (L2PcInstance member : player.getParty().getMembers())
					{
						if (Util.checkIfInRange(skill.getAffectRange(), _npc, member, true) && !member.isDead())
						{
							skill.getEffects(player, member);
						}
					}
				}
				else
				{
					if (Util.checkIfInRange(skill.getAffectRange(), _npc, player, true) && !player.isDead())
					{
						skill.getEffects(player, player);
					}
				}
				break;
			}
			case RANGE:
			{
				for (L2Character target : _npc.getKnownList().getKnownCharactersInRadius(skill.getAffectRange()))
				{
					switch (_skillData.getAffectObject())
					{
						case FRIEND:
						{
							if (isFriendly(player, target) && !target.isDead())
							{
								skill.getEffects(target, target);
							}
							break;
						}
						case NOT_FRIEND:
						{
							if (isEnemy(player, target) && !target.isDead())
							{
								// Update PvP status
								if (target.isPlayable())
								{
									player.updatePvPStatus(target);
								}
								skill.getEffects(target, target);
							}
							break;
						}
					}
				}
				break;
			}
		}
		ThreadPoolManager.getInstance().scheduleGeneral(this, _skillData.getDelay());
	}
	
	/**
	 * Verifies if the character is an friend and can be affected by positive effect.
	 * @param player the player
	 * @param target the target
	 * @return {@code true} if target can be affected by positive effect, {@code false} otherwise
	 */
	private boolean isFriendly(L2PcInstance player, L2Character target)
	{
		if (target.isPlayable())
		{
			final L2PcInstance targetPlayer = target.getActingPlayer();
			
			if (player == targetPlayer)
			{
				return true;
			}
			
			if (player.isInPartyWith(targetPlayer))
			{
				return true;
			}
			
			if (player.isInCommandChannelWith(targetPlayer))
			{
				return true;
			}
			
			if (player.isInClanWith(targetPlayer))
			{
				return true;
			}
			
			if (player.isInAllyWith(targetPlayer))
			{
				return true;
			}
			
			if (player.isOnSameSiegeSideWith(targetPlayer))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Verifies if the character is an enemy and can be affected by negative effect.
	 * @param player the player
	 * @param target the target
	 * @return {@code true} if target can be affected by negative effect, {@code false} otherwise
	 */
	private boolean isEnemy(L2PcInstance player, L2Character target)
	{
		if (isFriendly(player, target))
		{
			return false;
		}
		
		if (target instanceof L2TamedBeastInstance)
		{
			return isEnemy(player, ((L2TamedBeastInstance) target).getOwner());
		}
		
		if (target.isMonster())
		{
			return true;
		}
		
		if (target.isPlayable())
		{
			final L2PcInstance targetPlayer = target.getActingPlayer();
			
			if (!isFriendly(player, targetPlayer))
			{
				if (targetPlayer.getPvpFlag() != 0)
				{
					return true;
				}
				
				if (targetPlayer.getKarma() != 0)
				{
					return true;
				}
				
				if (player.isAtWarWith(targetPlayer))
				{
					return true;
				}
				
				if (targetPlayer.isInsideZone(ZoneIdType.PVP))
				{
					return true;
				}
			}
		}
		return false;
	}
}