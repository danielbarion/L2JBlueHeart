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
package handlers.effecthandlers;

import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.stats.Env;

/**
 * Get Agro effect implementation.
 * @author Adry_85
 */
public final class GetAgro extends L2Effect
{
	public GetAgro(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected().isAttackable())
		{
			getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, getEffector());
		}
		
		if (getEffected().isPlayer() && getEffector().isPlayer())
		{
			L2PcInstance effector = getEffector().getActingPlayer();
			L2PcInstance effected = getEffected().getActingPlayer();
			if (!effector.isFriend(effected))
			{
				effected.setTarget(effector);
				effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, effector);
			}
		}
		
		return true;
	}
}
