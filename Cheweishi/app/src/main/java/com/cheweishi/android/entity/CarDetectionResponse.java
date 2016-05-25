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
        private float mileAge;
        private double averageFuelConsumption;
        private double cost;
        private int runningTime;
        private double fuelConsumption;
        private float totalMileAge;

        public double getAverageSpeed() {
            return averageSpeed;
        }

        public void setAverageSpeed(double averageSpeed) {
            this.averageSpeed = averageSpeed;
        }

        public float getMileAge() {
            return mileAge;
        }

        public void setMileAge(float mileAge) {
            this.mileAge = mileAge;
        }

        public double getAverageFuelConsumption() {
            return averageFuelConsumption;
        }

        public void setAverageFuelConsumption(double averageFuelConsumption) {
            this.averageFuelConsumption = averageFuelConsumption;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public int getRunningTime() {
            return runningTime;
        }

        public void setRunningTime(int runningTime) {
            this.runningTime = runningTime;
        }

        public double getFuelConsumption() {
            return fuelConsumption;
        }

        public void setFuelConsumption(double fuelConsumption) {
            this.fuelConsumption = fuelConsumption;
        }

        public float getTotalMileAge() {
            return totalMileAge;
        }

        public void setTotalMileAge(float totalMileAge) {
            this.totalMileAge = totalMileAge;
        }
    }
}
