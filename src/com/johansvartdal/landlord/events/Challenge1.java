package com.johansvartdal.landlord.events;

import com.johansvartdal.landlord.Challenge;

public class Challenge1 extends Challenge {

    @Override
    public void startEvent() {
        this.eventEnded();
    }
}
