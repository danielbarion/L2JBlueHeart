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
package quests.Q00094_SagaOfTheSoultaker;

import l2r.gameserver.model.Location;

import quests.AbstractSagaQuest;

/**
 * Saga of the Soultaker (94)
 * @author Emperorc
 */
public class Q00094_SagaOfTheSoultaker extends AbstractSagaQuest
{
	public Q00094_SagaOfTheSoultaker()
	{
		super(94, Q00094_SagaOfTheSoultaker.class.getSimpleName(), "Saga of the Soultaker");
		_npc = new int[]
		{
			30832,
			31623,
			31279,
			31279,
			31645,
			31646,
			31648,
			31650,
			31654,
			31655,
			31657,
			31279
		};
		Items = new int[]
		{
			7080,
			7533,
			7081,
			7509,
			7292,
			7323,
			7354,
			7385,
			7416,
			7447,
			7085,
			0
		};
		Mob = new int[]
		{
			27257,
			27243,
			27265
		};
		classid = new int[]
		{
			95
		};
		prevclass = new int[]
		{
			0x0d
		};
		npcSpawnLocations = new Location[]
		{
			new Location(191046, -40640, -3042),
			new Location(46066, -36396, -1685),
			new Location(46087, -36372, -1685)
		};
		Text = new String[]
		{
			"PLAYERNAME! Pursued to here! However, I jumped out of the Banshouren boundaries! You look at the giant as the sign of power!",
			"... Oh ... good! So it was ... let's begin!",
			"I do not have the patience ..! I have been a giant force ...! Cough chatter ah ah ah!",
			"Paying homage to those who disrupt the orderly will be PLAYERNAME's death!",
			"Now, my soul freed from the shackles of the millennium, Halixia, to the back side I come ...",
			"Why do you interfere others' battles?",
			"This is a waste of time.. Say goodbye...!",
			"...That is the enemy",
			"...Goodness! PLAYERNAME you are still looking?",
			"PLAYERNAME ... Not just to whom the victory. Only personnel involved in the fighting are eligible to share in the victory.",
			"Your sword is not an ornament. Don't you think, PLAYERNAME?",
			"Goodness! I no longer sense a battle there now.",
			"let...",
			"Only engaged in the battle to bar their choice. Perhaps you should regret.",
			"The human nation was foolish to try and fight a giant's strength.",
			"Must...Retreat... Too...Strong.",
			"PLAYERNAME. Defeat...by...retaining...and...Mo...Hacker",
			"....! Fight...Defeat...It...Fight...Defeat...It..."
		};
		registerNPCs();
	}
}
