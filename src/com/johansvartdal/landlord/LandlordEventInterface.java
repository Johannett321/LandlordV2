package com.johansvartdal.landlord;

public interface LandlordEventInterface {

    void startEvent();
    void endEvent(Boolean cancelled);
    String getEventType();

    void resumeEvent();

}
