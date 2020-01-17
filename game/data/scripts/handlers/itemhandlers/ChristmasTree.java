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
package handlers.itemhandlers;

import l2r.gameserver.data.sql.NpcTable;
import l2r.gameserver.handler.IItemHandler;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.L2Spawn;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.SystemMessageId;

public class ChristmasTree implements IItemHandler
{
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return false;
		}
		
		L2PcInstance activeChar = playable.getActingPlayer();
		L2NpcTemplate template1 = null;
		
		switch (item.getId())
		{
			case 5560:
				template1 = NpcTable.getInstance().getTemplate(13006);
				break;
			case 5561:
				template1 = NpcTable.getInstance().getTemplate(13007);
				break;
		}
		
		if (template1 == null)
		{
			return false;
		}
		
		L2Object target = activeChar.getTarget();
		if (target == null)
		{
			target = activeChar;
		}
		
		try
		{
			L2Spawn spawn = new L2Spawn(template1);
			spawn.setX(target.getX());
			spawn.setY(target.getY());
			spawn.setZ(target.getZ());
			spawn.setInstanceId(activeChar.getInstanceId());
			L2Npc npc = spawn.spawnOne(false);
			npc.setSummoner(activeChar);
			
			activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false);
			
			activeChar.sendMessage("Created " + template1.getName() + " at x: " + spawn.getX() + " y: " + spawn.getY() + " z: " + spawn.getZ());
			return true;
		}
		catch (Exception e)
		{
			activeChar.sendPacket(SystemMessageId.TARGET_CANT_FOUND);
			return false;
		}
	}
}
