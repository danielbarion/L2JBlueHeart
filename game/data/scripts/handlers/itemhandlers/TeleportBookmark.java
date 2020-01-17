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

import l2r.gameserver.handler.IItemHandler;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;

/**
 * Teleport Bookmark Slot Handler
 * @author ShanSoft
 */
public class TeleportBookmark implements IItemHandler
{
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return false;
		}
		
		L2PcInstance player = playable.getActingPlayer();
		
		if (player.getBookMarkSlot() >= 9)
		{
			player.sendPacket(SystemMessageId.YOUR_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_REACHED_ITS_MAXIMUM_LIMIT);
			return false;
		}
		
		player.destroyItem("Consume", item.getObjectId(), 1, null, false);
		
		player.setBookMarkSlot(player.getBookMarkSlot() + 3);
		player.sendPacket(SystemMessageId.THE_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_BEEN_INCREASED);
		
		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
		sm.addItemName(item.getId());
		player.sendPacket(sm);
		return true;
	}
}
