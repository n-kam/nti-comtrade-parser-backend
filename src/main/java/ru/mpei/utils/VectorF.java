package ru.mpei.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VectorF {

    /**
     * Модуль вектора
     */
    private double mag;

    /**
     * Угол вектора, град [-180 : 180]
     */
    private double ang;

    /**
     * Ортогональная составляющая, ось Х
     */
    private double ortX;

    /**
     * Ортогональная составляющая, ось Y
     */
    private double ortY;

    /**
     * Задать вектор с помощью амплитуды и угла (градусы)
     */
    public void setMagAngDeg(double mag, double ang) {
        this.mag = mag;
        this.ang = ang;
        double rad = Math.toRadians(ang);
        this.ortX = mag * Math.cos(rad);
        this.ortY = mag * Math.sin(rad);
    }

    /**
     * Задать вектор с помощью амплитуды и угла (радианы)
     */
    public void setMagAngRad(double mag, double ang) {
        this.mag = mag;
        this.ang = Math.toDegrees(ang);
        this.ortX = mag * Math.cos(ang);
        this.ortY = mag * Math.sin(ang);
    }

    /**
     * Задать вектор с помощью ортогональных составляющих
     */
    public void setOrtPair(double ortX, double ortY) {
        this.ortX = ortX;
        this.ortY = ortY;
        this.mag = Math.hypot(ortX, ortY);
        this.ang = Math.toDegrees(Math.atan2(ortY, ortX));
    }
}
