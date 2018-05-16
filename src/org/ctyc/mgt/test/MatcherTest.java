package org.ctyc.mgt.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class MatcherTest extends TestCase {
	
	public void testMatcher(){
		
		String testString = "94125415 家長:91234567";
		
		Pattern pattern = Pattern.compile("家長[ ]*:*[ ]*\\d*");
		Matcher matcher = pattern.matcher(testString);
		
		while (matcher.find()) {

            System.out.println("matcher.group():\t" + matcher.group());

        }
		
	}
	
	public void testReplaceAll(){
		
		String testString = "94125415 家長91234567";
		
		testString = testString.replaceAll("\\家長[ ]*：*:*[ ]*", "家長：");
		System.out.println("Test String:\t" + testString);
	}
	
	public void testReplaceAll2(){
		
		String originString = "1234\\r\\n5436";
		
		String testString = originString.replaceAll("\\\\r\\\\n", " ");
		System.out.println("Origin String:\t" + originString);
		System.out.println("Test String:\t" + testString);
	}
}
