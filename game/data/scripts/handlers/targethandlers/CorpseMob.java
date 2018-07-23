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
package handlers.targethandlers;

import l2r.Config;
import l2r.gameserver.handler.ITargetTypeHandler;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.L2SkillType;
import l2r.gameserver.model.skills.targets.L2TargetType;
import l2r.gameserver.network.SystemMessageId;

/**
 * @author UnAfraid
 */
public class CorpseMob implements ITargetTypeHandler
{
	@Override
	public L2Object[] getTargetList(L2Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		if ((target == null) || !target.isAttackable() || !target.isDead())
		{
			activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
			return _emptyTargetList;
		}
		
		if ((skill.getSkillType() == L2SkillType.SUMMON) && target.isServitor() && (target.getActingPlayer() != null) && (target.getActingPlayer().getObjectId() == activeChar.getObjectId()))
		{
			return _emptyTargetList;
		}
		
		if ((skill.getSkillType() == L2SkillType.DRAIN) && ((L2Attackable) target).isOldCorpse(activeChar.getActingPlayer(), Config.CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY, true))
		{
			return _emptyTargetList;
		}
		
		return new L2Character[]
		{
			target
		};
	}
	
	@Override
	public Enum<L2TargetType> getTargetType()
	{
		return L2TargetType.CORPSE_MOB;
	}
}
