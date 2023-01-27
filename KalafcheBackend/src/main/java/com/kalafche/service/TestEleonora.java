package com.kalafche.service;

public class TestEleonora {

	public static void main(String[] args) {
		System.out.println(contacenateWords("Eleonora", "Komitova"));
		
	}
	
	private static char findTheThirdCharacter(String test) {
		char character = test.charAt(3);
		
		return character;
	}
	
	private static String contacenateWords(String firstWord, String secondWord) {
		String result = firstWord + " " + secondWord;
		
		return result;
	}
	
}
