package com.johansvartdal.landlord;

public abstract class Challenge implements LandlordEventInterface {

    OnLandlordEventEndListener onLandlordEventEndListener;

    public void setOnEventEndListener(OnLandlordEventEndListener onEventEndListener) {
        this.onLandlordEventEndListener = onEventEndListener;
    }

    @Override
    public void eventEnded() {
        onLandlordEventEndListener.onEnd();
    }
}
