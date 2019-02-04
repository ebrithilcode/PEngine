package com.pengine;

import java.util.List;
import java.util.ArrayList;
import com.pengine.InputInternet.Input;
import com.pengine.InputInternet.Data;

public class EngineList {
    List<GameObject> objectList;
    List<Input> inputData;
    List<Data> otherServerData;
    List<Data> otherClientData;

    public EngineList() {
        objectList = new ArrayList<GameObject>();
        inputData = new ArrayList<Input>();
        otherServerData = new ArrayList<Data>();
    }

    public List<GameObject> getObjects() {
        return objectList;
    }
    public List<Input> getInputs() {
        return inputData;
    }
    public void addObject(int pos, GameObject g) {
        objectList.add(pos, g);
    }

}