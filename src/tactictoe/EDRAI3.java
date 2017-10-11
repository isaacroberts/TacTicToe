/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tactictoe;

import java.util.ArrayList;
import java.util.Random;
import tactictoe.Hash.State;

/**
 * This is a Tac Tic Toe player.  
 * This version of the player adds an ability to look at the implication of the move on what the other player can do.
 * @author Erling
 */
public class EDRAI3 extends AI {
    
    public class Location {
        int x, y, value, maxOccupy;
        ArrayList<WinWay> wins = new ArrayList<WinWay>();
        public Location(int ex, int why) {
            x = ex;
            y = why;
            value = 0;
            maxOccupy = 0;
        }
    }

   public Location squares[] = new Location[9];
   public Location hashes[] = new Location[9];

   private Hash.State playerState;

   private class WinWay {            
        public Location row[] = new Location[3];
        public Hash.State status;
        public int occupied;
        public WinWay(Location one, Location two, Location three) {
            row[0] = one;
            row[1] = two;
            row[2] = three;
            status = Hash.State.Idle;
            occupied = 0;
        }
    }
    ArrayList <WinWay> hashWins = new ArrayList<WinWay>();
    
    public EDRAI3(boolean p1)
    {
        super(p1);
        // index = 3x + y
        squares[0] = new Location(0,0);
        squares[1] = new Location(0,1);
        squares[2] = new Location(0,2);
        squares[3] = new Location(1,0);
        squares[4] = new Location(1,1);
        squares[5] = new Location(1,2);
        squares[6] = new Location(2,0);
        squares[7] = new Location(2,1);
        squares[8] = new Location(2,2);
   
        hashes[0] = new Location(0,0);
        hashes[1] = new Location(0,1);
        hashes[2] = new Location(0,2);
        hashes[3] = new Location(1,0);
        hashes[4] = new Location(1,1);
        hashes[5] = new Location(1,2);
        hashes[6] = new Location(2,0);
        hashes[7] = new Location(2,1);
        hashes[8] = new Location(2,2);
        for (int w=0; w< hashes.length; ++w) 
        {
            hashes[w].wins.add(new WinWay(squares[0], new Location(0,1), new Location(0,2)));
            hashes[w].wins.add(new WinWay(new Location(1,0), new Location(1,1), new Location(1,2)));
            hashes[w].wins.add(new WinWay(new Location(2,0), new Location(2,1), new Location(2,2)));
            hashes[w].wins.add(new WinWay(new Location(0,0), new Location(1,0), new Location(2,0)));
            hashes[w].wins.add(new WinWay(new Location(0,1), new Location(1,1), new Location(2,1)));
            hashes[w].wins.add(new WinWay(new Location(0,2), new Location(1,2), new Location(2,2)));
            hashes[w].wins.add(new WinWay(new Location(0,0), new Location(1,1), new Location(2,2)));
            hashes[w].wins.add(new WinWay(new Location(2,0), new Location(1,1), new Location(0,2)));
        }
   
        hashWins.add(new WinWay(hashes[0], hashes[1], hashes[2]));
        hashWins.add(new WinWay(hashes[3], hashes[4], hashes[5]));
        hashWins.add(new WinWay(hashes[6], hashes[7], hashes[8]));
        hashWins.add(new WinWay(hashes[0], hashes[3], hashes[6]));
        hashWins.add(new WinWay(hashes[1], hashes[4], hashes[7]));
        hashWins.add(new WinWay(hashes[2], hashes[5], hashes[8]));
        hashWins.add(new WinWay(hashes[0], hashes[4], hashes[8]));
        hashWins.add(new WinWay(hashes[2], hashes[4], hashes[6]));
        
        
        if (isP1) {
            playerState = Hash.State.P1;
            } else {
            playerState = Hash.State.P2;
            }
   } // end of constructor

    public void play(Game g)
    {
    // Game has 3x3 Hash's and each Hash has 3x3 of spaces
    // Game.currentHash().getSpace(x,y)
    // Game.getHash(x,y) get the hash object to call hash functions with
    // Game.takeSpace(x,y)  select square
    // Game.setCurrentHash(x,y) pick a hash when openMove
    // Game.openMove()  Boolean to decide if you can choose hash        
    // PREPARE FOR NEXT ROUND
        // populate the maxOccupy for each hash to enable evaluation of hte game
    for (int h = 0; h<hashes.length; ++h) {
        hashes[h].maxOccupy = 0;
        int tempOcc = 0;
        for (int w = 0; w<hashes[h].wins.size(); ++w) {
            findOpenSquare(hashes[h].wins.get(w),true,g, h);
//            System.out.println("WW: " + hashes[h].wins.get(w).occupied);
            if (tempOcc < hashes[h].wins.get(w).occupied) {
                tempOcc = hashes[h].wins.get(w).occupied;
            }
            if (tempOcc == 2) {
                w = hashes[h].wins.size();
            }
        }
        if (hashes[h].maxOccupy < tempOcc) {
            hashes[h].maxOccupy = tempOcc;
            }
    }
    // Declaration Section
//      PLAY THE STRATEGIC GAME FIRST
    zeroLocationValue(hashes);
    evaluateWinWay(hashes, hashWins, g, false, 0); //curr hash not needed when false
    if (g.openMove()) {
            //decide which hash to play
//        System.out.println("Hash Value=");
        int pick = findHighValue(hashes);
//        System.out.print("Square = " +pick +":" +hashes[pick].x + "," + hashes[pick].y );
        g.setCurrentHash(hashes[pick].x, hashes[pick].y);  //BACKWARDS?
    }
 //     WORK THE TACTICAL SIDE
    Hash current = g.currentHash();
    int currHash = current.getX() * 3 + current.getY();
    // there are 8 ways to win, and the logic is to check each way to win and decide which
    // ways remain and select a position based on the best way to win.
    zeroLocationValue(squares);
    evaluateWinWay(squares, hashes[currHash].wins, g, true, currHash);     
    secretGameAnalysis(hashes, squares, currHash);
//    System.out.println("values = ");
    int choice = findHighValue(squares);
    g.takeSpace(squares[choice].x, squares[choice].y);
    } //end of game play   
      
