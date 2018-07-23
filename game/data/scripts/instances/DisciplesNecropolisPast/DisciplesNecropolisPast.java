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
package instances.DisciplesNecropolisPast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.SkillHolder;
import l2r.gameserver.model.instancezone.InstanceWorld;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.ExStartScenePlayer;
import l2r.gameserver.network.serverpackets.NpcSay;
import l2r.gameserver.util.Util;

import instances.AbstractInstance;
import quests.Q00196_SevenSignsSealOfTheEmperor.Q00196_SevenSignsSealOfTheEmperor;

/**
 * Disciple's Necropolis Past instance zone.
 * @author Adry_85
 */
public final class DisciplesNecropolisPast extends AbstractInstance
{
	protected class DNPWorld extends InstanceWorld
	{
		protected final List<L2Npc> anakimGroup = new ArrayList<>();
		protected final List<L2Npc> lilithGroup = new ArrayList<>();
		
		protected final List<L2Npc> seals = new CopyOnWriteArrayList<>();
		
		protected L2Npc _lilith = null;
		protected L2Npc _anakim = null;
		protected int countKill = 0;
	}
	
	// NPCs
	private static final int SEAL_DEVICE = 27384;
	private static final int PROMISE_OF_MAMMON = 32585;
	private static final int SHUNAIMAN = 32586;
	private static final int LEON = 32587;
	private static final int DISCIPLES_GATEKEEPER = 32657;
	private static final int LILITH = 32715;
	private static final int LILITHS_STEWARD = 32716;
	private static final int LILITHS_ELITE = 32717;
	private static final int ANAKIM = 32718;
	private static final int ANAKIMS_GUARDIAN = 32719;
	private static final int ANAKIMS_GUARD = 32720;
	private static final int ANAKIMS_EXECUTOR = 32721;
	private static final int LILIM_BUTCHER = 27371;
	private static final int LILIM_MAGUS = 27372;
	private static final int LILIM_KNIGHT_ERRANT = 27373;
	private static final int SHILENS_EVIL_THOUGHTS1 = 27374;
	private static final int SHILENS_EVIL_THOUGHTS2 = 27375;
	private static final int LILIM_KNIGHT = 27376;
	private static final int LILIM_SLAYER = 27377;
	private static final int LILIM_GREAT_MAGUS = 27378;
	private static final int LILIM_GUARD_KNIGHT = 27379;
	// Items
	private static final int SACRED_SWORD_OF_EINHASAD = 15310;
	private static final int SEAL_OF_BINDING = 13846;
	// Skills
	private static final SkillHolder SEAL_ISOLATION = new SkillHolder(5980, 3);
	private static final Map<Integer, SkillHolder> SKILLS = new HashMap<>();
	
	static
	{
		SKILLS.put(32715, new SkillHolder(6187, 1)); // Presentation - Lilith Battle
		SKILLS.put(32716, new SkillHolder(6188, 1)); // Presentation - Lilith's Steward Battle1
		SKILLS.put(32717, new SkillHolder(6190, 1)); // Presentation - Lilith's Bodyguards Battle1
		SKILLS.put(32718, new SkillHolder(6191, 1)); // Presentation - Anakim Battle
		SKILLS.put(32719, new SkillHolder(6192, 1)); // Presentation - Anakim's Guardian Battle1
		SKILLS.put(32720, new SkillHolder(6194, 1)); // Presentation - Anakim's Guard Battle
		SKILLS.put(32721, new SkillHolder(6195, 1)); // Presentation - Anakim's Executor Battle
	}
	
	// Locations
	private static final Location ENTER = new Location(-89554, 216078, -7488, 0, 0);
	private static final Location EXIT = new Location(171895, -17501, -4903, 0, 0);
	// NpcStringId
	private static final NpcStringId[] LILITH_SHOUT =
	{
		NpcStringId.HOW_DARE_YOU_TRY_TO_CONTEND_AGAINST_ME_IN_STRENGTH_RIDICULOUS,
		NpcStringId.ANAKIM_IN_THE_NAME_OF_GREAT_SHILIEN_I_WILL_CUT_YOUR_THROAT,
		NpcStringId.YOU_CANNOT_BE_THE_MATCH_OF_LILITH_I_LL_TEACH_YOU_A_LESSON
	};
	// Misc
	private static final int TEMPLATE_ID = 112;
	private static final int DOOR_1 = 17240102;
	private static final int DOOR_2 = 17240104;
	private static final int DOOR_3 = 17240106;
	private static final int DOOR_4 = 17240108;
	private static final int DOOR_5 = 17240110;
	private static final int DISCIPLES_NECROPOLIS_DOOR = 17240111;
	private static final Map<Integer, Location> LILITH_SPAWN = new HashMap<>();
	private static final Map<Integer, Location> ANAKIM_SPAWN = new HashMap<>();
	
