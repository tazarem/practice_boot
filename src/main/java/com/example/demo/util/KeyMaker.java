package com.example.demo.util;
import org.apache.commons.lang3.ArrayUtils;

public class KeyMaker {
	public String makePk(int record_count, int code_length) {
	    String[] lowerCase = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"}; //36
	    String[] upperCase = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	    String[] numberCase = {"0","1","2","3","4","5","6","7","8","9"}; //10
	    
		String pk="";
		int r = 0;
		int p = record_count;
		String[] totalArray = ArrayUtils.addAll(lowerCase, upperCase);
		
		totalArray = ArrayUtils.addAll(totalArray, numberCase);
		
		do {
	        if(p>0){
	            pk = totalArray[p%totalArray.length]+pk;
	            p = (int) Math.floor(p/totalArray.length);
	        }else if(p==0){
	            pk=totalArray[p%totalArray.length]+pk;
	        }
			r++;
		}while(r<code_length);
		
	return pk;}
	
	public String getRandomStr(int size) {
		if(size > 0) {
			char[] tmp = new char[size];
			for(int i=0; i<tmp.length; i++) {
				int div = (int) Math.floor( Math.random() * 2 );
				
				if(div == 0) { // 0이면 숫자로
					tmp[i] = (char) (Math.random() * 10 + '0') ;
				}else { //1이면 알파벳
					tmp[i] = (char) (Math.random() * 26 + 'A') ;
				}
			}
			return new String(tmp);
		}
		return "ERROR : Size is required."; 
	}
}
