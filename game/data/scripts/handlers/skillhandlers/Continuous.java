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
package handlers.skillhandlers;

import java.util.List;

import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.enums.CtrlEvent;
import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.enums.ShotType;
import l2r.gameserver.handler.ISkillHandler;
import l2r.gameserver.instancemanager.DuelManager;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Summon;
import l2r.gameserver.model.actor.instance.L2ClanHallManagerInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.L2SkillType;
import l2r.gameserver.model.skills.targets.L2TargetType;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.model.stats.Formulas;
import l2r.gameserver.network.SystemMessageId;

/**
 * @version $Revision: 1.1.2.2.2.9 $ $Date: 2005/04/03 15:55:04 $
 */
public class Continuous implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.BUFF,
		L2SkillType.DEBUFF,
		L2SkillType.DOT,
		L2SkillType.MDOT,
		L2SkillType.POISON,
		L2SkillType.BLEED,
		L2SkillType.FEAR,
		L2SkillType.CONT,
		L2SkillType.AGGDEBUFF,
		L2SkillType.FUSION
	};
	
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		boolean acted = true;
		
		L2PcInstance player = null;
		if (activeChar.isPlayer())
		{
			player = activeChar.getActingPlayer();
		}
		
		if (skill.getEffectId() != 0)
		{
			L2Skill sk = SkillData.getInstance().getInfo(skill.getEffectId(), skill.getEffectLvl() == 0 ? 1 : skill.getEffectLvl());
			
			if (sk != null)
			{
				skill = sk;
			}
		}
		
		boolean ss = skill.useSoulShot() && activeChar.isChargedShot(ShotType.SOULSHOTS);
		boolean sps = skill.useSpiritShot() && activeChar.isChargedShot(ShotType.SPIRITSHOTS);
		boolean bss = skill.useSpiritShot() && activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		
		for (L2Object obj : targets)
		{
			if (!obj.isCharacter())
			{
				continue;
			}
			
			L2Character target = (L2Character) obj;
			byte shld = 0;
			
			if (Formulas.calcSkillReflect(target, skill) == Formulas.SKILL_REFLECT_SUCCEED)
			{
				target = activeChar;
			}
			
			// Player holding a cursed weapon can't be buffed and can't buff
			if ((skill.getSkillType() == L2SkillType.BUFF) && !(activeChar instanceof L2ClanHallManagerInstance))
			{
				if (target != activeChar)
				{
					if (target.isPlayer())
					{
						L2PcInstance trg = target.getActingPlayer();
						if (trg.isCursedWeaponEquipped() || trg.isInvul())
						{
							continue;
						}
						else if (trg.getBlockCheckerArena() != -1)
						{
							continue;
						}
					}
					else if ((player != null) && player.isCursedWeaponEquipped())
					{
						continue;
					}
				}
			}
			
			L2PcInstance trg = null;
			if (target.isPlayer())
			{
				trg = target.getActingPlayer();
			}
			
			// Bad buff shield protection
			if ((skill.getSkillType() == L2SkillType.BUFF) && activeChar.isPlayer())
			{
				if ((target != activeChar) && (trg != null) && trg.getVarB("noBuff"))
				{
					if (!trg.isInSameParty(player) && !trg.isInSameAlly(player) && !trg.isInSameClan(player) && !trg.isInSameChannel(player) && !trg.isInvul())
					{
						continue;
					}
				}
			}
			
			if (skill.isOffensive() || skill.isDebuff())
			{
				shld = Formulas.calcShldUse(activeChar, target, skill);
				acted = Formulas.calcSkillSuccess(activeChar, target, skill, shld, ss, sps, bss);
			}
			
			if (acted)
			{
				if (skill.isToggle())
				{
					List<L2Effect> effects = target.getEffectList().getEffects();
					if (effects != null)
					{
						for (L2Effect e : effects)
						{
							if (e != null)
							{
								if (e.getSkill().getId() == skill.getId())
								{
									e.exit();
									return;
								}
							}
						}
					}
				}
				
				// if this is a debuff let the duel manager know about it
				// so the debuff can be removed after the duel
				// (player & target must be in the same duel)
				if (target.isPlayer() && target.getActingPlayer().isInDuel() && ((skill.getSkillType() == L2SkillType.DEBUFF) || (skill.getSkillType() == L2SkillType.BUFF)) && (player != null) && (player.getDuelId() == target.getActingPlayer().getDuelId()))
				{
					DuelManager dm = DuelManager.getInstance();
					for (L2Effect buff : skill.getEffects(activeChar, target, new Env(shld, ss, sps, bss)))
					{
						if (buff != null)
						{
							dm.onBuff(target.getActingPlayer(), buff);
						}
					}
				}
				else
				{
					L2Effect[] effects = skill.getEffects(activeChar, target, new Env(shld, ss, sps, bss));
					L2Summon summon = target.getSummon();
					if ((summon != null) && (summon != activeChar) && summon.isServitor() && (effects.length > 0))
					{
						if (effects[0].canBeStolen() || skill.isHeroSkill() || skill.isStatic())
						{
							skill.getEffects(activeChar, target.getSummon(), new Env(shld, ss, sps, bss));
						}
						// vGodFather: some extra implementation most of target type one skills must take effect on servitors too
						else if (((skill.getTargetType() == L2TargetType.ONE) || ((skill.getTargetType() == L2TargetType.SELF) && !skill.isToggle())) && !skill.isDebuff())
						{
							skill.getEffects(activeChar, target.getSummon(), new Env(shld, ss, sps, bss));
						}
					}
				}
				
				if (skill.getSkillType() == L2SkillType.AGGDEBUFF)
				{
					if (target.isAttackable())
					{
						try
						{
							target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, (int) skill.getPower());
						}
						catch (Exception e)
						{
							_log.warn("Logger: notifyEvent failed (Continuous) Report this to team. ");
						}
					}
					else if (target.isPlayable())
					{
						if (target.getTarget() == activeChar)
						{
							target.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, activeChar);
						}
						else
						{
							target.setTarget(activeChar);
						}
					}
				}
			}
			else
			{
				activeChar.sendPacket(SystemMessageId.ATTACK_FAILED);
			}
			
			// Possibility of a lethal strike (Banish Undead, Banish Seraph, Turn Undead)
			Formulas.calcLethalHit(activeChar, target, skill);
		}
		
		// self Effect :]
		if (skill.hasSelfEffects())
		{
			final L2Effect effect = activeChar.getFirstEffect(skill.getId());
			if ((effect != null) && effect.isSelfEffect())
			{
				// Replace old effect with new one.
				effect.exit();
			}
			skill.getEffectsSelf(activeChar);
		}
		
		activeChar.setChargedShot(bss ? ShotType.BLESSED_SPIRITSHOTS : ShotType.SPIRITSHOTS, false);
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
