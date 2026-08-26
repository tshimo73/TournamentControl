/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ui;

import database.*;
import entities.*;
import enums.*;
import filing.*;
import games.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jframeconfig.*;
import matchmaking.*;
import players.*;
import tournaments.*;
import utils.*;

/*
PLAYER#33#Billy#Bergstrom#97535519#BLR#1045.19#N/A#0.0#0.0#
PLAYER#34#Nadene#Lakin#84005277#TLS#2485.19#IM#0.0#0.0#
PLAYER#35#Eduardo#Koepp#65466049#BGD#2159.15#NM#0.0#0.0#
PLAYER#36#Brady#Predovic#92081455#BDI#2225.51#CM#0.0#0.0#
PLAYER#37#Buford#Harber#86717689#MCO#2357.83#FM#0.0#0.0#
PLAYER#38#Rayford#Koepp#21402799#AUT#2180.75#NM#0.0#0.0#
PLAYER#39#Daron#Homenick#11016885#CHE#2391.27#FM#0.0#0.0#
PLAYER#40#Lincoln#Murazik#47397442#AUT#1688.54#N/A#0.0#0.0#
PLAYER#41#Daine#Renner#60134048#COL#2201.72#CM#0.0#0.0#
PLAYER#42#Ettie#Hudson#65655500#DEU#2431.35#IM#0.0#0.0#
PLAYER#43#German#Feil#76449293#MAR#1027.05#N/A#0.0#0.0#
PLAYER#44#Brett#Larson#14069124#TUN#2400.44#IM#0.0#0.0#
PLAYER#45#Louetta#Dickens#18818590#NLD#2319.37#FM#0.0#0.0#
PLAYER#46#Lemuel#Morissette#47014431#SLE#2265.46#CM#0.0#0.0#
PLAYER#47#Natividad#Aufderhar#18381069#MCO#2323.47#FM#0.0#0.0#
PLAYER#48#Irving#Kerluke#76557541#GRC#2417.81#IM#0.0#0.0#
PLAYER#49#Silas#Quigley#65163787#TON#2254.11#CM#0.0#0.0#
PLAYER#50#Dane#Kutch#54563320#SDN#2203.59#CM#0.0#0.0#
PLAYER#51#Dominick#Gottlieb#19222783#MOZ#2310.63#FM#0.0#0.0#
PLAYER#52#Jesusa#Zboncak#63314369#AZE#2325.61#FM#0.0#0.0#
PLAYER#53#Graig#Lemke#6612350#BGR#2184.39#NM#0.0#0.0#
PLAYER#54#Deanne#Moore#57782292#PRT#2423.9#IM#0.0#0.0#
*/
public class MainTerminal {
    public static void main(String[] args){
        DatabaseManager.init();
        
        PlayerEntity pe = new PlayerEntity();
        int id = 52;
        HashMap<String, Object> attrs = new HashMap<>();
        
        attrs.put(PlayerFields.FIRST_NAME.toString(), "Tshimo");
        attrs.put(PlayerFields.LAST_NAME.toString(), "von Doom");
        
        Player j = pe.update(id, attrs);
        System.out.println(j);
        
        DatabaseManager.closeConn();
    }
}
