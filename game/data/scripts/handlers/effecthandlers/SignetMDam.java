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

/**
 * @author Forsaiken
 */
package handlers.effecthandlers;

import java.util.ArrayList;
import java.util.List;

import l2r.gameserver.data.sql.NpcTable;
import l2r.gameserver.enums.CtrlEvent;
import l2r.gameserver.enums.ShotType;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2EffectPointInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.l2skills.L2SkillSignetCasttime;
import l2r.gameserver.model.skills.targets.L2TargetType;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.model.stats.Formulas;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.MagicSkillLaunched;

public class SignetMDam extends L2Effect
{
	private L2EffectPointInstance _actor;
	private boolean _srcInArena;
	
	public SignetMDam(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.SIGNET_GROUND;
	}
	
	@Override
	public boolean onStart()
	{
		L2NpcTemplate template;
		if (getSkill() instanceof L2SkillSignetCasttime)
		{
			template = NpcTable.getInstance().getTemplate(getSkill().getNpcId());
		}
		else
		{
			return false;
		}
		
		final L2EffectPointInstance effectPoint = new L2EffectPointInstance(template, getEffector());
		effectPoint.setCurrentHp(effectPoint.getMaxHp());
		effectPoint.setCurrentMp(effectPoint.getMaxMp());
		
		int x = getEffector().getX();
		int y = getEffector().getY();
		int z = getEffector().getZ();
		
		if (getEffector().isPlayer() && (getSkill().getTargetType() == L2TargetType.GROUND))
		{
			final Location wordPosition = getEffector().getActingPlayer().getCurrentSkillWorldPosition();
			
			if (wordPosition != null)
			{
				x = wordPosition.getX();
				y = wordPosition.getY();
				z = wordPosition.getZ();
			}
		}
		effectPoint.setIsInvul(true);
		effectPoint.spawnMe(x, y, z);
		
		_actor = effectPoint;
		_srcInArena = (getEffector().isInsideZone(ZoneIdType.PVP) && !getEffector().isInsideZone(ZoneIdType.SIEGE));
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		int mpConsume = getSkill().getMpConsume();
		
		final L2PcInstance activeChar = getEffector().getActingPlayer();
		
		activeChar.rechargeShots(getSkill().useSoulShot(), getSkill().useSpiritShot());
		
		boolean sps = getSkill().useSpiritShot() && getEffector().isChargedShot(ShotType.SPIRITSHOTS);
		boolean bss = getSkill().useSpiritShot() && getEffector().isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		
		List<L2Character> targets = new ArrayList<>();
		
		for (L2Character cha : _actor.getKnownList().getKnownCharactersInRadius(getSkill().getAffectRange()))
		{
			if ((cha == null) || (cha == activeChar))
			{
				continue;
			}
			
			if (cha.isAttackable() || cha.isPlayable())
			{
				if (cha.isAlikeDead())
				{
					continue;
				}
				
				if (getSkill().isOffensive() && !L2Skill.checkForAreaOffensiveSkills(getEffector(), cha, getSkill(), _srcInArena))
				{
					continue;
				}
				
				if (mpConsume > activeChar.getCurrentMp())
				{
					activeChar.sendPacket(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
					return false;
				}
				
				activeChar.reduceCurrentMp(mpConsume);
				if (cha.isPlayable())
				{
					if (activeChar.canAttackCharacter(cha))
					{
						targets.add(cha);
						activeChar.updatePvPStatus(cha);
					}
				}
				else
				{
					targets.add(cha);
				}
			}
		}
		
		if (!targets.isEmpty())
		{
			activeChar.broadcastPacket(new MagicSkillLaunched(activeChar, getSkill().getId(), getSkill().getLevel(), targets.toArray(new L2Character[targets.size()])));
			for (L2Character target : targets)
			{
				final boolean mcrit = Formulas.calcMCrit(activeChar.getMCriticalHit(target, getSkill()));
				final byte shld = Formulas.calcShldUse(activeChar, target, getSkill());
				final int mdam = (int) Formulas.calcMagicDam(activeChar, target, getSkill(), shld, sps, bss, mcrit);
				
				if (target.isSummon())
				{
					target.broadcastStatusUpdate();
				}
				
				if (mdam > 0)
				{
					if (!target.isRaid() && Formulas.calcAtkBreak(target, mdam))
					{
						target.breakAttack();
						target.breakCast();
					}
					activeChar.sendDamageMessage(target, mdam, mcrit, false, false);
					target.reduceCurrentHp(mdam, activeChar, getSkill());
				}
				target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, activeChar);
			}
		}
		activeChar.setChargedShot(bss ? ShotType.BLESSED_SPIRITSHOTS : ShotType.SPIRITSHOTS, false);
		return true;
	}
	
	@Override
	public void onExit()
	{
		if (_actor != null)
		{
			_actor.deleteMe();
		}
	}
}