	static
	{
		LILITH_SPAWN.put(LILITH, new Location(-83175, 217021, -7504, 49151));
		LILITH_SPAWN.put(LILITHS_STEWARD, new Location(-83327, 216938, -7492, 50768));
		LILITH_SPAWN.put(LILITHS_ELITE, new Location(-83003, 216909, -7492, 4827));
		ANAKIM_SPAWN.put(ANAKIM, new Location(-83179, 216479, -7504, 16384));
		ANAKIM_SPAWN.put(ANAKIMS_GUARDIAN, new Location(-83321, 216507, -7492, 16166));
		ANAKIM_SPAWN.put(ANAKIMS_GUARD, new Location(-83086, 216519, -7495, 15910));
		ANAKIM_SPAWN.put(ANAKIMS_EXECUTOR, new Location(-83031, 216604, -7492, 17071));
	}
	
	public DisciplesNecropolisPast()
	{
		super(DisciplesNecropolisPast.class.getSimpleName());
		addAttackId(SEAL_DEVICE);
		addFirstTalkId(SHUNAIMAN, LEON, DISCIPLES_GATEKEEPER);
		addKillId(LILIM_BUTCHER, LILIM_MAGUS, LILIM_KNIGHT_ERRANT, LILIM_KNIGHT, SHILENS_EVIL_THOUGHTS1, SHILENS_EVIL_THOUGHTS2, LILIM_SLAYER, LILIM_GREAT_MAGUS, LILIM_GUARD_KNIGHT);
		addAggroRangeEnterId(LILIM_BUTCHER, LILIM_MAGUS, LILIM_KNIGHT_ERRANT, LILIM_KNIGHT, SHILENS_EVIL_THOUGHTS1, SHILENS_EVIL_THOUGHTS2, LILIM_SLAYER, LILIM_GREAT_MAGUS, LILIM_GUARD_KNIGHT);
		addSpawnId(SEAL_DEVICE);
		addStartNpc(PROMISE_OF_MAMMON);
		addTalkId(PROMISE_OF_MAMMON, SHUNAIMAN, LEON, DISCIPLES_GATEKEEPER);
	}
	
	protected void spawnNPC(DNPWorld world)
	{
		for (Map.Entry<Integer, Location> entry : LILITH_SPAWN.entrySet())
		{
			final L2Npc npc = addSpawn(entry.getKey(), entry.getValue(), false, 0, false, world.getInstanceId());
			world.lilithGroup.add(npc);
			switch (npc.getId())
			{
				case LILITH:
					world._lilith = npc;
					break;
			}
		}
		for (Map.Entry<Integer, Location> entry : ANAKIM_SPAWN.entrySet())
		{
			final L2Npc enpc = addSpawn(entry.getKey(), entry.getValue(), false, 0, false, world.getInstanceId());
			world.anakimGroup.add(enpc);
			switch (enpc.getId())
			{
				case ANAKIM:
					world._anakim = enpc;
					break;
			}
		}
	}
	
	private synchronized void checkDoors(L2Npc npc, DNPWorld world)
	{
		world.countKill++;
		switch (world.countKill)
		{
			case 4:
				openDoor(DOOR_1, world.getInstanceId());
				break;
			case 10:
				openDoor(DOOR_2, world.getInstanceId());
				break;
			case 18:
				openDoor(DOOR_3, world.getInstanceId());
				break;
			case 28:
				openDoor(DOOR_4, world.getInstanceId());
				break;
			case 40:
				openDoor(DOOR_5, world.getInstanceId());
				break;
		}
	}
	
	@Override
	public void onEnterInstance(L2PcInstance player, InstanceWorld world, boolean firstEntrance)
	{
		if (firstEntrance)
		{
			spawnNPC((DNPWorld) world);
			world.addAllowed(player.getObjectId());
		}
		teleportPlayer(player, ENTER, world.getInstanceId());
	}
	
