package es.moldovan.givrsapp;

import java.util.Date;

/**
 * Created by OvidiuMircea on 26/02/2016.
 */
public class ProjectItem {

    /*data:
    *
    *  name: String,
     image: String,
     description: String,
     initiator: String,
     instructions: String,
     date: { type: Date, default: Date.now },
     location: {
       type: [Number],
       index: '2d'
     },
     items: [{
       name: String,
       gived: Boolean,
       givr: String
     }]*/
    String pName;
    String pImg;
    String pDescription;
    String pInitiator;
    String pInstructions;
    Date pDate;


}

