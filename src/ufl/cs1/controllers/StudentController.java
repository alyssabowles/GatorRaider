package ufl.cs1.controllers;

import game.controllers.DefenderController;
import game.models.Defender;
import game.models.Game;
import game.models.Node;
import game.models.Maze;
import game.models.Attacker;

import java.util.List;

public final class StudentController implements DefenderController
{
	public void init(Game game) { }

	public void shutdown(Game game) { }

	public int[] update(Game game,long timeDue)
	{

        int[] actions = new int[Game.NUM_DEFENDER];
        List<Defender> enemies = game.getDefenders();

        //Attacker objects
        Attacker attacker = game.getAttacker();

        //Defines all ghosts in game
        Defender red = enemies.get(0);
        Defender pink = enemies.get(1);
        Defender orange = enemies.get(2);
        Defender blue = enemies.get(3);

        Node redPosition = red.getLocation();
        Node orangePosition = orange.getLocation();
        Node bluePosition = blue.getLocation();
        Node pinkPosition = pink.getLocation();

        //Red ghost

        Node pacmanPosition = attacker.getLocation();
        Node pacmanNextMove = pacmanPosition.getNeighbor(attacker.getDirection());
        int distAway = redPosition.getPathDistance(pacmanPosition);
        boolean forkPath = redPosition.isJunction();
        boolean atPill = false;
        boolean pillIsClose = false;
        List<Node> pillList = game.getPowerPillList();

        /* If the distance from red ghost to pacman is less than 15 tiles, then it should
         * follow Pac-Man.
         */
        if (distAway < 15)
        {
            if (pacmanNextMove != null)
                actions[0] = red.getNextDir(pacmanNextMove, true);
            else
                actions[0] = red.getNextDir(redPosition, false);
        }

        //If there is a junction, red ghost should move the direction Pac-Man is in
        if (forkPath)
            if (pacmanNextMove != null)
                actions[0] = red.getNextDir(pacmanNextMove, true);



        //Orange ghost


        /* Orange ghost should be moving around one of the corners of the board,
         * as long as there is a power pill there.
         */
        for (int i = 0; i < pillList.size(); i++)
        {
            if (game.checkPowerPill(pillList.get(i)) == true)
                actions[2] = orange.getNextDir(pillList.get(i), true);
                atPill = !atPill;
        }
        //If there are no more power pills, then it will just chase Pac-Man.
        if(pillList.size() == 0)
            actions[2] = orange.getNextDir(pacmanPosition, true);



        //Blue ghost

        //Determine if Pac man is close to a power pill, so blue can know to run away

        //The for loop determines if the pill is close to Pac-Man
        for (int i = 0; i < pillList.size(); i++)
        {
            if (game.checkPowerPill(pillList.get(i)) == true)
            {
                if(pacmanPosition.getPathDistance(pillList.get(i)) < 250)
                {
                    pillIsClose = true;
                    break;
                }
                else
                    pillIsClose = false;
            }
        }

        //When blue ghost is vulnerable, then it should run away from Pac-Man
        if(blue.getVulnerableTime()>0 || pillIsClose)
            actions[3] = blue.getNextDir(pacmanPosition, false);
        else
        {
            if (pacmanNextMove != null)
                actions[3] = blue.getNextDir(pacmanNextMove, true);
            else
                actions[3] = blue.getNextDir(pacmanPosition, true);
        }




        //Pink Ghost

        //Pink ghost should always be chasing Pac-Man
        if (pacmanNextMove != null)
            actions[1] = pink.getNextDir(pacmanNextMove, true);
        else
            actions[1] = pink.getNextDir(pacmanPosition, true);
        //When the ghost is vulnerable, flee from Pac-Man.
        if(pink.getVulnerableTime() > 0)
            actions[1]= pink.getNextDir(pacmanPosition,false);


        return actions;
	}
}