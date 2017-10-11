package tactictoe;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import tactictoe.tactful.HashPlotter;
import tactictoe.tactful.Path;
import tactictoe.tactful.Tactful;
import tactictoe.tactful.Guesser;



public class TacTicToe extends JFrame implements Runnable, KeyListener, ComponentListener
{
    
    void startAI() {
        
        p1=new Tactful(true);
        p2=new Tactful(false);
        
    }
    
    
    
    
Container con = getContentPane();
Thread t = new Thread(this);

public Game game;
boolean shift;
AI p1,p2;
static final long frameTime=1000;

    public TacTicToe()
    {
        con.setBackground(Color.BLUE);
        con.setLayout(new FlowLayout());
        addKeyListener(this);
        addComponentListener(this);
        Hash.initialize();
        Mark.initialize();
        Path.initialize();
        HashPlotter.initialize();
        game=new Game();
        startAI();
        shift=false;
        
        
        t.start();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    public void run()
    {
        try {
          while (true)
          {
              long startTime=System.currentTimeMillis();
              if (!game.getStatus().over())
              {
                  if (game.activePlayer())
                  {
                      p1.play(game);
                  }
                  else
                      p2.play(game);
              }
              repaint();
              long sleep=frameTime-(System.currentTimeMillis()-startTime);
              if (sleep>0)
                  t.sleep(sleep);
          }
        }
        catch(Exception e)
        {
            e.printStackTrace();
//            System.exit(0);
        }
    }
    public void update(Graphics g)
    {
        paint(g);
    } 
    public static String drawString="";
    public void paint(Graphics gr)
    {
       // super.paint(gr);
        Image i=createImage(getSize().width, getSize().height);
        Graphics2D g2 = (Graphics2D)i.getGraphics();
        game.draw(g2);
        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial",Font.PLAIN,20));
        g2.drawString(drawString,30,60);
        g2.dispose();
        gr.drawImage(i, 0, 0, this);
    }
    public static TacTicToe frame;
    public static void main(String[] args)
    {
        Util.size=1000;
        frame = new TacTicToe();

        frame.setSize((int)Util.size, (int)Util.size);
        frame.setVisible(true);
    }
    public static void resize()
    {
        Hash.resize();
        Mark.resize();
    }
    public void keyReleased(KeyEvent k)
    {
        if (k.getKeyCode()==KeyEvent.VK_SHIFT)
        {
            shift=false;
        }
        else if (!game.getStatus().over())
        {
            if (game.activePlayer() && p1.allowInput()
                    || !game.activePlayer() && p2.allowInput())
            {
                switch (k.getKeyCode())
                {
                    case KeyEvent.VK_RIGHT:
                        game.moveCursor(1,0,shift);
                        break;
                    case KeyEvent.VK_LEFT:
                        game.moveCursor(-1, 0,shift);
                        break;
                    case KeyEvent.VK_UP:
                        game.moveCursor(0,-1,shift);
                        break;
                    case KeyEvent.VK_DOWN:
                        game.moveCursor(0,1,shift);
                        break;
                    case KeyEvent.VK_ENTER:
                        game.takeSpace();
                        break;
                }
            }
        }
        else 
        {
            if (k.getKeyCode()==KeyEvent.VK_D)
                game.displayGame=!game.displayGame;
        }
        if (k.getKeyCode()==KeyEvent.VK_BACK_SPACE)
        {
            game=new Game();
            startAI();
        }
        else if (k.getKeyCode()==KeyEvent.VK_ESCAPE)
        {
            System.exit(0);
        }
        repaint();
    }
    public void keyPressed(KeyEvent k)
    {
        if (k.getKeyCode()==KeyEvent.VK_SHIFT)
        {
            shift=true;
        }
    }
    public void keyTyped(KeyEvent k)
    {
        
    }
    public void componentHidden(ComponentEvent e)
    {}
    public void componentShown(ComponentEvent e)
    {}
    public void componentMoved(ComponentEvent e)
    {}
    public void componentResized(ComponentEvent e)
    {
        Rectangle window=getBounds();
        if (window.height<window.width)
            Util.size=window.height;
        else
            Util.size=window.width;
        resize();
    }
    
    
}

