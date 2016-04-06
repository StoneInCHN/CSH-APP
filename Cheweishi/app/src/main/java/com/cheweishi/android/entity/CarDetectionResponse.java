package com.cheweishi.android.entity;

import com.cheweishi.android.response.BaseResponse;

/**
 * Created by tangce on 4/6/2016.
 */
public class CarDetectionResponse extends BaseResponse {

    /**
     * totalMileAge : 0
     * fuelConsumption : -30
     * averageSpeed : 0
     * averageFuelConsumption : 0
     * mileAge : -1000
     * runningTime : 0
     * cost : 0
     */

    private MsgBean msg;

    public MsgBean getMsg() {
        return msg;
    }

    public void setMsg(MsgBean msg) {
        this.msg = msg;
    }

    public static class MsgBean {
        private int totalMileAge;
        private int fuelConsumption;
        private int averageSpeed;
        private int averageFuelConsumption;
        private int mileAge;
        private int runningTime;
        private int cost;

        public int getTotalMileAge() {
            return totalMileAge;
        }

        public void setTotalMileAge(int totalMileAge) {
            this.totalMileAge = totalMileAge;
        }

        public int getFuelConsumption() {
            return fuelConsumption;
        }

        public void setFuelConsumption(int fuelConsumption) {
            this.fuelConsumption = fuelConsumption;
        }

        public int getAverageSpeed() {
            return averageSpeed;
        }

        public void setAverageSpeed(int averageSpeed) {
            this.averageSpeed = averageSpeed;
        }

        public int getAverageFuelConsumption() {
            return averageFuelConsumption;
        }

        public void setAverageFuelConsumption(int averageFuelConsumption) {
            this.averageFuelConsumption = averageFuelConsumption;
        }

        public int getMileAge() {
            return mileAge;
        }

        public void setMileAge(int mileAge) {
            this.mileAge = mileAge;
        }

        public int getRunningTime() {
            return runningTime;
        }

        public void setRunningTime(int runningTime) {
            this.runningTime = runningTime;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }
    }
}
