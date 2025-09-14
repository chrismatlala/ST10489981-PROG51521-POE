/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package userauthenticationsystem;

/**
 *
 * @author chris
 */
public class Response {
    String response = "Isee you there";
    
    public String returnResponse(){
        return response;
        
        
    }
    public int calculateNumResponses(int initialResponseCount,int newResponses){
        
        int totalresponses = initialResponseCount + newResponses;
        
        return totalresponses;
    }
    
}
