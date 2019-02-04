package com.pengine.components.renderers;

import com.pengine.Vector;
import processing.core.PImage;

import static com.pengine.PEngine.APPLET;

public class Animation {

  PImage[] pics;
  int frame = 0;
  int len;
  Vector size;
  String name;

  public Animation(String filePath, int count, Vector csize) {
    pics = new PImage[count];
    len = count;
    size = csize;

    for (int i=0; i<count; i++) {
      //test
      pics[i] = APPLET.loadImage(filePath+(i+1)+".png");
      pics[i].resize((int)size.x, (int)size.y);
    }
    if (pics[0] == null) {
      APPLET.println(this);
      APPLET.exit();
    }
  }

  PImage show() {
    if (APPLET.frameCount%10==0) {
      frame++;
      frame %= len;
    }
    return pics[frame];
  }

}