    private void zeroLocationValue(Location[] loc) {    
        for (int a = 0; a < loc.length; ++a) {
        loc[a].value = 0;
        }
    }
    
    private int findHighValue(Location[] loc) {
      int highValue = 0;
      int choice = 0;
      for (int select = 0; select < loc.length; ++select) {
//         System.out.print(loc[select].value + ", ");
         if (loc[select].value > highValue) {
            highValue = loc[select].value;
            choice = select;
            } else if (loc[select].value == highValue) { //don't be too predictable
                Random probability = new Random();
                double p = probability.nextDouble();
                if (p > 0.5) {
                    highValue = loc[select].value;
                    choice = select;
                }
            }
      }
      return choice;
    }  

    private void secretGameAnalysis(Location[] strat, Location[] tact, int cHash) {
     //Tactical is the value returned from evaluate WinWay and is already stored
    // The importance of the square on the board
    // The importance of the next square to the opponent
        //the max value, so opportunity when open.
    // The tactical situation in the next square
 
    for (int pChoice =0; pChoice < strat.length; ++pChoice) {
        if (tact[pChoice].value != 0) { //only check real choices
            int futTactical = 0;
            for (int j=0; j< strat[pChoice].wins.size(); ++j) { //these are old values...one turn behind
//                    System.out.print(pChoice + ": " +strat[pChoice].wins.get(j).occupied + ", ");
                if (strat[pChoice].wins.get(j).occupied > futTactical) {
                    futTactical = strat[pChoice].wins.get(j).occupied;
                }
                //I have not adjusted futTactical to account for this move if cHash = pChoice
                    //It would mean adding one, but not always...
            }
            int futStrategic = strat[pChoice].value;
            if (futStrategic == 0) {
                int high = findHighValue(strat);
                futStrategic = strat[high].value;
                //set futTactical to max occupy on board, how?
            }
            tact[pChoice].value = strat[cHash].value * tact[pChoice].value - futStrategic * (1+ futTactical); 
//            System.out.println("secret: "+strat[cHash].value + "* " + tact[pChoice].value + "- "+ futStrategic + "* " + futTactical);
        }
    }
 
    //          tactical * strategic - futStrategic * futTactical;
    //if strat[choice].state complete, strat[findHighValue(strat)].value;
    //strat[i].value
    }

    private void evaluateWinWay(Location[] loc, ArrayList<WinWay> wwal, Game g, Boolean isSquare, int cHash)
    {
        for(int i = 0; i < wwal.size(); ++i) {
        ArrayList <Integer> openSq  = new ArrayList<Integer>();
        openSq.addAll(findOpenSquare(wwal.get(i), isSquare, g, cHash));
        if (wwal.get(i).status == Hash.State.Tie) {
           wwal.remove(i);
           i--;
        } else {
          int bonus = 0;
          if (wwal.get(i).status == playerState && wwal.get(i).occupied == 2) {
               bonus = 2; //if you can win, that that is worth extra value, but not ultimate
          }
          for (int toAdd = 0; toAdd < 3 - wwal.get(i).occupied; ++toAdd) {
              //This is the tactical element of game play
              loc[(wwal.get(i).row[openSq.get(toAdd)].x * 3 + wwal.get(i).row[openSq.get(toAdd)].y)].value += (1+wwal.get(i).occupied) * (1 + wwal.get(i).occupied) * (1 + bonus);
//              System.out.println(1 * (1+filled) * (1+ filled));  
          }
        }
    } //end of wins for
   //     System.out.print("WW:" + wwal.size()); 
    }
    
    private ArrayList <Integer> findOpenSquare(WinWay way, Boolean isSquare, Game g, int cHash) {
       Hash.State read;
       int filled = 0;
       int x = cHash / 3;
       int y = cHash % 3;
       Hash.State tempState = Hash.State.Idle;
       ArrayList <Integer> openSq  = new ArrayList<Integer>();
       for(int r = 0; r < 3; ++r) {
            if (isSquare) {
                read = g.getHash(x,y).getSpace(way.row[r].x, way.row[r].y);
            } else {
                read = g.getHash(way.row[r].x, way.row[r].y).getHashState();
     //           System.out.println("Hash State=" + read);
            }
            switch (read) {
               case Idle:
               case Playable:
               case Open:
                //capture identity
                openSq.add(r);
                 break;
               case Tie:
                   // not sure what to do here for hash is tie...
                   break;
               case P1:
                //P2 can't win now so wrap up and go home
                if (tempState == Hash.State.P2) {
                    tempState = Hash.State.Tie;
                    r = 3;
                    } else {
                    //P1 can earn points
                    filled += 1;
                    tempState = Hash.State.P1;
                    }
                break;
               case P2:
                //opposite of the above
                 if (tempState == Hash.State.P1) {
                    tempState = Hash.State.Tie;
                    r = 3;
                    } else {
                    //P2 can earn points
                    filled += 1;
                    tempState = Hash.State.P2;
                    }
                break;
            } //end of switch
       } //end of three locations in win
       //what do we know about the wins situation now?
        way.occupied = filled;
        way.status = tempState;
        return openSq;
    }
}
