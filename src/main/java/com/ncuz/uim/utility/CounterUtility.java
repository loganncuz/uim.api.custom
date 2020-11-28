package com.ncuz.uim.utility;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class CounterUtility {

    private String metricsPath;

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public int getMetricsCounter(String txtFile){
        int result=0;
        FileInputStream inputStream = null;
        String alarmConter = null;
        metricsPath=txtFile;
        try {
              inputStream = new FileInputStream(txtFile);
        } catch (FileNotFoundException e) {
           // e.printStackTrace();
            System.out.println("FileNotFoundException :"+e.getMessage());
        }
//        System.out.println("inputStream :"+inputStream+" | "+txtFile);
        if(inputStream!=null)
        try {

              alarmConter = IOUtils.toString(inputStream);
//            System.out.println("getMetricsCounter IOUtils.toString(inputStream) :"+alarmConter);
              if(alarmConter!=null){
//                  System.out.println("getMetricsCounter isInteger(alarmConter.trim()) :"+isInteger(alarmConter.trim()));
                  if(isInteger(alarmConter.trim())){
                      result=Integer.parseInt(alarmConter.trim());
                  }
              }


        } catch (IOException e) {
            System.out.println("IOException :"+e.getMessage());
//            e.printStackTrace();
        }
        System.out.println("getMetricsCounter :"+alarmConter+" | "+result);
        try {
            if(inputStream !=null)
                inputStream.close();
        } catch (IOException e) {
            System.out.println("IOException :"+e.getMessage());
//            e.printStackTrace();
        }
        return result;
    }

    public void setMetricsCounter(int counter){
        FileWriter writer = null;
        System.out.println("setMetricsCounter :"+counter);
        try {
            writer = new FileWriter(metricsPath);
            writer.write(String.valueOf(counter));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    };
}
