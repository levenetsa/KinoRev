package com.company;

public class Main {
    private static final String INPUT = "input_druz";
    private static final Integer N = 100;
    private static final Integer mN = 10;
    private static final Double F = 100D;
    private static final Double mF = 0.3;

    public static void main(String[] args) {
        ColCounter counter = new ColCounter(INPUT);
        counter.precountStats();
        //counter.printNMostOftenWords(1000);
        counter.countTenUslovn(mN, N, mF, F);
        counter.countTScore(mN, N, 2D, 4000D);
    }
}
