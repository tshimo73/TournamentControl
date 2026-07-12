/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package utils;


public class Random {
    public static int getRandomInt(int start, int end){
        return (int)(Math.random() * (end - start) + start);
    }
    
    public static double getRandomDouble(double start, double end){
        double r = Double.parseDouble(String.format("%.2f", (Math.random() * (end - start) + start)));
        
        return r;
    }
    
    public static int getZeroOrOne(){
        return (int) Math.round(Math.random());
    }
}
