import java.util.List;
import java.util.ArrayList;

public class EngineList {
    List<GameObject> objectList;
    List<Input> inputData;
    List<Data> otherData;

    EngineList() {
        objectList = new ArrayList<GameObject>();
        inputData = new ArrayList<Input>();
        otherData = new ArrayList<Data>();
    }

    public List<GameObject> getObjects() {
        return objectList;
    }
    public List<Input> getInputs() {
        return inputData;
    }
    public void addObject(int pos, GameObject g) {
        objectList.add(i, g);
    }
}