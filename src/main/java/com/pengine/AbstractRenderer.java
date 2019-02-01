package com.pengine;

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

  @Override
  public boolean lateUpdate() {
    show();
    return false;
  }

}

