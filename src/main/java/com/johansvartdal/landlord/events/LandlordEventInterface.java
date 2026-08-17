package com.johansvartdal.landlord.events;

public interface LandlordEventInterface {

    void startEvent();
    void endEvent(Boolean cancelled);
    String getEventType();

    void resumeEvent();
    void prepareEvent();

}
