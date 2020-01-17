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
package ai.group_template;

import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.NpcStringId;

import ai.npc.AbstractNpcAI;

/**
 * Turek Orcs AI - flee and return with assistance
 * @author GKR
 */

public final class TurekOrcs extends AbstractNpcAI
{
	// NPC's
	private static final int[] MOBS =
	{
		20494, // Turek War Hound
		20495, // Turek Orc Warlord
		20497, // Turek Orc Skirmisher
		20498, // Turek Orc Supplier
		20499, // Turek Orc Footman
		20500, // Turek Orc Sentinel
	};
	
	public TurekOrcs()
	{
		super(TurekOrcs.class.getSimpleName(), "ai/group_template");
		addAttackId(MOBS);
		addEventReceivedId(MOBS);
		addMoveFinishedId(MOBS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("checkState") && !npc.isDead() && (npc.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK))
		{
			if ((npc.getCurrentHp() > (npc.getMaxHp() * 0.7)) && (npc.getVariables().getInt("state") == 2))
			{
				npc.getVariables().set("state", 3);
				((L2Attackable) npc).returnHome();
			}
			else
			{
				npc.getVariables().remove("state");
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (!npc.getVariables().hasVariable("isHit"))
		{
			npc.getVariables().set("isHit", 1);
		}
		else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.5)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.3)) && (attacker.getCurrentHp() > (attacker.getMaxHp() * 0.25)) && npc.hasAIValue("fleeX") && npc.hasAIValue("fleeY") && npc.hasAIValue("fleeZ") && (npc.getVariables().getInt("state") == 0) && (getRandom(100) < 10))
		{
			// Say and flee
			broadcastNpcSay(npc, 0, NpcStringId.getNpcStringId(getRandom(1000007, 1000027)));
			npc.disableCoreAI(true); // to avoid attacking behaviour, while flee
			npc.setIsRunning(true);
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(npc.getAIValue("fleeX"), npc.getAIValue("fleeY"), npc.getAIValue("fleeZ")));
			npc.getVariables().set("state", 1);
			npc.getVariables().set("attacker", attacker.getObjectId());
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onEventReceived(String eventName, L2Npc sender, L2Npc receiver, L2Object reference)
	{
		if (eventName.equals("WARNING") && !receiver.isDead() && (receiver.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK) && (reference != null) && (reference.getActingPlayer() != null) && !reference.getActingPlayer().isDead())
		{
			receiver.getVariables().set("state", 3);
			receiver.setIsRunning(true);
			((L2Attackable) receiver).addDamageHate(reference.getActingPlayer(), 0, 99999);
			receiver.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, reference.getActingPlayer());
		}
		return null;
	}
	
	@Override
	public void onMoveFinished(L2Npc npc)
	{
		// NPC reaches flee point
		if (npc.getVariables().getInt("state") == 1)
		{
			if ((npc.getX() == npc.getAIValue("fleeX")) && (npc.getY() == npc.getAIValue("fleeY")))
			{
				npc.disableCoreAI(false);
				startQuestTimer("checkState", 15000, npc, null);
				npc.getVariables().set("state", 2);
				npc.broadcastEvent("WARNING", 400, L2World.getInstance().getPlayer(npc.getVariables().getInt("attacker")));
			}
			else
			{
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(npc.getAIValue("fleeX"), npc.getAIValue("fleeY"), npc.getAIValue("fleeZ")));
			}
		}
		else if ((npc.getVariables().getInt("state") == 3) && npc.staysInSpawnLoc())
		{
			npc.disableCoreAI(false);
			npc.getVariables().remove("state");
		}
	}
}
