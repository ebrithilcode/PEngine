package com.rsttst.pengine;

import java.util.List;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

public class Engine {
  Collider collider;
  List<Entity> entities, queuedSpawns, queuedKills;
  boolean spawnsInQueue, killsInQueue;

  public Engine(Collider collider) {
    this.collider = collider;
    entities = new ArrayList<Entity>();
    queuedSpawns = new ArrayList<Entity>();
    queuedKills = new ArrayList<Entity>();
    spawnsInQueue = false;
    killsInQueue = false;
  }

  public Engine(Collider collider, List<? extends Entity> entities) {
    this(collider);
    this.entities.addAll(entities);
  }

  public void update() {
    if(spawnsInQueue) {
      entities.addAll(queuedSpawns);
      queuedSpawns.clear();
      spawnsInQueue = false;
    }
    if(killsInQueue) {
      entities.removeAll(queuedKills);
      queuedKills.clear();
      killsInQueue = false;
    }
    for(Entity e : entities) {
      e.update(this);
    }
    handleCollisions();
  }

  public void render(PApplet applet) {
    for(Entity e : entities) {
      e.render(applet);
    }
  }

  //naive implmentation - consider using a quad tree
  protected void handleCollisions() {
    for (int i = 0; i<entities.size(); i++) {
      for (int j=i+1; j<entities.size(); j++) {
        PVector collision = collider.getCollision(entities.get(i), entities.get(j));
        if (collision.x != 0 || collision.y != 0) {
          entities.get(i).onCollide(this, entities.get(j), collision.copy().mult(-1));
          entities.get(j).onCollide(this, entities.get(i), collision.copy());
        }
      }
    }
  }

  public void spawnEntity(Entity toSpawn) {
    queuedSpawns.add(toSpawn);
    spawnsInQueue = true;
  }

  public void killEntity(Entity toKill) {
    queuedKills.add(toKill);
    killsInQueue = true;
  }

}
