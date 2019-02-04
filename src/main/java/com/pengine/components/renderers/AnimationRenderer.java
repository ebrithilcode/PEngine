package com.pengine.components.renderers;

import com.pengine.components.AbstractRenderer;
import com.pengine.GameObject;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;

import static com.pengine.PEngine.APPLET;


public class AnimationRenderer extends AbstractRenderer {

  List<Animation> animations;
  Animation current;
  boolean mirror = false;

  public AnimationRenderer(GameObject g) {
    super(g);
    animations = new ArrayList<>();
  }

  public void startAnimation(String n) {
    for (Animation a: animations) {
      if (a.name.equals(n)) {
        current = a;
        return;
      }
    }
  }

  public void show() {
    if (current != null) {
      PImage img = current.show();
      APPLET.pushMatrix();
      APPLET.translate(parent.pos.x, parent.pos.y);
      APPLET.rotate(parent.rot);
      if (mirror) APPLET.scale(-1,1);
      APPLET.image(img,0,0);
      APPLET.popMatrix();
    }
  }

}
