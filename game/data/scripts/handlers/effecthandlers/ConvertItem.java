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
package handlers.effecthandlers;

import l2r.gameserver.model.Elementals;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.items.L2Weapon;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.InventoryUpdate;
import l2r.gameserver.network.serverpackets.SystemMessage;

/**
 * Convert Item effect.
 * @author Zoey76
 */
public class ConvertItem extends L2Effect
{
	public ConvertItem(Env env, EffectTemplate template)
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
		if ((getEffector() == null) || (getEffected() == null) || getEffected().isAlikeDead() || !getEffected().isPlayer())
		{
			return false;
		}
		
		final L2PcInstance player = getEffected().getActingPlayer();
		if (player.isEnchanting())
		{
			return false;
		}
		
		final L2Weapon weaponItem = player.getActiveWeaponItem();
		if (weaponItem == null)
		{
			return false;
		}
		
		L2ItemInstance wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		if (wpn == null)
		{
			wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		}
		
		if ((wpn == null) || wpn.isAugmented() || (weaponItem.getChangeWeaponId() == 0))
		{
			return false;
		}
		
		final int newItemId = weaponItem.getChangeWeaponId();
		if (newItemId == -1)
		{
			return false;
		}
		
		final int enchantLevel = wpn.getEnchantLevel();
		final Elementals elementals = wpn.getElementals() == null ? null : wpn.getElementals()[0];
		final L2ItemInstance[] unequiped = player.getInventory().unEquipItemInBodySlotAndRecord(wpn.getItem().getBodyPart());
		final InventoryUpdate iu = new InventoryUpdate();
		for (L2ItemInstance item : unequiped)
		{
			iu.addModifiedItem(item);
		}
		player.sendPacket(iu);
		
		if (unequiped.length <= 0)
		{
			return false;
		}
		byte count = 0;
		for (L2ItemInstance item : unequiped)
		{
			if (!(item.getItem() instanceof L2Weapon))
			{
				count++;
				continue;
			}
			
			final SystemMessage sm;
			if (item.getEnchantLevel() > 0)
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED);
				sm.addInt(item.getEnchantLevel());
				sm.addItemName(item);
			}
			else
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISARMED);
				sm.addItemName(item);
			}
			player.sendPacket(sm);
		}
		
		if (count == unequiped.length)
		{
			return false;
		}
		
		final L2ItemInstance destroyItem = player.getInventory().destroyItem("ChangeWeapon", wpn, player, null);
		if (destroyItem == null)
		{
			return false;
		}
		
		final L2ItemInstance newItem = player.getInventory().addItem("ChangeWeapon", newItemId, 1, player, destroyItem);
		if (newItem == null)
		{
			return false;
		}
		
		if ((elementals != null) && (elementals.getElement() != -1) && (elementals.getValue() != -1))
		{
			newItem.setElementAttr(elementals.getElement(), elementals.getValue());
		}
		newItem.setEnchantLevel(enchantLevel);
		player.getInventory().equipItem(newItem);
		
		final SystemMessage msg;
		if (newItem.getEnchantLevel() > 0)
		{
			msg = SystemMessage.getSystemMessage(SystemMessageId.S1_S2_EQUIPPED);
			msg.addInt(newItem.getEnchantLevel());
			msg.addItemName(newItem);
		}
		else
		{
			msg = SystemMessage.getSystemMessage(SystemMessageId.S1_EQUIPPED);
			msg.addItemName(newItem);
		}
		player.sendPacket(msg);
		
		final InventoryUpdate u = new InventoryUpdate();
		u.addRemovedItem(destroyItem);
		u.addItem(newItem);
		player.sendPacket(u);
		
		player.broadcastUserInfo();
		return true;
	}
}
