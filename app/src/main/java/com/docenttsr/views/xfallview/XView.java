package com.docenttsr.views.xfallview;


public class XView {

    private float posX;
    private float posY;

    private float pivotX;
    private float pivotY;

    private float velocityY;

    private float scale;

    private int rotateAngle;

    private int transparency;

    public XView(Builder builder) {
        posX = builder.posX;
        posY = builder.posY;
        velocityY = builder.velocityY;
        scale = builder.scale;
        rotateAngle = builder.rotateAngle;
        transparency = builder.transparency;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getPivotX() {
        return pivotX;
    }

    public void setPivotX(float pivotX) {
        this.pivotX = pivotX;
    }

    public float getPivotY() {
        return pivotY;
    }

    public void setPivotY(float pivotY) {
        this.pivotY = pivotY;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public int getRotateAngle() {
        return rotateAngle;
    }

    public void setRotateAngle(int rotateAngle) {
        this.rotateAngle = rotateAngle;
    }

    public int getTransparency() {
        return transparency;
    }

    public void setTransparency(int transparency) {
        this.transparency = transparency;
    }


    public static class Builder {

        private float posX;
        private float posY;

        private float pivotX;
        private float pivotY;

        private float velocityY;

        private float scale;

        private int rotateAngle;

        private int transparency;

        public Builder setPosX(float posX) {
            this.posX = posX;
            return this;
        }

        public Builder setPosY(float posY) {
            this.posY = posY;
            return this;
        }

        public Builder setPivotX(float pivotX) {
            this.pivotX = pivotX;
            return this;
        }

        public Builder setPivotY(float pivotY) {
            this.pivotY = pivotY;
            return this;
        }

        public Builder setVelocityY(float velocityY) {
            this.velocityY = velocityY;
            return this;
        }

        public Builder setScale(float scale) {
            this.scale = scale;
            return this;
        }

        public Builder setRotateAngle(int rotateAngle) {
            this.rotateAngle = rotateAngle;
            return this;
        }

        public Builder setTransparency(int transparency) {
            this.transparency = transparency;
            return this;
        }

        public XView create() {
            return new XView(this);
        }
    }

}
