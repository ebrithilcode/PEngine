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
        otherClientData = new ArrayList<Data>();
    }

    public List<GameObject> getObjects() {
        return objectList;
    }
    public List<Input> getInputs() {
        return inputData;
    }
    public List<Data> getServerData() { return otherServerData; }
    public List<Data> getClientData() { return otherClientData; }
    public void addObject(int pos, GameObject g) {
        objectList.add(pos, g);
    }
    public void addInput(Input i) { inputData.add(i); }
    public void addServerData(Data d) { otherServerData.add(d); }
    public void addClientData(Data d) { otherClientData.add(d); }

}