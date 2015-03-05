/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SnakeObjects;

import java.util.ArrayList;
import main.SnakeCore;
import main.Visible;
import processing.core.PApplet;
import processing.core.PVector;

import SnakeObjects.Field;



/**
 *
 * @author fatcloud
 */
public class Snake implements Visible {

  // the snake game
  SnakeCore  snakeCore;
  
  // imformation required for rendering
  int   col;
  
  // imformation about location, length
  ArrayList<SnakeBody> bodyParts;

  int     state;          // Alive? Invincible? Normal? etc.
  char     direction;
  float   speed;          // unit: steps per frame
  
  
  public static final char 
      GO_LEFT        = 'l',
      GO_RIGHT       = 'r',
      GO_UP          = 'u',
      GO_DOWN        = 'd';
  
  public Snake( SnakeCore sc ){
    snakeCore = sc;
    SnakeBody head = new SnakeBody(0,0);
    bodyParts = new ArrayList<SnakeBody>();
    direction = 'l';
    
    bodyParts.add( head );
  }
  
  public void setHeadPosition( int x, int y ) {
    SnakeBody head = bodyParts.get(0);
    head.set( x, y );
  }
  
  public void setLength( int len ){
    int initLen = bodyParts.size();
    if( len > initLen ) {
      SnakeBody sb = bodyParts.get( bodyParts.size() - 1 );
      for( int i = initLen; i < len; ++i )
        bodyParts.add( new SnakeBody( sb.x, sb.y) );
    } else {
      for ( int i = initLen - 1; i > len ; --i ) 
        bodyParts.remove( i );
    }
  }
  
  public void setColor( int c ) {
    col = c;
  }
  
  public void setDirection( char dir ) {
    direction = dir;
  }
  
  
  
  public void updatePosition( ){
        
    //head
    SnakeBody sb = bodyParts.get(0);
    for( int i = bodyParts.size() - 1; i > 0; --i){
      SnakeBody sbp = bodyParts.get(i);
      sbp.x = bodyParts.get( i - 1 ).x;
      sbp.y = bodyParts.get( i - 1 ).y;
    }
    
    //check if the position of head match doors position;
    //and the direction fit any door direction, tell the snake
    //go through the door,
    //otherwise, take snake into next position.
    //move sb
    PVector headPos = new PVector( sb.x, sb.y );
    Field f = snakeCore.getField(); 
    
    if ( f.isEnteringDoor( headPos, direction ) ) {
        PVector dp = f.getDoorOutPos( headPos, direction );
        char    dd = f.getDoorOutDir( headPos, direction );
        sb.x = dp.x; 
        sb.y = dp.y;
        if( dd == 'u' )
            direction = 'd';
        else if( dd == 'l' )
            direction = 'r';
        else if( dd == 'r' )
            direction = 'l';
        else if( dd == 'd' )
            direction = 'u';
        
    } else {
        switch (direction) {
            case GO_LEFT:
                sb.x = sb.x - 1;
                break;
            case GO_RIGHT:
                sb.x = sb.x + 1;
                break;
            case GO_UP:
                sb.y = sb.y - 1;
                break;
            case GO_DOWN:
                sb.y = sb.y + 1;
                break;
        }        
    }
  }
  
  
  
  
  public void render( PApplet p ){
    for( SnakeBody sb : bodyParts ){
      p.fill( col );
      int gridSize = 10;//snakeCore.gridSize;
      p.rect( sb.x * gridSize, sb.y * gridSize, gridSize, gridSize );
    }
  }
  
}
