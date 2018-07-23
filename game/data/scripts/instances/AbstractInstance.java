/*
 * Copyright (C) 2004-2014 L2J DataPack
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
package instances;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import l2r.Config;
import l2r.gameserver.enums.InstanceReenterType;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.L2Summon;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.entity.Instance;
import l2r.gameserver.model.holders.InstanceReenterTimeHolder;
import l2r.gameserver.model.instancezone.InstanceWorld;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;

import ai.npc.AbstractNpcAI;

/**
 * Abstract class for Instances.
 * @author FallenAngel
 */
public abstract class AbstractInstance extends AbstractNpcAI
{
	public AbstractInstance(String name, String desc)
	{
		super(name, desc);
	}
	
	public AbstractInstance(String name)
	{
		super(name, "instances");
	}
	
	protected void enterInstance(L2PcInstance player, InstanceWorld instance, String template, int templateId)
	{
		final InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
		if (world != null)
		{
			if (world.getTemplateId() == templateId)
			{
				onEnterInstance(player, world, false);
				
				final Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
				if (inst.isRemoveBuffEnabled())
				{
					handleRemoveBuffs(player, world);
				}
				return;
			}
			player.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_ANOTHER_INSTANT_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON);
			return;
		}
		
		if (checkConditions(player, templateId))
		{
			instance.setInstanceId(InstanceManager.getInstance().createDynamicInstance(template));
			instance.setTemplateId(templateId);
			instance.setStatus(0);
			InstanceManager.getInstance().addWorld(instance);
			onEnterInstance(player, instance, true);
			
			final Instance inst = InstanceManager.getInstance().getInstance(instance.getInstanceId());
			if (inst.getReenterType() == InstanceReenterType.ON_INSTANCE_ENTER)
			{
				handleReenterTime(instance);
			}
			
			if (inst.isRemoveBuffEnabled())
			{
				handleRemoveBuffs(instance);
			}
			
			if (Config.DEBUG_INSTANCES)
			{
				_log.info("Instance " + inst.getName() + " (" + instance.getTemplateId() + ") has been created by player " + player.getName());
			}
		}
	}
	
	protected void finishInstance(InstanceWorld world)
	{
		finishInstance(world, Config.INSTANCE_FINISH_TIME);
	}
	
	protected void finishInstance(InstanceWorld world, int duration)
	{
		final Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
		
		if (inst.getReenterType() == InstanceReenterType.ON_INSTANCE_FINISH)
		{
			handleReenterTime(world);
		}
		
		if (duration == 0)
		{
			InstanceManager.getInstance().destroyInstance(inst.getId());
		}
		else if (duration > 0)
		{
			inst.setDuration(duration);
			inst.setEmptyDestroyTime(0);
		}
	}
	
	protected void handleReenterTime(InstanceWorld world)
	{
		final Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
		final List<InstanceReenterTimeHolder> reenterData = inst.getReenterData();
		
		long time = -1;
		
		for (InstanceReenterTimeHolder data : reenterData)
		{
			if (data.getTime() > 0)
			{
				time = System.currentTimeMillis() + data.getTime();
				break;
			}
			
			final Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.AM_PM, data.getHour() >= 12 ? 1 : 0);
			calendar.set(Calendar.HOUR, data.getHour());
			calendar.set(Calendar.MINUTE, data.getMinute());
			calendar.set(Calendar.SECOND, 0);
			
			if (calendar.getTimeInMillis() <= System.currentTimeMillis())
			{
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			
			if (data.getDay() != null)
			{
				while (calendar.get(Calendar.DAY_OF_WEEK) != (Math.min(data.getDay().getValue() + 1, 7)))
				{
					calendar.add(Calendar.DAY_OF_MONTH, 1);
				}
			}
			
			if (time == -1)
			{
				time = calendar.getTimeInMillis();
			}
			else if (calendar.getTimeInMillis() < time)
			{
				time = calendar.getTimeInMillis();
			}
		}
		
		if (time > 0)
		{
			setReenterTime(world, time);
		}
	}
	
	protected void handleRemoveBuffs(InstanceWorld world)
	{
		for (int objId : world.getAllowed())
		{
			final L2PcInstance player = L2World.getInstance().getPlayer(objId);
			
			if (player != null)
			{
				handleRemoveBuffs(player, world);
			}
		}
	}
	
	protected abstract void onEnterInstance(L2PcInstance player, InstanceWorld world, boolean firstEntrance);
	
	protected boolean checkConditions(L2PcInstance player, int templateId)
	{
		return checkConditions(player);
	}
	
	protected boolean checkConditions(L2PcInstance player)
	{
		return true;
	}
	
	/**
	 * Spawns group of instance NPC's
	 * @param groupName the name of group from XML definition to spawn
	 * @param instanceId the instance ID
	 * @return list of spawned NPC's
	 */
	protected List<L2Npc> spawnGroup(String groupName, int instanceId)
	{
		return InstanceManager.getInstance().getInstance(instanceId).spawnGroup(groupName);
	}
	
	/**
	 * Sets reenter time for every player in the instance.
	 * @param world the instance
	 * @param time the time in milliseconds
	 */
	protected void setReenterTime(InstanceWorld world, long time)
	{
		for (int objectId : world.getAllowed())
		{
			InstanceManager.getInstance().setInstanceTime(objectId, world.getTemplateId(), time);
			final L2PcInstance player = L2World.getInstance().getPlayer(objectId);
			if ((player != null) && player.isOnline())
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.INSTANT_ZONE_FROM_HERE_S1_S_ENTRY_HAS_BEEN_RESTRICTED).addString(InstanceManager.getInstance().getInstance(world.getInstanceId()).getName()));
			}
		}
		
		if (Config.DEBUG_INSTANCES)
		{
			_log.info("Time restrictions has been set for player in instance ID: " + world.getInstanceId() + " (" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time) + ")");
		}
	}
	
	private void handleRemoveBuffs(L2PcInstance player, InstanceWorld world)
	{
		final Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
		
		switch (inst.getRemoveBuffType())
		{
			case ALL:
			{
				player.stopAllEffectsExceptThoseThatLastThroughDeath();
				
				final L2Summon summon = player.getSummon();
				if (summon != null)
				{
					summon.stopAllEffectsExceptThoseThatLastThroughDeath();
				}
				break;
			}
			case WHITELIST:
			{
				for (L2Effect info : player.getEffectList().getBuffs())
				{
					if (!inst.getBuffExceptionList().contains(info.getSkill().getId()))
					{
						info.getEffected().getEffectList().stopSkillEffects(info.getSkill().getId());
					}
				}
				
				final L2Summon summon = player.getSummon();
				if (summon != null)
				{
					for (L2Effect info : summon.getEffectList().getBuffs())
					{
						if (!inst.getBuffExceptionList().contains(info.getSkill().getId()))
						{
							info.getEffected().getEffectList().stopSkillEffects(info.getSkill().getId());
						}
					}
				}
				break;
			}
			case BLACKLIST:
			{
				for (L2Effect info : player.getEffectList().getBuffs())
				{
					if (inst.getBuffExceptionList().contains(info.getSkill().getId()))
					{
						info.getEffected().getEffectList().stopSkillEffects(info.getSkill().getId());
					}
				}
				
				final L2Summon summon = player.getSummon();
				if (summon != null)
				{
					for (L2Effect info : summon.getEffectList().getBuffs())
					{
						if (inst.getBuffExceptionList().contains(info.getSkill().getId()))
						{
							info.getEffected().getEffectList().stopSkillEffects(info.getSkill().getId());
						}
					}
				}
				break;
			}
		}
	}
}