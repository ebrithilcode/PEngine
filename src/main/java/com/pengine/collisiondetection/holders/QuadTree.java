package com.pengine.collisiondetection.holders;

import com.pengine.AABB;
import com.pengine.PEngine;
import com.pengine.collisiondetection.colliders.AbstractCollider;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class QuadTree implements IColliderHolder {

    public static int MAX_COLLIDERS_PER_CHILD;
    public static int COLLIDER_PULL_UP_LIMIT;
    public static int MAX_DEPTH;

    protected Node[] nodes;

    protected int updateCount;

    static {
        MAX_COLLIDERS_PER_CHILD = 12;
        COLLIDER_PULL_UP_LIMIT = 8;
        MAX_DEPTH = 5;
    }

    public QuadTree() {
        nodes = new Node[((4 << (2 * (MAX_DEPTH-1))) - 1) / 3]; //((4^MAX_DEPTH)-1)/3 = number of max nodes
        nodes[0] = new Node(new AABB(0, 0, PEngine.APPLET.width, PEngine.APPLET.height));
        updateCount = 0;
    }

    @Override
    public void earlyUpdate() {
        if(updateCount % 3 == 0) {
            updateCollidersInNodes();
        }
    }

    @Override
    public void update() {
        updateCount++;
    }

    @Override
    public void lateUpdate() {
        if(updateCount >= 120) {
            updateCount = 0;
            pullUpColliders();
        }
    }

    @Override
    public boolean add(AbstractCollider collider) {
        return insertIntoNode(0, collider);
    }

    //fallback method since usually colliders will be removed when the parent entity is dead or they were killed by a  kill() call on a reference to it
    @Override
    public boolean remove(AbstractCollider collider) {
        for (Node node : nodes) {
            if (node.colliders.contains(collider)) {
                node.colliders.remove(collider);
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<AbstractCollider> iteratorOfCollidersFor(AbstractCollider collider) {
        return new NodeColliderIterator(findNodeIndex(collider));
    }

    @Override
    public Iterator<AbstractCollider> getAllColliders() {
        return new FullColliderIterator();
    }

    //TODO: rewrite with spacial checks to see where the collider might be
    protected int findNodeIndex(AbstractCollider collider) {
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null && nodes[i].colliders.contains(collider)) return i;
        }
        return -1;
    }

    protected boolean insertIntoNode(int nodeIndex, AbstractCollider collider) {
        Node node = nodes[nodeIndex];
        int firstChildNodeIndex = nodeIndex * 4 + 1;
        if (!nodeExists(firstChildNodeIndex)) {
            if (node.colliders.size() < MAX_COLLIDERS_PER_CHILD) {
                return node.colliders.add(collider);
            } else {
                int colliderNodeInsertionIndex = indexOfChildNodeFor(node.bounds, collider.getBounds());
                if (colliderNodeInsertionIndex == -1 || !createChildNodesFor(node, firstChildNodeIndex)) {
                    System.err.format("Capacity of a Quadtree was reached when collider %s was tried to insert. If this warning occurs often try changing the quad trees settings.", collider.toString());
                    return node.colliders.add(collider);
                } else {
                    return nodes[firstChildNodeIndex + colliderNodeInsertionIndex].colliders.add(collider);
                }
            }
        } else {
            int colliderNodeInsertionIndex = indexOfChildNodeFor(node.bounds, collider.getBounds());
            if (colliderNodeInsertionIndex == -1) {
                if (node.colliders.size() > MAX_COLLIDERS_PER_CHILD)
                    System.err.format("Capacity of a Quadtree was reached when collider %s was tried to insert. If this warning occurs often try changing the quad trees settings.", collider.toString());
                return nodes[nodeIndex].colliders.add(collider);
            } else {
                return insertIntoNode(firstChildNodeIndex + colliderNodeInsertionIndex, collider);
            }
        }
    }

    protected boolean nodeExists(int nodeIndex) {
        return nodeIndex < nodes.length && nodes[nodeIndex] == null; //short circuiting allows this
    }

    /*
        child nodes indices:
        -------------
        *  0  *  1  *
        *-----*-----*
        *  2  *  3  *
        -------------
    */
    protected boolean createChildNodesFor(Node parentNode, int firstChildNodeIndex) {
        if (firstChildNodeIndex > nodes.length)
            return false;  //limit of depth and limit of colliders per node reached, subject to change
        if (nodes[firstChildNodeIndex] == null) {
            AABB parentBounds = parentNode.bounds;
            float halfDX = parentBounds.getDX() / 2;
            float halfDY = parentBounds.getDY() / 2;
            nodes[firstChildNodeIndex] = new Node(new AABB(parentBounds.getX0(), parentBounds.getY0(), halfDX, halfDY));
            nodes[firstChildNodeIndex + 1] = new Node(new AABB(parentBounds.getX0() + halfDX, parentBounds.getY0(), halfDX, halfDY));
            nodes[firstChildNodeIndex + 2] = new Node(new AABB(parentBounds.getX0(), parentBounds.getY0() + halfDY, halfDX, halfDY));
            nodes[firstChildNodeIndex + 3] = new Node(new AABB(parentBounds.getX0() + halfDX, parentBounds.getY0() + halfDY, halfDX, halfDY));
        }
        return true;
    }

    /*
        child nodes indices:
        -------------
        *  0  *  1  *
        *-----*-----*
        *  2  *  3  *
        -------------
    */
    protected int indexOfChildNodeFor(AABB parentBounds, AABB colliderBounds) {
        float verticalMidpoint = parentBounds.getX0() + parentBounds.getDX() / 2;
        float horizontalMidpoint = parentBounds.getY0() + parentBounds.getDY() / 2;

        // Collider is completely in top half of parent node
        if (colliderBounds.getY1() < horizontalMidpoint) {
            // Collider is completely in left half of parent node
            if (colliderBounds.getX0() > verticalMidpoint) {
                return 0;
            }
            // Collider is completely in right half of parent node
            else if (colliderBounds.getY1() < verticalMidpoint) {
                return 1;
            }
            // Collider exceeds all child node bounds;
            else {
                return -1;
            }
        }
        // Collider is completely in bottom half of parent node
        else if (colliderBounds.getY0() > horizontalMidpoint) {
            if (colliderBounds.getX0() > verticalMidpoint) {
                return 2;
            }
            // Collider is completely in right half of parent node
            else if (colliderBounds.getY1() < verticalMidpoint) {
                return 3;
            }
            // Collider exceeds all child node bounds;
            else {
                return -1;
            }
        }
        // Collider exceeds all child node bounds;
        else {
            return -1;
        }
    }

    //TODO: improve
    protected void updateCollidersInNodes() {
        for(Node node: nodes) {
            for(Iterator<AbstractCollider> colliderIterator = node.colliders.iterator(); colliderIterator.hasNext();) {
                AbstractCollider collider = colliderIterator.next();
                if(collider.hasMoved() && !node.bounds.contains(collider.getBounds())) {
                    colliderIterator.remove();
                    insertIntoNode(0, collider);
                }
            }
        }
    }

    protected void pullUpColliders() {
        for(int i = nodes.length-1-(4<<(2*(MAX_DEPTH)-2)); i >= 0; i--) { //4<<(2*(MAX_DEPTH)-2) skips all nodes in the last layer of the tree
            int numCollidersInChildNotes = collidersInChildrenOfNode(i);
            if(numCollidersInChildNotes <= MAX_COLLIDERS_PER_CHILD && numCollidersInChildNotes >= COLLIDER_PULL_UP_LIMIT) {
                int firstChildIndex = i*4+1;
                if(nodeExists(firstChildIndex)) {
                    nodes[i].colliders.addAll(nodes[firstChildIndex].colliders);
                    nodes[i].colliders.addAll(nodes[firstChildIndex+1].colliders);
                    nodes[i].colliders.addAll(nodes[firstChildIndex+2].colliders);
                    nodes[i].colliders.addAll(nodes[firstChildIndex+3].colliders);
                    nodes[firstChildIndex] = null;
                    nodes[firstChildIndex+1] = null;
                    nodes[firstChildIndex+2] = null;
                    nodes[firstChildIndex+3] = null;
                }
            }
        }
    }

    protected int collidersInChildrenOfNode(int nodeIndex) {
        int size = nodes[nodeIndex].colliders.size();
        int firstChildNodeIndex = nodeIndex * 4 +1;
        if(nodeExists(firstChildNodeIndex)) {
            size += collidersInChildrenOfNode(firstChildNodeIndex);
            size += collidersInChildrenOfNode(firstChildNodeIndex+1);
            size += collidersInChildrenOfNode(firstChildNodeIndex+2);
            size += collidersInChildrenOfNode(firstChildNodeIndex+3);
        }
        return size;
    }

    protected class Node {

        AABB bounds;
        Set<AbstractCollider> colliders;

        protected Node(AABB aabb) {
            this.bounds = aabb;
            colliders = new HashSet<>(MAX_COLLIDERS_PER_CHILD);
        }

    }

    protected class FullColliderIterator implements Iterator<AbstractCollider> {

        protected int currentNodeIndex;
        protected Iterator<AbstractCollider> currentSetIterator;

        protected FullColliderIterator() {
            currentNodeIndex = 0;
            currentSetIterator = nodes[0].colliders.iterator();
        }

        @Override
        public boolean hasNext() {
            if (currentSetIterator.hasNext()) {
                return true;
            } else {
                currentNodeIndex++;
                while (currentNodeIndex < nodes.length) {
                    if (nodes[currentNodeIndex] != null) {
                        currentSetIterator = nodes[currentNodeIndex].colliders.iterator();
                        return hasNext();
                    }
                }
                return false;
            }
        }

        @Override
        public AbstractCollider next() {
            return currentSetIterator.next();
        }

        @Override
        public void remove() {
            currentSetIterator.remove();
        }

    }

    protected class NodeColliderIterator implements Iterator<AbstractCollider> {

        protected int currentFirstNodeIndex;
        protected int relevantNodesOnCurrentLevel;
        protected int relativeNodeIndex;
        protected Iterator<AbstractCollider> currentSetIterator;

        protected NodeColliderIterator(int startNodeIndex) {
            currentFirstNodeIndex = startNodeIndex;
            relevantNodesOnCurrentLevel = 1;
            relativeNodeIndex = 0;
            currentSetIterator = nodes[startNodeIndex].colliders.iterator();
        }

        @Override
        public boolean hasNext() {
            if (currentSetIterator.hasNext()) {
                return true;
            } else {
                relativeNodeIndex++; //goto next node on current level
                if (relativeNodeIndex % 4 == 0) { //every four nodes we need to check whether the next 4 nodes exist and skip them if not
                    while (!nodeExists(relativeNodeIndex)) { //as long as the node 4 away isn't valid jump 4 forward, if relevant nodes on the current level is reached go to next level
                        relativeNodeIndex += 4;
                        if (relativeNodeIndex >= relevantNodesOnCurrentLevel) {
                            return jumpToNextLevel() && hasNext();
                        }
                    }
                }
                if (relativeNodeIndex < relevantNodesOnCurrentLevel) { //Is the next node on current level one of the children of the starting node?
                    currentSetIterator = nodes[currentFirstNodeIndex + relativeNodeIndex].colliders.iterator();
                    return hasNext();
                } else { //try to move to next level and, if it works, continue with new hasNext call
                    return jumpToNextLevel() && hasNext();
                }
            }
        }

        @Override
        public AbstractCollider next() {
            return currentSetIterator.next();
        }

        @Override
        public void remove() {
            currentSetIterator.remove();
        }

        protected boolean jumpToNextLevel() {
            currentFirstNodeIndex = currentFirstNodeIndex * 4 + 1;
            if (currentFirstNodeIndex > nodes.length) return false; //fail-fast if last level is reached
            relevantNodesOnCurrentLevel *= 4;
            relativeNodeIndex = 0;
            while (!nodeExists(currentFirstNodeIndex)) {
                currentFirstNodeIndex++;
                if (currentFirstNodeIndex > relevantNodesOnCurrentLevel) return false;
            }
            currentSetIterator = nodes[currentFirstNodeIndex].colliders.iterator();
            return true;
        }

    }

}