	private void makeCast(L2Npc npc, List<L2Npc> targets)
	{
		npc.setTarget(targets.get(getRandom(targets.size())));
		if (SKILLS.containsKey(npc.getId()))
		{
			npc.doCast(SKILLS.get(npc.getId()).getSkill());
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final InstanceWorld tmpworld = InstanceManager.getInstance().getPlayerWorld(player);
		if (tmpworld instanceof DNPWorld)
		{
			final DNPWorld world = (DNPWorld) tmpworld;
			switch (event)
			{
				case "FINISH":
				{
					if (getQuestItemsCount(player, SEAL_OF_BINDING) >= 4)
					{
						stopPlayer(player);
						stopSeals(player);
						
						player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ_SEALING_EMPEROR_2ND);
						startQuestTimer("TELEPORT", 27000, null, player);
					}
					break;
				}
				case "TELEPORT":
				{
					player.enableAllSkills();
					player.setIsImmobilized(false);
					teleportPlayer(player, ENTER, player.getInstanceId());
					break;
				}
				case "FIGHT":
				{
					for (L2Npc caster : world.anakimGroup)
					{
						if ((caster != null) && !caster.isCastingNow())
						{
							makeCast(caster, world.lilithGroup);
						}
						if ((caster != null) && (caster.getId() == ANAKIM))
						{
							if (caster.isScriptValue(0))
							{
								world._lilith.broadcastPacket(new NpcSay(world._lilith.getObjectId(), Say2.NPC_SHOUT, world._lilith.getId(), NpcStringId.YOU_SUCH_A_FOOL_THE_VICTORY_OVER_THIS_WAR_BELONGS_TO_SHILIEN));
								caster.setScriptValue(1);
							}
							else if (getRandom(100) < 10)
							{
								world._lilith.broadcastPacket(new NpcSay(world._lilith.getObjectId(), Say2.NPC_SHOUT, world._lilith.getId(), LILITH_SHOUT[getRandom(3)]));
							}
						}
					}
					for (L2Npc caster : world.lilithGroup)
					{
						if ((caster != null) && !caster.isCastingNow())
						{
							makeCast(caster, world.anakimGroup);
						}
						if ((caster != null) && (caster.getId() == LILITH))
						{
							if (caster.isScriptValue(0))
							{
								world._anakim.broadcastPacket(new NpcSay(world._anakim.getObjectId(), Say2.NPC_SHOUT, world._anakim.getId(), NpcStringId.FOR_THE_ETERNITY_OF_EINHASAD));
								if (Util.checkIfInRange(2000, world._anakim, player, true))
								{
									player.sendPacket(new NpcSay(world._anakim.getObjectId(), Say2.TELL, world._anakim.getId(), NpcStringId.MY_POWERS_WEAKENING_HURRY_AND_TURN_ON_THE_SEALING_DEVICE));
								}
								caster.setScriptValue(1);
							}
							else if (getRandom(100) < 10)
							{
								switch (getRandom(3))
								{
									case 0:
									{
										world._anakim.broadcastPacket(new NpcSay(world._anakim.getObjectId(), Say2.NPC_SHOUT, world._anakim.getId(), NpcStringId.DEAR_SHILLIENS_OFFSPRINGS_YOU_ARE_NOT_CAPABLE_OF_CONFRONTING_US));
										if (Util.checkIfInRange(2000, world._anakim, player, true))
										{
											player.sendPacket(new NpcSay(world._anakim.getObjectId(), Say2.TELL, world._anakim.getId(), NpcStringId.ALL_4_SEALING_DEVICES_MUST_BE_TURNED_ON));
										}
										break;
									}
									case 1:
									{
										world._anakim.broadcastPacket(new NpcSay(world._anakim.getObjectId(), Say2.NPC_SHOUT, world._anakim.getId(), NpcStringId.ILL_SHOW_YOU_THE_REAL_POWER_OF_EINHASAD));
										if (Util.checkIfInRange(2000, world._anakim, player, true))
										{
											player.sendPacket(new NpcSay(world._anakim.getObjectId(), Say2.TELL, world._anakim.getId(), NpcStringId.LILITHS_ATTACK_IS_GETTING_STRONGER_GO_AHEAD_AND_TURN_IT_ON));
										}
										break;
									}
									case 2:
									{
										world._anakim.broadcastPacket(new NpcSay(world._anakim.getObjectId(), Say2.NPC_SHOUT, world._anakim.getId(), NpcStringId.DEAR_MILITARY_FORCE_OF_LIGHT_GO_DESTROY_THE_OFFSPRINGS_OF_SHILLIEN));
										if (Util.checkIfInRange(2000, world._anakim, player, true))
										{
											player.sendPacket(new NpcSay(world._anakim.getObjectId(), Say2.TELL, world._anakim.getId(), NpcStringId.DEAR_S1_GIVE_ME_MORE_STRENGTH).addStringParameter(player.getName()));
										}
										break;
									}
								}
							}
						}
						startQuestTimer("FIGHT", 1000, null, player);
					}
					break;
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		switch (npc.getId())
		{
			case LILIM_BUTCHER:
			case LILIM_GUARD_KNIGHT:
			{
				if (npc.isScriptValue(0))
				{
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getId(), NpcStringId.THIS_PLACE_ONCE_BELONGED_TO_LORD_SHILEN));
					npc.setScriptValue(1);
				}
				break;
			}
			case LILIM_MAGUS:
			case LILIM_GREAT_MAGUS:
			{
				if (npc.isScriptValue(0))
				{
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getId(), NpcStringId.WHO_DARES_ENTER_THIS_PLACE));
					npc.setScriptValue(1);
				}
				break;
			}
			case LILIM_KNIGHT_ERRANT:
			case LILIM_KNIGHT:
			{
				if (npc.isScriptValue(0))
				{
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getId(), NpcStringId.THOSE_WHO_ARE_AFRAID_SHOULD_GET_AWAY_AND_THOSE_WHO_ARE_BRAVE_SHOULD_FIGHT));
					npc.setScriptValue(1);
				}
				break;
			}
			case LILIM_SLAYER:
			{
				if (npc.isScriptValue(0))
				{
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getId(), NpcStringId.LEAVE_NOW));
					npc.setScriptValue(1);
				}
				break;
			}
		}
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isSummon)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getPlayerWorld(player);
		if (tmpworld instanceof DNPWorld)
		{
			final DNPWorld world = (DNPWorld) tmpworld;
			if ((npc.getId() == SEAL_DEVICE) && !world.seals.contains(npc))
			{
				world.seals.add(npc);
			}
			
			if (npc.isScriptValue(0))
			{
				if (npc.getCurrentHp() < (npc.getMaxHp() * 0.1))
				{
					giveItems(player, SEAL_OF_BINDING, 1);
					player.sendPacket(SystemMessageId.THE_SEALING_DEVICE_ACTIVATION_COMPLETE);
					npc.setScriptValue(1);
					startQuestTimer("FINISH", 1000, npc, player);
					cancelQuestTimer("FIGHT", npc, player);
				}
			}
			if (getRandom(100) < 50)
			{
				npc.doCast(SEAL_ISOLATION.getSkill());
			}
		}
		return super.onAttack(npc, player, damage, isSummon);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return npc.getId() + ".htm";
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		final InstanceWorld tmpworld = InstanceManager.getInstance().getPlayerWorld(player);
		if (tmpworld instanceof DNPWorld)
		{
			final DNPWorld world = (DNPWorld) tmpworld;
			checkDoors(npc, world);
		}
		
		switch (npc.getId())
		{
			case LILIM_MAGUS:
			case LILIM_GREAT_MAGUS:
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getId(), NpcStringId.LORD_SHILEN_SOME_DAY_YOU_WILL_ACCOMPLISH_THIS_MISSION));
				break;
			}
			case LILIM_KNIGHT_ERRANT:
			case LILIM_KNIGHT:
			case LILIM_GUARD_KNIGHT:
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getId(), NpcStringId.WHY_ARE_YOU_GETTING_IN_OUR_WAY));
				break;
			}
			case LILIM_SLAYER:
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getId(), NpcStringId.FOR_SHILEN));
				break;
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public final String onSpawn(L2Npc npc)
	{
		npc.setIsMortal(false);
		return super.onSpawn(npc);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		final QuestState qs = talker.getQuestState(Q00196_SevenSignsSealOfTheEmperor.class.getSimpleName());
		String htmltext = getNoQuestMsg(talker);
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (npc.getId())
		{
			case PROMISE_OF_MAMMON:
			{
				if (qs.isCond(3) || qs.isCond(4))
				{
					enterInstance(talker, new DNPWorld(), "DisciplesNecropolisPast.xml", TEMPLATE_ID);
					return "";
				}
				break;
			}
			case LEON:
			{
				if (qs.getCond() >= 3)
				{
					takeItems(talker, SACRED_SWORD_OF_EINHASAD, -1);
					InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(talker);
					world.removeAllowed(talker.getObjectId());
					talker.teleToLocation(EXIT, 0);
					htmltext = "32587-01.html";
				}
				break;
			}
			case DISCIPLES_GATEKEEPER:
			{
				if (qs.getCond() >= 3)
				{
					InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
					if (tmpworld instanceof DNPWorld)
					{
						DNPWorld world = (DNPWorld) tmpworld;
						openDoor(DISCIPLES_NECROPOLIS_DOOR, world.getInstanceId());
						talker.showQuestMovie(ExStartScenePlayer.SCENE_SSQ_SEALING_EMPEROR_1ST);
						startQuestTimer("FIGHT", 1000, null, talker);
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	private void stopPlayer(L2PcInstance player)
	{
		player.abortAttack();
		player.abortCast();
		player.disableAllSkills();
		player.setTarget(null);
		player.stopMove(null);
		player.setIsImmobilized(true);
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
	}
	
	private void stopSeals(L2PcInstance player)
	{
		final InstanceWorld tmpworld = InstanceManager.getInstance().getPlayerWorld(player);
		if (tmpworld instanceof DNPWorld)
		{
			DNPWorld world = (DNPWorld) tmpworld;
			for (L2Npc seal : world.seals)
			{
				if (seal != null)
				{
					seal.abortAttack();
					seal.abortCast();
					seal.setIsInvul(true);
					seal.setIsImmobilized(true);
					seal.disableAllSkills();
				}
			}
		}
	}
}
