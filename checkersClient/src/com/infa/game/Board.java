package game;

import java.io.Serializable;
import java.util.ArrayList;

public class Board implements Serializable
{
    private ArrayList<Byte> board;
    private byte boardSize;

    public Board()
    {
        //0 - empty
        //1,2 - players
        boardSize = 8;
        board = new ArrayList<Byte>();
        for(int y=0;y<boardSize;y++)
        {
            for(int x=0;x<boardSize;x++)
            {
                if(y<3)
                {
                    if((x+y*boardSize+y) %2==1) board.add((byte) 1);
                    else board.add((byte) 0);
                }
                else if(y>4)
                {
                    if((x+y*boardSize+y) %2==1) board.add((byte) 2);
                    else board.add((byte) 0);
                }
                else board.add((byte) 0);
            }
        }
    }

    public void show()
    {
        for(int y=0;y<boardSize;y++)
        {
            for(int x=0;x<boardSize;x++)
            {
                System.out.print(board.get(x+y*boardSize));
            }
            System.out.print("\n");
        }
    }

    public ArrayList<Byte> getBoard()
    {
        return board;
    }

    public byte getSize() {return boardSize;}
}
