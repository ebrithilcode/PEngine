package com.pengine.rendering;

import com.pengine.IKillable;

public interface IRenderer extends Comparable<IRenderer>, IKillable {

    void render();

    boolean isVisible();

    void setVisible(boolean visible); //TODO: reevaluate

    byte getPriority();

    void onMessage(IRenderMessage message);

}
