package com.pengine.components;

import com.pengine.GameObject;

public abstract class AbstractRenderer extends Component {

  AbstractRenderer() {}

  protected AbstractRenderer(GameObject g) {
    super(g);
  }

  public abstract void show();

  public boolean earlyUpdate() {
    return false;
  }

  public boolean update() {
    return false;
  }

  public boolean lateUpdate() { return false; }

}

