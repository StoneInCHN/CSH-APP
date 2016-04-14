package com.cheweishi.android.entity;

import com.cheweishi.android.response.BaseResponse;

/**
 * Created by tangce on 4/6/2016.
 */
public class CarDetectionResponse extends BaseResponse {


    /**
     * averageSpeed : 11.443515
     * mileAge : 0
     * averageFuelConsumption : 3.2788703
     * cost : 0
     * runningTime : 0
     * fuelConsumption : 0
     * totalMileAge : 0
     */

    private MsgBean msg;

    public MsgBean getMsg() {
        return msg;
    }

    public void setMsg(MsgBean msg) {
        this.msg = msg;
    }

    public static class MsgBean {
        private double averageSpeed;
        private int mileAge;
        private double averageFuelConsumption;
        private int cost;
        private int runningTime;
        private int fuelConsumption;
        private int totalMileAge;

        public double getAverageSpeed() {
            return averageSpeed;
        }

        public void setAverageSpeed(double averageSpeed) {
            this.averageSpeed = averageSpeed;
        }

        public int getMileAge() {
            return mileAge;
        }

        public void setMileAge(int mileAge) {
            this.mileAge = mileAge;
        }

        public double getAverageFuelConsumption() {
            return averageFuelConsumption;
        }

        public void setAverageFuelConsumption(double averageFuelConsumption) {
            this.averageFuelConsumption = averageFuelConsumption;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public int getRunningTime() {
            return runningTime;
        }

        public void setRunningTime(int runningTime) {
            this.runningTime = runningTime;
        }

        public int getFuelConsumption() {
            return fuelConsumption;
        }

        public void setFuelConsumption(int fuelConsumption) {
            this.fuelConsumption = fuelConsumption;
        }

        public int getTotalMileAge() {
            return totalMileAge;
        }

        public void setTotalMileAge(int totalMileAge) {
            this.totalMileAge = totalMileAge;
        }
    }
}
